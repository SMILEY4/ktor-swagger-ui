package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.ktor.http.HttpMethod
import io.ktor.server.routing.Route

/**
 * Information about a route
 */
data class RouteMeta(
    val path: String,
    val method: HttpMethod,
    val documentation: OpenApiRoute,
    val protected: Boolean,
    val route: Route? = null,
)
