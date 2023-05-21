package io.github.smiley4.ktorswaggerui.spec.route

import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.ktor.http.HttpMethod

/**
 * Information about a route
 */
data class RouteMeta(
    val path: String,
    val method: HttpMethod,
    val documentation: OpenApiRoute,
    val protected: Boolean
)
