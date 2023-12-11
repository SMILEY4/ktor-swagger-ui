package io.github.smiley4.ktorswaggerui.builder.openapi

import io.github.smiley4.ktorswaggerui.data.AuthType
import io.github.smiley4.ktorswaggerui.data.SecuritySchemeData
import io.swagger.v3.oas.models.security.SecurityScheme

class SecuritySchemesBuilder(
    private val oAuthFlowsBuilder: OAuthFlowsBuilder
) {

    fun build(securitySchemes: List<SecuritySchemeData>): Map<String, SecurityScheme> {
        return mutableMapOf<String, SecurityScheme>().apply {
            securitySchemes.forEach {
                put(it.schemeName, build(it))
            }
        }
    }

    private fun build(securityScheme: SecuritySchemeData): SecurityScheme {
        return SecurityScheme().apply {
            description = securityScheme.description
            name = if(securityScheme.type == AuthType.API_KEY) securityScheme.name ?: securityScheme.schemeName else null
            type = securityScheme.type?.swaggerType
            `in` = securityScheme.location?.swaggerType
            scheme = securityScheme.scheme?.swaggerType
            bearerFormat = securityScheme.bearerFormat
            flows = securityScheme.flows?.let { f -> oAuthFlowsBuilder.build(f) }
            openIdConnectUrl = securityScheme.openIdConnectUrl
        }
    }

}
