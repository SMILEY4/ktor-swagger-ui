package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.OpenApiResponse
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.oas.models.security.SecurityRequirement

/**
 * Builder for a single OpenAPI Path
 */
class OApiPathBuilder {

    private val parametersBuilder = OApiParametersBuilder()
    private val requestBodyBuilder = OApiRequestBodyBuilder()
    private val responsesBuilder = OApiResponsesBuilder()


    fun build(route: RouteMeta, components: ComponentsContext, config: SwaggerUIPluginConfig): Pair<String, PathItem> {
        return route.path to PathItem().apply {
            val operation = Operation().apply {
                tags = buildTags(route, config.automaticTagGenerator)
                summary = route.documentation.summary
                description = route.documentation.description
                operationId = route.documentation.operationId
                parameters = parametersBuilder.build(route.documentation.getRequest().getParameters(), config)
                deprecated = route.documentation.deprecated
                route.documentation.getRequest().getBody()?.let {
                    requestBody = requestBodyBuilder.build(it, components, config)
                }
                responses = ApiResponses().apply {
                    responsesBuilder.build(route.documentation.getResponses().getResponses(), components, config).forEach {
                        addApiResponse(it.first, it.second)
                    }
                    if (shouldAddUnauthorized(route, config.getDefaultUnauthorizedResponse())) {
                        responsesBuilder.build(listOf(config.getDefaultUnauthorizedResponse()!!), components, config).forEach {
                            addApiResponse(it.first, it.second)
                        }
                    }
                }
                if (route.protected) {
                    (route.documentation.securitySchemeName ?: config.defaultSecuritySchemeName)?.let { schemeName ->
                        security = mutableListOf(
                            SecurityRequirement().apply {
                                addList(schemeName, emptyList())
                            }
                        )
                    }
                }
            }
            when (route.method) {
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


    private fun buildTags(route: RouteMeta, tagGenerator: ((url: List<String>) -> String?)?): List<String> {
        val generatedTags = tagGenerator?.let {
            it(route.path.split("/").filter { it.isNotEmpty() })
        }
        return (route.documentation.tags + generatedTags).filterNotNull()
    }


    private fun shouldAddUnauthorized(config: RouteMeta, defaultUnauthorizedResponses: OpenApiResponse?): Boolean {
        val unauthorizedCode = HttpStatusCode.Unauthorized.value.toString();
        return defaultUnauthorizedResponses != null
                && config.protected
                && config.documentation.getResponses().getResponses().count { it.statusCode == unauthorizedCode } == 0
    }
}