package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.dsl.OpenApiExample
import io.swagger.v3.oas.models.examples.Example

/**
 * Builder for the OpenAPI Example-Object
 */
class OApiExampleBuilder {

    fun build(name: String, example: OpenApiExample, components: ComponentsContext): Example {
        return if (components.examplesInComponents) {
            Example().apply {
                `$ref` = components.addExample(name, example)
            }
        } else {
            Example().apply {
                value = example.value
                summary = example.summary
                description = example.description
            }
        }
    }

}