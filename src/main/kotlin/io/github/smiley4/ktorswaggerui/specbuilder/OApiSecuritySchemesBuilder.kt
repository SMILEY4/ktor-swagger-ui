package io.github.smiley4.ktorswaggerui.specbuilder
import io.github.smiley4.ktorswaggerui.dsl.AuthKeyLocation
import io.github.smiley4.ktorswaggerui.dsl.AuthScheme
import io.github.smiley4.ktorswaggerui.dsl.AuthType
import io.github.smiley4.ktorswaggerui.dsl.OpenApiSecurityScheme
import io.swagger.v3.oas.models.security.SecurityScheme

/**
 * Builder for OpenAPI SecurityScheme-Objects
 */
class OApiSecuritySchemesBuilder(
    private val authFlowsBuilder: OApiOAuthFlowsBuilder
) {

    fun build(securitySchemes: List<OpenApiSecurityScheme>): Map<String, SecurityScheme> {
        return mutableMapOf<String, SecurityScheme>().apply {
            securitySchemes.forEach {
                put(it.name, SecurityScheme().apply {
                    description = it.description
                    name = it.name
                    type = when (it.type) {
                        AuthType.API_KEY -> SecurityScheme.Type.APIKEY
                        AuthType.HTTP -> SecurityScheme.Type.HTTP
                        AuthType.OAUTH2 -> SecurityScheme.Type.OAUTH2
                        AuthType.OPENID_CONNECT -> SecurityScheme.Type.OPENIDCONNECT
                        AuthType.MUTUAL_TLS -> SecurityScheme.Type.MUTUALTLS
                        null -> null
                    }
                    `in` = when (it.location) {
                        AuthKeyLocation.QUERY -> SecurityScheme.In.QUERY
                        AuthKeyLocation.HEADER -> SecurityScheme.In.HEADER
                        AuthKeyLocation.COOKIE -> SecurityScheme.In.COOKIE
                        null -> null
                    }
                    scheme = when (it.scheme) {
                        AuthScheme.BASIC -> "Basic"
                        AuthScheme.BEARER -> "Bearer"
                        AuthScheme.DIGEST -> "Digest"
                        AuthScheme.HOBA -> "HOBA"
                        AuthScheme.MUTUAL -> "Mutual"
                        AuthScheme.OAUTH -> "OAuth"
                        AuthScheme.SCRAM_SHA_1 -> "SCRAM-SHA-1"
                        AuthScheme.SCRAM_SHA_256 -> "SCRAM-SHA-256"
                        AuthScheme.VAPID -> "vapid"
                        else -> null
                    }
                    bearerFormat = it.bearerFormat
                    flows = it.getFlows()?.let { f -> authFlowsBuilder.build(f) }
                    openIdConnectUrl = it.openIdConnectUrl
                })
            }
        }
    }

}