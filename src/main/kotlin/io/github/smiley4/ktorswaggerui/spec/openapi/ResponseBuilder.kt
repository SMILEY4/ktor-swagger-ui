package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.dsl.OpenApiResponse
import io.swagger.v3.oas.models.responses.ApiResponse

class ResponseBuilder(
    private val headerBuilder: HeaderBuilder,
    private val contentBuilder: ContentBuilder
) {

    fun build(response: OpenApiResponse): Pair<String, ApiResponse> =
        response.statusCode to ApiResponse().also {
            it.description = response.description
            it.headers = response.getHeaders().mapValues { header -> headerBuilder.build(header.value) }
            response.getBody()?.let { body ->
                it.content = contentBuilder.build(body)
            }
        }

}