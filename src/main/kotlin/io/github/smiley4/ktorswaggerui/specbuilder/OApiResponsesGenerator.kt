package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.dsl.OpenApiResponse
import io.swagger.v3.oas.models.headers.Header
import io.swagger.v3.oas.models.responses.ApiResponse

/**
 * Generator for the OpenAPI Responses
 */
class OApiResponsesGenerator {

    /**
     * Generate the Responses from the given configs
     */
    fun generate(configs: List<OpenApiResponse>, componentCtx: ComponentsContext): List<Pair<String, ApiResponse>> {
        return configs.map { responseCfg ->
            responseCfg.statusCode.value.toString() to ApiResponse().apply {
                description = responseCfg.description
                responseCfg.getBody()?.let {
                    content = OApiContentGenerator().generate(responseCfg.getBody()!!, componentCtx)
                }
                headers = responseCfg.getHeaders().mapValues {
                    Header().apply {
                        description = it.value.description
                        required = it.value.required
                        deprecated = it.value.deprecated
                        schema = it.value.schema?.let { s -> OApiSchemaGenerator().generate(s, ComponentsContext.NOOP) }
                    }
                }
            }
        }
    }

}