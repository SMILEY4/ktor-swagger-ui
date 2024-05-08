package io.github.smiley4.ktorswaggerui.dsl.config

import io.github.smiley4.ktorswaggerui.data.ExampleConfigData
import io.github.smiley4.ktorswaggerui.data.ExampleDescriptor
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker

/**
 * Configuration for schemas
 */
@OpenApiDslMarker
class ExampleConfig {

    private val sharedExamples = mutableMapOf<String, ExampleDescriptor>()

    fun example(exampleDescriptor: ExampleDescriptor) {
        sharedExamples[exampleDescriptor.name] = exampleDescriptor
    }

    fun build() = ExampleConfigData(
        sharedExamples = sharedExamples
    )

}