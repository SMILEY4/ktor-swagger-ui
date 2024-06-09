package io.github.smiley4.ktorswaggerui.dsl.routes

import io.github.smiley4.ktorswaggerui.data.OpenApiBaseBodyData
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker
import io.ktor.http.ContentType

/**
 * Describes a single request/response body with a single schema.
 */
@OpenApiDslMarker
sealed class OpenApiBaseBody {

    /**
     * A brief description of the request body
     */
    var description: String? = null

    /**
     * Determines if the request body is required in the request
     */
    var required: Boolean? = null

    /**
     * Allowed Media Types for this body. If none specified, a media type will be chosen automatically based on the provided schema
     */
    var mediaTypes: Collection<ContentType> = emptySet()

    abstract fun build(): OpenApiBaseBodyData
}
