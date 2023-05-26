package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.media.Schema

class ComponentsBuilder(
    private val config: SwaggerUIPluginConfig,
    private val securitySchemesBuilder: SecuritySchemesBuilder
) {

    fun build(schemas: Map<String, Schema<*>>): Components {
        return Components().also {
            it.schemas = schemas
            if (config.getSecuritySchemes().isNotEmpty()) {
                it.securitySchemes = securitySchemesBuilder.build(config.getSecuritySchemes())
            }
        }
    }

}