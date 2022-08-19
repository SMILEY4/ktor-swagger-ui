package de.lruegner.ktorswaggerui.apispec

import de.lruegner.ktorswaggerui.documentation.RouteResponse
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.oas.models.security.SecurityRequirement

/**
 * Generator for a single OpenAPI Path
 */
class OApiPathGenerator {

    /**
     * Generate the OpenAPI Path-Item from the given config
     */
    fun generate(config: RouteMeta, autoUnauthorizedResponses: Boolean, defaultSecurityScheme: String?): Pair<String, PathItem> {
        return config.path to PathItem().apply {
            val operation = Operation().apply {
                tags = config.documentation.tags
                summary = config.documentation.summary
                description = config.documentation.description
                parameters = OApiParametersGenerator().generate(config.documentation.getParameters())
                config.documentation.getRequestBody()?.let {
                    requestBody = OApiRequestBodyGenerator().generate(it)
                }
                responses = ApiResponses().apply {
                    if (shouldAddUnauthorized(config, autoUnauthorizedResponses)) {
                        OApiResponsesGenerator().generate(listOf(defaultUnauthorizedResponse())).forEach {
                            addApiResponse(it.first, it.second)
                        }
                    }
                    OApiResponsesGenerator().generate(config.documentation.getResponses()).forEach {
                        addApiResponse(it.first, it.second)
                    }
                }
                if (config.protected && defaultSecurityScheme != null) {
                    security = mutableListOf(
                        SecurityRequirement().apply {
                            addList(defaultSecurityScheme, emptyList())
                        }
                    )
                }
            }
            when (config.method) {
                HttpMethod.Get -> get = operation
                HttpMethod.Post -> post = operation
                HttpMethod.Put -> put = operation
                HttpMethod.Patch -> patch = operation
                HttpMethod.Delete -> delete = operation
                HttpMethod.Head -> head = operation
                HttpMethod.Options -> options = operation
            }
        }
    }

    private fun shouldAddUnauthorized(config: RouteMeta, autoUnauthorizedResponses: Boolean) =
        autoUnauthorizedResponses
                && config.protected
                && config.documentation.getResponses().count { it.statusCode == HttpStatusCode.Unauthorized } == 0

    private fun defaultUnauthorizedResponse() = RouteResponse(HttpStatusCode.Unauthorized).apply {
        description = "Authentication failed"
    }

}