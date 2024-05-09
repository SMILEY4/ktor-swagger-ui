package io.github.smiley4.ktorswaggerui.builder.openapi

import io.github.smiley4.ktorswaggerui.data.OpenApiResponseData
import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.ktor.http.HttpStatusCode
import io.swagger.v3.oas.models.responses.ApiResponses

class ResponsesBuilder(
    private val responseBuilder: ResponseBuilder,
    private val config: PluginConfigData
) {

    fun build(responses: List<OpenApiResponseData>, isProtected: Boolean): ApiResponses =
        ApiResponses().also {
            responses
                .map { response -> responseBuilder.build(response) }
                .forEach { (name, response) -> it.addApiResponse(name, response) }
            if (shouldAddUnauthorized(responses, isProtected)) {
                config.securityConfig.defaultUnauthorizedResponse
                    ?.let { response -> responseBuilder.build(response) }
                    ?.also { (name, response) -> it.addApiResponse(name, response) }
            }
        }

    private fun shouldAddUnauthorized(responses: List<OpenApiResponseData>, isProtected: Boolean): Boolean {
        val unauthorizedCode = HttpStatusCode.Unauthorized.value.toString();
        return config.securityConfig.defaultUnauthorizedResponse != null
                && isProtected
                && responses.count { it.statusCode == unauthorizedCode } == 0
    }

}
