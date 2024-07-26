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


    /**
     * Add a shared example that can be referenced by all routes.
     * The name of the example has to be unique among all shared examples and acts as its id.
     * @param example the example data.
     */
    fun example(example: ExampleDescriptor) {
        sharedExamples[example.name] = example
    }

    /**
     * Add a shared example that can be referenced by all routes by the given name.
     * The provided name has to be unique among all shared examples and acts as its id.
     */
    fun example(name: String, example: Example) = example(SwaggerExampleDescriptor(name, example))

    /**
     * Add a shared example that can be referenced by all routes by the given name.
     * The provided name has to be unique among all shared examples and acts as its id.
     */
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


    /**
     * Specify a custom encoder for example objects
     */
    fun encoder(exampleEncoder: ExampleEncoder) {
        this.exampleEncoder = exampleEncoder
    }

    /**
     * Build the data object for this config.
     * @param securityConfig the data for security config that might contain additional examples
     */
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
