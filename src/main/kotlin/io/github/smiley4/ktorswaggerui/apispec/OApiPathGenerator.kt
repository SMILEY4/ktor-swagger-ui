package io.github.smiley4.ktorswaggerui.apispec

import io.github.smiley4.ktorswaggerui.documentation.SingleResponseDocumentation
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
    fun generate(
        config: RouteMeta,
        defaultUnauthorizedResponses: SingleResponseDocumentation?,
        defaultSecurityScheme: String?,
        tagGenerator: ((url: List<String>) -> String?)?
    ): Pair<String, PathItem> {
        return config.path to PathItem().apply {
            val operation = Operation().apply {
                tags = (config.documentation.tags + tagGenerator?.let { it(config.path.split("/").filter { it.isNotEmpty() }) })
                    .filterNotNull()
                summary = config.documentation.summary
                description = config.documentation.description
                parameters = OApiParametersGenerator().generate(config.documentation.getRequest().getParameters())
                config.documentation.getRequest().getBody()?.let {
                    requestBody = OApiRequestBodyGenerator().generate(it)
                }
                responses = ApiResponses().apply {
                    OApiResponsesGenerator().generate(config.documentation.getResponses().getResponses()).forEach {
                        addApiResponse(it.first, it.second)
                    }
                    if (shouldAddUnauthorized(config, defaultUnauthorizedResponses)) {
                        OApiResponsesGenerator().generate(listOf(defaultUnauthorizedResponses!!)).forEach {
                            addApiResponse(it.first, it.second)
                        }
                    }
                }
                if (config.protected) {
                    (config.documentation.securitySchemeName ?: defaultSecurityScheme)?.let { schemeName ->
                        security = mutableListOf(
                            SecurityRequirement().apply {
                                addList(schemeName, emptyList())
                            }
                        )
                    }
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


    /**
     * Whether a response for "Unauthorized" should be added automatically. Must be enabled and not already defined.
     */
    private fun shouldAddUnauthorized(config: RouteMeta, defaultUnauthorizedResponses: SingleResponseDocumentation?) =
        defaultUnauthorizedResponses != null
                && config.protected
                && config.documentation.getResponses().getResponses().count { it.statusCode == HttpStatusCode.Unauthorized } == 0

}