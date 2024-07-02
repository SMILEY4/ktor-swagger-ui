package io.github.smiley4.ktorswaggerui.dsl.config

import io.github.smiley4.ktorswaggerui.data.*
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker
import io.github.smiley4.ktorswaggerui.dsl.routes.ValueExampleDescriptorDsl
import io.swagger.v3.oas.models.examples.Example

/**
 * Configuration for examples
 */
@OpenApiDslMarker
class ExampleConfig {

    val sharedExamples = mutableMapOf<String, ExampleDescriptor>()
    var exampleEncoder: ExampleEncoder? = null

    fun example(example: ExampleDescriptor) {
        sharedExamples[example.name] = example
    }

    fun example(name: String, example: Example) = example(SwaggerExampleDescriptor(name, example))

    fun example(name: String, example: ValueExampleDescriptorDsl.() -> Unit) = example(
        ValueExampleDescriptorDsl()
            .apply(example)
            .let { result ->
                ValueExampleDescriptor(
                    name = name,
                    value = result.value,
                    summary = result.summary,
                    description = result.description
                )
            }
    )

    fun encoder(exampleEncoder: ExampleEncoder) {
        this.exampleEncoder = exampleEncoder
    }

    fun build(securityConfig: SecurityData) = ExampleConfigData(
        sharedExamples = sharedExamples,
        securityExamples = securityConfig.defaultUnauthorizedResponse?.body?.let {
            when (it) {
                is OpenApiSimpleBodyData -> it
                is OpenApiMultipartBodyData -> null
            }
        },
        exampleEncoder = exampleEncoder
    )
}
