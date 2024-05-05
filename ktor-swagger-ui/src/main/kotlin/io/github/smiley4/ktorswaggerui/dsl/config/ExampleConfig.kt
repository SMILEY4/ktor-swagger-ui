package io.github.smiley4.ktorswaggerui.dsl.config

import io.github.smiley4.ktorswaggerui.data.ExampleDescriptor
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker
import io.swagger.v3.oas.models.examples.Example

/**
 * Configuration for schemas
 */
@OpenApiDslMarker
class ExampleConfig {

    var inComponents: Boolean = false

    private val examples = mutableMapOf<String, Example>()

    fun example(exampleDescriptor: ExampleDescriptor) {
        TODO()
    }

}