package io.github.smiley4.ktorswaggerui.builder.openapi

import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.github.smiley4.ktorswaggerui.dsl.OpenApiResponses
import io.ktor.http.HttpStatusCode
import io.swagger.v3.oas.models.responses.ApiResponses

class ResponsesBuilder(
    private val responseBuilder: ResponseBuilder,
    private val config: PluginConfigData
) {

    fun build(responses: OpenApiResponses, isProtected: Boolean): ApiResponses =
        ApiResponses().also {
            responses.getResponses()
                .map { response -> responseBuilder.build(response) }
                .forEach { (name, response) -> it.addApiResponse(name, response) }
            if (shouldAddUnauthorized(responses, isProtected)) {
                config.defaultUnauthorizedResponse
                    ?.let { response -> responseBuilder.build(response) }
                    ?.also { (name, response) -> it.addApiResponse(name, response) }
            }
        }

    private fun shouldAddUnauthorized(responses: OpenApiResponses, isProtected: Boolean): Boolean {
        val unauthorizedCode = HttpStatusCode.Unauthorized.value.toString();
        return config.defaultUnauthorizedResponse != null
                && isProtected
                && responses.getResponses().count { it.statusCode == unauthorizedCode } == 0
    }

}
