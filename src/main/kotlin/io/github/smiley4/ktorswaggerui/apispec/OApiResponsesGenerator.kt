package io.github.smiley4.ktorswaggerui.apispec

import io.github.smiley4.ktorswaggerui.documentation.SingleResponseDocumentation
import io.swagger.v3.oas.models.responses.ApiResponse

/**
 * Generator for the OpenAPI Responses
 */
class OApiResponsesGenerator {

    /**
     * Generate the Respones from the given configs
     */
    fun generate(configs: List<SingleResponseDocumentation>): List<Pair<String, ApiResponse>> {
        return configs.map { responseCfg ->
            responseCfg.statusCode.toString() to ApiResponse().apply {
                description = responseCfg.description
                responseCfg.getBody()?.let {
                    content = OApiContentGenerator().generate(responseCfg.getBody()!!)
                }
            }
        }
    }

}