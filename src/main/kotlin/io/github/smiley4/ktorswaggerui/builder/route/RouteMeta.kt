package io.github.smiley4.ktorswaggerui.builder.route

import io.github.smiley4.ktorswaggerui.data.OpenApiRouteData
import io.ktor.http.HttpMethod

/**
 * Information about a route
 */
data class RouteMeta(
    val path: String,
    val method: HttpMethod,
    val documentation: OpenApiRouteData,
    val protected: Boolean
)
