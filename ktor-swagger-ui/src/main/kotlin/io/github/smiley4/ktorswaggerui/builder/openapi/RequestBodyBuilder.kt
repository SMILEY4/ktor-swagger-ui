package io.github.smiley4.ktorswaggerui.builder.openapi

import io.github.smiley4.ktorswaggerui.data.OpenApiBaseBodyData
import io.swagger.v3.oas.models.parameters.RequestBody

/**
 * Build the openapi [RequestBody]-object. Holds information describing a single request body.
 * See [OpenAPI Specification - Request Body Object](https://swagger.io/specification/#request-body-object).
 */
class RequestBodyBuilder(
    private val contentBuilder: ContentBuilder
) {

    fun build(body: OpenApiBaseBodyData): RequestBody =
        RequestBody().also {
            it.description = body.description
            it.required = body.required
            it.content = contentBuilder.build(body)
        }

}
