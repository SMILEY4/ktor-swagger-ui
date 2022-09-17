package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.dsl.OpenApiSecurityScheme
import io.swagger.v3.oas.models.Components

/**
 * Builder for the OpenAPI Components Object
 */
class OApiComponentsBuilder {

    private val exampleBuilder = OApiExampleBuilder()
    private val securitySchemesBuilder = OApiSecuritySchemesBuilder()

    fun build(ctx: ComponentsContext, securitySchemes: List<OpenApiSecurityScheme>): Components {
        return Components().apply {
            schemas = ctx.schemas
            examples = ctx.examples.mapValues {
                exampleBuilder.build("", it.value, ComponentsContext.NOOP)
            }
            if (securitySchemes.isNotEmpty()) {
                this.securitySchemes = securitySchemesBuilder.build(securitySchemes)
            }
        }
    }

}
