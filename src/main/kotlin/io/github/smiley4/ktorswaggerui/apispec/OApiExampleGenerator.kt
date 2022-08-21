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
    fun generate(config: ExampleDocumentation): Example {
        return Example().apply {
            value = config.value
            summary = config.summary
            description = config.description
        }
    }

}