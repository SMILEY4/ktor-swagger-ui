package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.DocumentedRouteSelector
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.plugin
import io.ktor.server.auth.AuthenticationRouteSelector
import io.ktor.server.routing.HttpMethodRouteSelector
import io.ktor.server.routing.RootRouteSelector
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.Routing
import io.ktor.server.routing.TrailingSlashRouteSelector
import kotlin.reflect.full.isSubclassOf

class RouteCollector {

    /**
     * Collect all routes from the given application
     */
    fun collectRoutes(application: Application, config: SwaggerUIPluginConfig): Sequence<RouteMeta> {
        return allRoutes(application.plugin(Routing))
            .asSequence()
            .map { route ->
                RouteMeta(
                    method = getMethod(route),
                    path = getPath(route, config),
                    documentation = getDocumentation(route, OpenApiRoute()),
                    protected = isProtected(route)
                )
            }
    }

    private fun getDocumentation(route: Route, base: OpenApiRoute): OpenApiRoute {
        var documentation = base
        if (route.selector is DocumentedRouteSelector) {
            documentation = merge(documentation, (route.selector as DocumentedRouteSelector).documentation)
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

    private fun merge(a: OpenApiRoute, b: OpenApiRoute): OpenApiRoute {
        return OpenApiRoute().apply {
            tags = mutableListOf<String>().also {
                it.addAll(a.tags)
                it.addAll(b.tags)
            }
            summary = a.summary ?: b.summary
            description = a.description ?: b.description
            operationId = a.operationId ?: b.operationId
            securitySchemeName = a.securitySchemeName ?: b.securitySchemeName
            deprecated = a.deprecated || b.deprecated
            request {
                (getParameters() as MutableList).also {
                    it.addAll(a.getRequest().getParameters())
                    it.addAll(b.getRequest().getParameters())
                }
                setBody(a.getRequest().getBody() ?: b.getRequest().getBody())
            }
            response {
                b.getResponses().getResponses().forEach { response -> addResponse(response) }
                a.getResponses().getResponses().forEach { response -> addResponse(response) }
            }
        }
    }

}
