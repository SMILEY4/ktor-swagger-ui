package io.github.smiley4.ktorswaggerui.builder.example

import io.github.smiley4.ktorswaggerui.data.ExampleDescriptor
import io.swagger.v3.oas.models.examples.Example

/**
 * Provides examples for an openapi-spec
 */
interface ExampleContext {

    /**
     * Get an [Example] (or a ref to an example) by its [ExampleDescriptor]
     */
    fun getExample(descriptor: ExampleDescriptor): Example


    /**
     * Get all examples placed in the components-section of the spec.
     */
    fun getComponentSection(): Map<String, Example>
}
