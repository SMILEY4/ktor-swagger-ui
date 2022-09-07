package io.github.smiley4.ktorswaggerui.routes

import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingResolveContext

class DocumentedRouteSelector(val documentation: OpenApiRoute) : RouteSelector() {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int) = RouteSelectorEvaluation.Transparent
}

fun Route.documentation(
    documentation: OpenApiRoute.() -> Unit = { },
    build: Route.() -> Unit
): Route {
    val documentedRoute = createChild(DocumentedRouteSelector(OpenApiRoute().apply(documentation)))
    documentedRoute.build()
    return documentedRoute
}

fun Route.documentation(
    documentation: OpenApiRoute,
    build: Route.() -> Unit
): Route {
    val documentedRoute = createChild(DocumentedRouteSelector(documentation))
    documentedRoute.build()
    return documentedRoute
}