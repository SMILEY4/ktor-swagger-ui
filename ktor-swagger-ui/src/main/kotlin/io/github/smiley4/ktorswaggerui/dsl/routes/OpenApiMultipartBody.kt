package io.github.smiley4.ktorswaggerui.dsl.routes

import io.github.smiley4.ktorswaggerui.data.OpenApiMultipartBodyData
import io.github.smiley4.ktorswaggerui.data.TypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker


/**
 * Describes a single request/response body with multipart content.
 * See https://swagger.io/docs/specification/describing-request-body/multipart-requests/ for more info
 */
@OpenApiDslMarker
class OpenApiMultipartBody : OpenApiBaseBody() {

    private val parts = mutableListOf<OpenApiMultipartPart>()

    /**
     * One part of a multipart-body
     */
    fun part(name: String, type: TypeDescriptor, block: OpenApiMultipartPart.() -> Unit) {
        parts.add(OpenApiMultipartPart(name, type).apply(block))
    }

    override fun build() = OpenApiMultipartBodyData(
        description = description,
        required = required ?: false,
        mediaTypes = mediaTypes,
        parts = parts.map { it.build() }
    )
}
