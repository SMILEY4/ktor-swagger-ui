package io.github.smiley4.ktorswaggerui.builder.openapi

import io.github.smiley4.ktorswaggerui.data.OpenApiResponseData
import io.swagger.v3.oas.models.responses.ApiResponse

class ResponseBuilder(
    private val headerBuilder: HeaderBuilder,
    private val contentBuilder: ContentBuilder
) {

    fun build(response: OpenApiResponseData): Pair<String, ApiResponse> =
        response.statusCode to ApiResponse().also {
            it.description = response.description
            it.headers = response.headers.mapValues { header -> headerBuilder.build(header.value) }
            response.body?.let { body ->
                it.content = contentBuilder.build(body)
            }
        }

}
