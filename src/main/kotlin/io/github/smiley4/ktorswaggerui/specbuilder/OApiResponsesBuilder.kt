package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.OpenApiResponse
import io.swagger.v3.oas.models.responses.ApiResponse

/**
 * Builder for the OpenAPI Responses
 */
class OApiResponsesBuilder {

    private val contentBuilder = OApiContentBuilder()
    private val headerBuilder = OApiHeaderBuilder()

    fun build(responses: List<OpenApiResponse>, components: ComponentsContext, config: SwaggerUIPluginConfig): List<Pair<String, ApiResponse>> {
        return responses.map { responseCfg ->
            responseCfg.statusCode to ApiResponse().apply {
                description = responseCfg.description
                responseCfg.getBody()?.let {
                    content = contentBuilder.build(responseCfg.getBody()!!, components, config)
                }
                headers = responseCfg.getHeaders().mapValues { headerBuilder.build(it.value, config) }
            }
        }
    }

}