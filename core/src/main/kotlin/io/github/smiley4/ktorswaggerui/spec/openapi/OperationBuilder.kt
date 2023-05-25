package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.spec.route.RouteMeta
import io.swagger.v3.oas.models.Operation

class OperationBuilder(
    private val operationTagsBuilder: OperationTagsBuilder,
    private val parameterBuilder: ParameterBuilder,
    private val requestBodyBuilder: RequestBodyBuilder,
    private val responsesBuilder: ResponsesBuilder,
    private val securityRequirementsBuilder: SecurityRequirementsBuilder
) {

    fun build(route: RouteMeta): Operation =
        Operation().also {
            it.summary = route.documentation.summary
            it.description = route.documentation.description
            it.operationId = route.documentation.operationId
            it.deprecated = route.documentation.deprecated
            it.tags = operationTagsBuilder.build(route)
            it.parameters = route.documentation.getRequest().getParameters().map { param -> parameterBuilder.build(param) }
            route.documentation.getRequest().getBody()?.let { body ->
                it.requestBody = requestBodyBuilder.build(body)
            }
            it.responses = responsesBuilder.build(route.documentation.getResponses(), route.protected)
            if (route.protected) {
                securityRequirementsBuilder.build(route).also { securityRequirements ->
                    if (securityRequirements.isNotEmpty()) {
                        it.security = securityRequirements
                    }
                }
            }
        }

}