package io.github.smiley4.ktorswaggerui.apispec

import io.github.smiley4.ktorswaggerui.documentation.ExampleDocumentation
import io.swagger.v3.oas.models.examples.Example

/**
 * Generator for the OpenAPI Example-Object
 */
class OApiExampleGenerator {

    /**
     * Generate the OpenAPI Info-Example from the given config
     */
    fun generate(name: String, config: ExampleDocumentation, componentsCtx: ComponentsContext): Example {
        return if (componentsCtx.examplesInComponents) {
            Example().apply {
                `$ref` = "#/components/examples/" + componentsCtx.addExample(name, config)
            }
        } else {
            Example().apply {
                value = config.value
                summary = config.summary
                description = config.description
            }
        }
    }

}