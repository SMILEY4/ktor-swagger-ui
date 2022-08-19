package io.github.smiley4.ktorswaggerui.apispec

import io.github.smiley4.ktorswaggerui.documentation.RouteDocumentation
import io.ktor.http.HttpMethod
import io.ktor.server.routing.Route

data class RouteMeta(
        val route: Route,
        val path: String,
        val method: HttpMethod,
        val documentation: RouteDocumentation,
        val protected: Boolean
    )