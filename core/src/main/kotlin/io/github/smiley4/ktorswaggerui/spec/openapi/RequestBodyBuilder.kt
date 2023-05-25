package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.dsl.OpenApiBaseBody
import io.swagger.v3.oas.models.parameters.RequestBody

class RequestBodyBuilder(
    private val contentBuilder: ContentBuilder
) {

    fun build(body: OpenApiBaseBody): RequestBody =
        RequestBody().also {
            it.description = body.description
            it.required = body.required
            it.content = contentBuilder.build(body)
        }

}