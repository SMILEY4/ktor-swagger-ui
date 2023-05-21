package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.spec.route.RouteMeta
import io.swagger.v3.oas.models.security.SecurityRequirement

class SecurityRequirementsBuilder(
    private val config: SwaggerUIPluginConfig
) {

    fun build(route: RouteMeta): List<SecurityRequirement> {
        val securitySchemes = mutableSetOf<String>().also { schemes ->
            route.documentation.securitySchemeName?.also { schemes.add(it) }
            route.documentation.securitySchemeNames?.also { schemes.addAll(it) }
        }
        if (securitySchemes.isEmpty()) {
            config.defaultSecuritySchemeName?.also { securitySchemes.add(it) }
            config.defaultSecuritySchemeNames?.also { securitySchemes.addAll(it) }
        }
        return securitySchemes.map {
            SecurityRequirement().apply {
                addList(it, emptyList())
            }
        }
    }

}