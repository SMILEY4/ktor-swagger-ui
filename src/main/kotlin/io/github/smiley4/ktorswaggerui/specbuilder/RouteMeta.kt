package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.ktor.http.HttpMethod
import io.ktor.server.routing.Route

data class RouteMeta(
    val route: Route,
    val path: String,
    val method: HttpMethod,
    val documentation: OpenApiRoute,
    val protected: Boolean
    )