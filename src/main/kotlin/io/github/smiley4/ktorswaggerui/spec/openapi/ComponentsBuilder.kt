package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.media.Schema

class ComponentsBuilder(
    private val config: PluginConfigData,
    private val securitySchemesBuilder: SecuritySchemesBuilder
) {

    fun build(schemas: Map<String, Schema<*>>, examples: Map<String, Example>): Components {
        return Components().also {
            it.schemas = schemas
            it.examples = examples
            if (config.securitySchemes.isNotEmpty()) {
                it.securitySchemes = securitySchemesBuilder.build(config.securitySchemes)
            }
        }
    }

}
