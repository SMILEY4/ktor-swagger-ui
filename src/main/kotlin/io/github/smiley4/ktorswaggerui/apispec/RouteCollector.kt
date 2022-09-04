package io.github.smiley4.ktorswaggerui.apispec

import io.github.smiley4.ktorswaggerui.documentation.DocumentedRouteSelector
import io.github.smiley4.ktorswaggerui.documentation.RouteDocumentation
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.plugin
import io.ktor.server.auth.AuthenticationRouteSelector
import io.ktor.server.routing.HttpMethodRouteSelector
import io.ktor.server.routing.RootRouteSelector
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.TrailingSlashRouteSelector

class RouteCollector {

    fun collectRoutes(application: Application): Sequence<RouteMeta> {
        return allRoutes(application.plugin(Routing))
            .asSequence()
            .map { route ->
                RouteMeta(
                    route = route,
                    method = getMethod(route),
                    path = getPath(route),
                    documentation = getDocumentation(route, RouteDocumentation()),
                    protected = isProtected(route)
                )
            }
    }

    private fun getDocumentation(route: Route, base: RouteDocumentation): RouteDocumentation {
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

    private fun getPath(route: Route): String {
        return when (route.selector) {
            is TrailingSlashRouteSelector -> "/"
            is RootRouteSelector -> ""
            is DocumentedRouteSelector -> route.parent?.let { getPath(it) } ?: ""
            is HttpMethodRouteSelector -> route.parent?.let { getPath(it) } ?: ""
            is AuthenticationRouteSelector -> route.parent?.let { getPath(it) } ?: ""
            else -> (route.parent?.let { getPath(it) } ?: "") + "/" + route.selector.toString()
        }
    }

    private fun isProtected(route: Route): Boolean {
        return when (route.selector) {
            is TrailingSlashRouteSelector -> false
            is RootRouteSelector -> false
            is DocumentedRouteSelector -> route.parent?.let { isProtected(it) } ?: false
            is HttpMethodRouteSelector -> route.parent?.let { isProtected(it) } ?: false
            is AuthenticationRouteSelector -> true
            else -> route.parent?.let { isProtected(it) } ?: false
        }
    }

    private fun allRoutes(root: Route): List<Route> {
        return (listOf(root) + root.children.flatMap { allRoutes(it) })
            .filter { it.selector is HttpMethodRouteSelector }
    }


    private fun merge(a: RouteDocumentation, b: RouteDocumentation): RouteDocumentation {
        return RouteDocumentation().apply {
            tags = mutableListOf<String>().also {
                it.addAll(a.tags)
                it.addAll(b.tags)
            }
            summary = a.summary ?: b.summary
            description = a.description ?: b.description
            securitySchemeName = a.securitySchemeName ?: b.securitySchemeName
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