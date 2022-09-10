package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.dsl.OpenApiResponse
import io.swagger.v3.oas.models.headers.Header
import io.swagger.v3.oas.models.responses.ApiResponse

/**
 * Builder for the OpenAPI Responses
 */
class OApiResponsesBuilder(
    private val contentBuilder: OApiContentBuilder,
    private val schemaBuilder: OApiSchemaBuilder
) {

    fun build(responses: List<OpenApiResponse>, components: ComponentsContext): List<Pair<String, ApiResponse>> {
        return responses.map { responseCfg ->
            responseCfg.statusCode.value.toString() to ApiResponse().apply {
                description = responseCfg.description
                responseCfg.getBody()?.let {
                    content = contentBuilder.build(responseCfg.getBody()!!, components)
                }
                headers = responseCfg.getHeaders().mapValues {
                    Header().apply {
                        description = it.value.description
                        required = it.value.required
                        deprecated = it.value.deprecated
                        schema = it.value.type?.let { t -> schemaBuilder.build(t, ComponentsContext.NOOP) }
                    }
                }
            }
        }
    }

}