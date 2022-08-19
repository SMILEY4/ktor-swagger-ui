package de.lruegner.ktorswaggerui.apispec

import de.lruegner.ktorswaggerui.documentation.RouteDocumentation
import io.ktor.http.HttpMethod
import io.ktor.server.routing.Route

data class RouteMeta(
        val route: Route,
        val path: String,
        val method: HttpMethod,
        val documentation: RouteDocumentation,
        val protected: Boolean
    )