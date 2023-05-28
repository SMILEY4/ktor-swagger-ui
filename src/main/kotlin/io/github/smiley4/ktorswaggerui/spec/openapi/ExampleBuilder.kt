package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.OpenApiExample
import io.github.smiley4.ktorswaggerui.dsl.SchemaType
import io.swagger.v3.oas.models.examples.Example

class ExampleBuilder(
    private val config: SwaggerUIPluginConfig
) {

    fun build(type: SchemaType?, example: OpenApiExample): Example =
        Example().also {
            it.value = buildExampleValue(type, example.value)
            it.summary = example.summary
            it.description = example.description
        }

    fun buildExampleValue(type: SchemaType?, value: Any): String {
        return config.encodingConfig.getExampleEncoder()(type, value) ?: value.toString()
    }

}