package de.lruegner.ktorswaggerui.apispec

import de.lruegner.ktorswaggerui.OpenApiSecuritySchemeConfig
import io.swagger.v3.oas.models.security.SecurityScheme

/**
 * Generator for OpenAPI SecurityScheme-Objects
 */
class OApiSecuritySchemesGenerator() {

    /**
     * Generate the OpenAPI SecurityScheme-Objects from the given configs
     */
    fun generate(configs: List<OpenApiSecuritySchemeConfig>): Map<String, SecurityScheme> {
        return mutableMapOf<String, SecurityScheme>().apply {
            configs.forEach {
                put(it.name, SecurityScheme().apply {
                    type = when (it.type) {
                        OpenApiSecuritySchemeConfig.Type.API_KEY -> SecurityScheme.Type.APIKEY
                        OpenApiSecuritySchemeConfig.Type.HTTP -> SecurityScheme.Type.HTTP
                        OpenApiSecuritySchemeConfig.Type.OAUTH2 -> SecurityScheme.Type.OAUTH2
                        OpenApiSecuritySchemeConfig.Type.OPENID_CONNECT -> SecurityScheme.Type.OPENIDCONNECT
                        OpenApiSecuritySchemeConfig.Type.MUTUAL_TLS -> SecurityScheme.Type.MUTUALTLS
                        null -> null
                    }
                    name = it.name
                    `in` = when (it.location) {
                        OpenApiSecuritySchemeConfig.KeyLocation.QUERY -> SecurityScheme.In.QUERY
                        OpenApiSecuritySchemeConfig.KeyLocation.HEADER -> SecurityScheme.In.HEADER
                        OpenApiSecuritySchemeConfig.KeyLocation.COOKIE -> SecurityScheme.In.COOKIE
                        null -> null
                    }
                    scheme = when(it.scheme) {
                        OpenApiSecuritySchemeConfig.Scheme.BASIC -> "Basic"
                        OpenApiSecuritySchemeConfig.Scheme.BEARER -> "Bearer"
                        OpenApiSecuritySchemeConfig.Scheme.DIGEST -> "Digest"
                        OpenApiSecuritySchemeConfig.Scheme.HOBA -> "HOBA"
                        OpenApiSecuritySchemeConfig.Scheme.MUTUAL -> "Mutual"
                        OpenApiSecuritySchemeConfig.Scheme.OAUTH -> "OAuth"
                        OpenApiSecuritySchemeConfig.Scheme.SCRAM_SHA_1 -> "SCRAM-SHA-1"
                        OpenApiSecuritySchemeConfig.Scheme.SCRAM_SHA_256 -> "SCRAM-SHA-256"
                        OpenApiSecuritySchemeConfig.Scheme.VAPID -> "vapid"
                    }
                })
            }
        }
    }

}