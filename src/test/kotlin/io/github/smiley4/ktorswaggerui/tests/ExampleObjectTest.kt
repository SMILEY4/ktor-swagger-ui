package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.apispec.ComponentsContext
import io.github.smiley4.ktorswaggerui.apispec.OApiExampleGenerator
import io.github.smiley4.ktorswaggerui.documentation.ExampleDocumentation
import io.kotest.core.spec.style.StringSpec
import io.swagger.v3.oas.models.examples.Example

class ExampleObjectTest : StringSpec({

    "test default example object" {
        val exampleValue = ExampleTestClass("TestText", true)
        val example = generateExampleObject("TestExample", exampleValue, ComponentsContext.NOOP)
        example shouldBeExample {
            value = exampleValue
        }
    }

    "test complete example object" {
        val exampleValue = ExampleTestClass("TestText", true)
        val example = generateExampleObject("TestExample", exampleValue, ComponentsContext.NOOP) {
            summary = "Test Summary"
            description = "Test Description"
        }
        example shouldBeExample {
            value = exampleValue
            summary = "Test Summary"
            description = "Test Description"
        }
    }

    "test referencing example in components" {
        val componentsContext = ComponentsContext(false, mutableMapOf(), true, mutableMapOf())
        val exampleValue = ExampleTestClass("TestText", true)
        val example = generateExampleObject("TestExample", exampleValue, componentsContext) {
            summary = "Test Summary"
            description = "Test Description"
        }
        example shouldBeExample {
            `$ref` = "#/components/examples/TestExample"
        }
    }

}) {

    companion object {

        private fun generateExampleObject(name: String, example: Any, context: ComponentsContext): Example {
            return OApiExampleGenerator().generate(name, ExampleDocumentation(example), context)
        }

        private fun generateExampleObject(
            name: String,
            example: Any,
            context: ComponentsContext,
            builder: ExampleDocumentation.() -> Unit
        ): Example {
            return OApiExampleGenerator().generate(name, ExampleDocumentation(example).apply(builder), context)
        }

        private data class ExampleTestClass(
            val someText: String,
            val someFlat: Boolean
        )

    }

}