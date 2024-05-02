package io.github.smiley4.ktorswaggerui.builder.openapi

import io.github.smiley4.ktorswaggerui.builder.route.RouteMeta
import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.swagger.v3.oas.models.security.SecurityRequirement

class SecurityRequirementsBuilder(
    private val config: PluginConfigData
) {

    fun build(route: RouteMeta): List<SecurityRequirement> {
        val securitySchemes = buildSet {
            addAll(route.documentation.securitySchemeNames)
            if(route.documentation.securitySchemeNames.isEmpty()) {
                addAll(config.defaultSecuritySchemeNames)
            }
        }
        return securitySchemes.map {
            SecurityRequirement().apply {
                addList(it, emptyList())
            }
        }
    }

}
