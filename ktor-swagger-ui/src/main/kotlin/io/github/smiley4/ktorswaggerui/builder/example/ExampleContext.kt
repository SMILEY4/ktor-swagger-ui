package io.github.smiley4.ktorswaggerui.builder.example

import io.github.smiley4.ktorswaggerui.data.ExampleDescriptor
import io.swagger.v3.oas.models.examples.Example

interface ExampleContext {
    fun getExample(descriptor: ExampleDescriptor): Example
    fun getComponentSection(): Map<String, Example>
}
