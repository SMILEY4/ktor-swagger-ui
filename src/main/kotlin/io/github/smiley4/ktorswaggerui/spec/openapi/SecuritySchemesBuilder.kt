package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.dsl.OpenApiSecurityScheme
import io.swagger.v3.oas.models.security.SecurityScheme

class SecuritySchemesBuilder(
    private val oAuthFlowsBuilder: OAuthFlowsBuilder
) {

    fun build(securitySchemes: List<OpenApiSecurityScheme>): Map<String, SecurityScheme> {
        return mutableMapOf<String, SecurityScheme>().apply {
            securitySchemes.forEach {
                put(it.name, build(it))
            }
        }
    }

    private fun build(securityScheme: OpenApiSecurityScheme): SecurityScheme {
        return SecurityScheme().apply {
            description = securityScheme.description
            name = securityScheme.name
            type = securityScheme.type?.swaggerType
            `in` = securityScheme.location?.swaggerType
            scheme = securityScheme.scheme?.swaggerType
            bearerFormat = securityScheme.bearerFormat
            flows = securityScheme.getFlows()?.let { f -> oAuthFlowsBuilder.build(f) }
            openIdConnectUrl = securityScheme.openIdConnectUrl
        }
    }

}
