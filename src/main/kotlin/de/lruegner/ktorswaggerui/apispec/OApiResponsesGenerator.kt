package de.lruegner.ktorswaggerui.apispec

import de.lruegner.ktorswaggerui.documentation.RouteResponse
import io.ktor.http.HttpStatusCode
import io.swagger.v3.oas.models.responses.ApiResponse

/**
 * Generator for the OpenAPI Responses
 */
class OApiResponsesGenerator {

    /**
     * Generate the Respones from the given configs
     */
    fun generate(configs: List<RouteResponse>): List<Pair<String, ApiResponse>> {
        return configs.map { responseCfg ->
            responseCfg.statusCode.toString() to ApiResponse().apply {
                description = responseCfg.description
                responseCfg.body?.let {
                    content = OApiContentGenerator().generate(responseCfg.body!!)
                }
            }
        }
    }

}