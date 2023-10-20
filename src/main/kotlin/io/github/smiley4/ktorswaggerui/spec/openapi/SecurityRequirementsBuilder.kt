package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.github.smiley4.ktorswaggerui.spec.route.RouteMeta
import io.swagger.v3.oas.models.security.SecurityRequirement

class SecurityRequirementsBuilder(
    private val config: PluginConfigData
) {

    fun build(route: RouteMeta): List<SecurityRequirement> {
        val securitySchemes = mutableSetOf<String>().also { schemes ->
            route.documentation.securitySchemeName?.also { schemes.add(it) }
            route.documentation.securitySchemeNames?.also { schemes.addAll(it) }
        }
        if (securitySchemes.isEmpty()) {
            config.defaultSecuritySchemeNames.also { securitySchemes.addAll(it) }
        }
        return securitySchemes.map {
            SecurityRequirement().apply {
                addList(it, emptyList())
            }
        }
    }

}
