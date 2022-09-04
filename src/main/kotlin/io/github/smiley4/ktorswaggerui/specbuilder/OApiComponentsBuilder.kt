package io.github.smiley4.ktorswaggerui.specbuilder

import io.swagger.v3.oas.models.Components

/**
 * Builder for the OpenAPI Components Object
 */
class OApiComponentsBuilder(
    private val exampleBuilder: OApiExampleBuilder
) {

    fun build(ctx: ComponentsContext): Components {
        return Components().apply {
            schemas = ctx.schemas
            examples = ctx.examples.mapValues {
                exampleBuilder.build("", it.value, ComponentsContext.NOOP)
            }
        }
    }

}
