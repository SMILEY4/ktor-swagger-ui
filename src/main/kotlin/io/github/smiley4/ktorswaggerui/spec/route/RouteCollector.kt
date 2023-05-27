package io.github.smiley4.ktorswaggerui.spec.route

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.DocumentedRouteSelector
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.ktor.http.HttpMethod
import io.ktor.server.auth.AuthenticationRouteSelector
import io.ktor.server.routing.HttpMethodRouteSelector
import io.ktor.server.routing.RootRouteSelector
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.TrailingSlashRouteSelector
import kotlin.reflect.full.isSubclassOf

class RouteCollector(
    private val routeDocumentationMerger: RouteDocumentationMerger
) {

    /**
     * Collect all routes from the given application
     */
    fun collectRoutes(routeProvider: () -> Route, config: SwaggerUIPluginConfig): Sequence<RouteMeta> {
        return allRoutes(routeProvider())
            .asSequence()
            .map { route ->
                RouteMeta(
                    method = getMethod(route),
                    path = getPath(route, config),
                    documentation = getDocumentation(route, OpenApiRoute()),
                    protected = isProtected(route)
                )
            }
            .filter { removeLeadingSlash(it.path) != removeLeadingSlash(config.getSwaggerUI().swaggerUrl) }
            .filter { removeLeadingSlash(it.path) != removeLeadingSlash("${config.getSwaggerUI().swaggerUrl}/api.json") }
            .filter { removeLeadingSlash(it.path) != removeLeadingSlash("${config.getSwaggerUI().swaggerUrl}/{filename}") }
            .filter { !config.getSwaggerUI().forwardRoot || it.path != "/" }
            .filter { !it.documentation.hidden }
            .filter { path ->
                config.pathFilter
                    ?.let { it(path.method, path.path.split("/").filter { it.isNotEmpty() }) }
                    ?: true
            }
    }

    private fun removeLeadingSlash(str: String): String =
        if (str.startsWith("/")) {
            str.substring(1)
        } else {
            str
        }

    private fun getDocumentation(route: Route, base: OpenApiRoute): OpenApiRoute {
        var documentation = base
        if (route.selector is DocumentedRouteSelector) {
            documentation = routeDocumentationMerger.merge(documentation, (route.selector as DocumentedRouteSelector).documentation)
        }
        return if (route.parent != null) {
            getDocumentation(route.parent!!, documentation)
        } else {
            documentation
        }
    }

    private fun getMethod(route: Route): HttpMethod {
        return (route.selector as HttpMethodRouteSelector).method
    }

    private fun getPath(route: Route, config: SwaggerUIPluginConfig): String {
        val selector = route.selector
        return if (isIgnoredSelector(selector, config)) {
            route.parent?.let { getPath(it, config) } ?: ""
        } else {
            when (route.selector) {
                is TrailingSlashRouteSelector -> "/"
                is RootRouteSelector -> ""
                is DocumentedRouteSelector -> route.parent?.let { getPath(it, config) } ?: ""
                is HttpMethodRouteSelector -> route.parent?.let { getPath(it, config) } ?: ""
                is AuthenticationRouteSelector -> route.parent?.let { getPath(it, config) } ?: ""
                else -> (route.parent?.let { getPath(it, config) } ?: "") + "/" + route.selector.toString()
            }
        }
    }

    private fun isIgnoredSelector(selector: RouteSelector, config: SwaggerUIPluginConfig): Boolean {
        return when (selector) {
            is TrailingSlashRouteSelector -> false
            is RootRouteSelector -> false
            is DocumentedRouteSelector -> true
            is HttpMethodRouteSelector -> true
            is AuthenticationRouteSelector -> true
            else -> config.ignoredRouteSelectors.any { selector::class.isSubclassOf(it) }
        }
    }

    private fun isProtected(route: Route): Boolean {
        return when (route.selector) {
            is AuthenticationRouteSelector -> true
            is TrailingSlashRouteSelector -> false
            is RootRouteSelector -> false
            is DocumentedRouteSelector -> route.parent?.let { isProtected(it) } ?: false
            is HttpMethodRouteSelector -> route.parent?.let { isProtected(it) } ?: false
            else -> route.parent?.let { isProtected(it) } ?: false
        }
    }

    private fun allRoutes(root: Route): List<Route> {
        return (listOf(root) + root.children.flatMap { allRoutes(it) })
            .filter { it.selector is HttpMethodRouteSelector }
    }


}
