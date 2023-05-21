package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.dsl.OpenApiExample
import io.swagger.v3.oas.models.examples.Example

class ExampleBuilder {

    fun build(example: OpenApiExample): Example =
        Example().also {
            it.value = example.value
            it.summary = example.summary
            it.description = example.description
        }

}