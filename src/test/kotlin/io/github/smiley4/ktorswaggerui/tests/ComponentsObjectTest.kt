package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.dsl.OpenApiExample
import io.github.smiley4.ktorswaggerui.specbuilder.ComponentsContext
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.KClass

class ComponentsObjectTest : StringSpec({

    "test nothing in components section" {
        val context = ComponentsContext(false, mutableMapOf(), false, mutableMapOf())

        buildSchema(ComponentsTestClass1::class, context).let {
            it.type shouldBe "object"
            it.properties shouldHaveSize 2
            it.`$ref`.shouldBeNull()
        }

        buildSchema(ComponentsTestClass2::class, context).let {
            it.type shouldBe "object"
            it.properties shouldHaveSize 2
            it.`$ref`.shouldBeNull()
        }

        buildSchema(Array<ComponentsTestClass2>::class, context).let {
            it.type shouldBe "array"
            it.items.shouldNotBeNull()
            it.`$ref`.shouldBeNull()
            it.items.shouldNotBeNull()
            it.items.type shouldBe "object"
        }

        buildExample("Example1", ComponentsTestClass1("test1", true), context).let {
            it.value.shouldNotBeNull()
            it.`$ref`.shouldBeNull()
        }

        buildExample("Example1", ComponentsTestClass1("test1-different", false), context).let {
            it.value.shouldNotBeNull()
            it.`$ref`.shouldBeNull()
        }

        buildExample("Example2", ComponentsTestClass2("testCounter", 42), context).let {
            it.value.shouldNotBeNull()
            it.`$ref`.shouldBeNull()
        }

        buildComponentsObject(context).let {
            it.schemas shouldHaveSize 0
            it.examples shouldHaveSize 0
            it.responses.shouldBeNull()
            it.parameters.shouldBeNull()
            it.requestBodies.shouldBeNull()
            it.headers.shouldBeNull()
            it.securitySchemes.shouldBeNull()
            it.links.shouldBeNull()
            it.callbacks.shouldBeNull()
            it.extensions.shouldBeNull()
        }
    }

    "test schemas in components section" {
        val context = ComponentsContext(true, mutableMapOf(), false, mutableMapOf())

        buildSchema(ComponentsTestClass1::class, context).let {
            it.type.shouldBeNull()
            it.properties.shouldBeNull()
            it.`$ref` shouldBe "#/components/schemas/io.github.smiley4.ktorswaggerui.tests.ComponentsObjectTest.Companion.ComponentsTestClass1"
        }

        buildSchema(ComponentsTestClass2::class, context).let {
            it.type.shouldBeNull()
            it.properties.shouldBeNull()
            it.`$ref` shouldBe "#/components/schemas/io.github.smiley4.ktorswaggerui.tests.ComponentsObjectTest.Companion.ComponentsTestClass2"
        }

        buildSchema(Array<ComponentsTestClass2>::class, context).let {
            it.type shouldBe "array"
            it.properties.shouldBeNull()
            it.`$ref`.shouldBeNull()
            it.items.shouldNotBeNull()
            it.items.type.shouldBeNull()
            it.items.`$ref` shouldBe "#/components/schemas/io.github.smiley4.ktorswaggerui.tests.ComponentsObjectTest.Companion.ComponentsTestClass2"
        }

        buildExample("Example1", ComponentsTestClass1("test1", true), context).let {
            it.value.shouldNotBeNull()
            it.`$ref`.shouldBeNull()
        }

        buildExample("Example1", ComponentsTestClass1("test1-different", false), context).let {
            it.value.shouldNotBeNull()
            it.`$ref`.shouldBeNull()
        }

        buildExample("Example2", ComponentsTestClass2("testCounter", 42), context).let {
            it.value.shouldNotBeNull()
            it.`$ref`.shouldBeNull()
        }

        buildComponentsObject(context).let {
            it.schemas shouldHaveSize 2
            it.schemas.keys shouldContainExactlyInAnyOrder listOf(
                "io.github.smiley4.ktorswaggerui.tests.ComponentsObjectTest.Companion.ComponentsTestClass1",
                "io.github.smiley4.ktorswaggerui.tests.ComponentsObjectTest.Companion.ComponentsTestClass2"
            )
            it.schemas["io.github.smiley4.ktorswaggerui.tests.ComponentsObjectTest.Companion.ComponentsTestClass1"]!!.let { schema ->
                schema.type shouldBe "object"
                schema.properties shouldHaveSize 2
                schema.`$ref`.shouldBeNull()
            }
            it.schemas["io.github.smiley4.ktorswaggerui.tests.ComponentsObjectTest.Companion.ComponentsTestClass2"]!!.let { schema ->
                schema.type shouldBe "object"
                schema.properties shouldHaveSize 2
                schema.`$ref`.shouldBeNull()
            }
            it.examples shouldHaveSize 0
            it.responses.shouldBeNull()
            it.parameters.shouldBeNull()
            it.requestBodies.shouldBeNull()
            it.headers.shouldBeNull()
            it.securitySchemes.shouldBeNull()
            it.links.shouldBeNull()
            it.callbacks.shouldBeNull()
            it.extensions.shouldBeNull()
        }
    }

    "test examples in components section" {
        val context = ComponentsContext(false, mutableMapOf(), true, mutableMapOf())

        buildSchema(ComponentsTestClass1::class, context).let {
            it.type shouldBe "object"
            it.properties shouldHaveSize 2
            it.`$ref`.shouldBeNull()
        }

        buildSchema(ComponentsTestClass2::class, context).let {
            it.type shouldBe "object"
            it.properties shouldHaveSize 2
            it.`$ref`.shouldBeNull()
        }

        buildSchema(Array<ComponentsTestClass2>::class, context).let {
            it.type shouldBe "array"
            it.items.shouldNotBeNull()
            it.`$ref`.shouldBeNull()
            it.items.shouldNotBeNull()
            it.items.type shouldBe "object"
        }

        buildExample("Example1", ComponentsTestClass1("test1", true), context).let {
            it.value.shouldBeNull()
            it.`$ref` shouldBe "#/components/examples/Example1"
        }

        val exampleValue1Different = OpenApiExample(ComponentsTestClass1("test1-different", false))
        buildExample("Example1", exampleValue1Different, context).let {
            it.value.shouldBeNull()
            it.`$ref` shouldBe "#/components/examples/Example1#" + exampleValue1Different.hashCode().toString(16)
        }

        buildExample("Example2", ComponentsTestClass2("testCounter", 42), context).let {
            it.value.shouldBeNull()
            it.`$ref` shouldBe "#/components/examples/Example2"
        }

        buildComponentsObject(context).let {
            it.examples shouldHaveSize 3
            it.examples.keys shouldContainExactlyInAnyOrder listOf(
                "Example1",
                "Example1#" + exampleValue1Different.hashCode().toString(16),
                "Example2",
            )
            it.examples.values.forEach { example ->
                example.value.shouldNotBeNull()
                example.`$ref`.shouldBeNull()
            }
            it.schemas shouldHaveSize 0
            it.responses.shouldBeNull()
            it.parameters.shouldBeNull()
            it.requestBodies.shouldBeNull()
            it.headers.shouldBeNull()
            it.securitySchemes.shouldBeNull()
            it.links.shouldBeNull()
            it.callbacks.shouldBeNull()
            it.extensions.shouldBeNull()
        }
    }

}) {

    companion object {

        private fun buildComponentsObject(context: ComponentsContext): Components {
            return getOApiComponentsBuilder().build(context)
        }

        private fun buildSchema(type: KClass<*>, context: ComponentsContext): Schema<*> {
            return getOApiSchemaBuilder().build(type.java, context)
        }

        private fun buildExample(name: String, example: Any, context: ComponentsContext): Example {
            return getOApiExampleBuilder().build(name, OpenApiExample(example), context)
        }

        private fun buildExample(name: String, example: OpenApiExample, context: ComponentsContext): Example {
            return getOApiExampleBuilder().build(name, example, context)
        }

        private data class ComponentsTestClass1(
            val someText: String,
            val someFlat: Boolean
        )

        private data class ComponentsTestClass2(
            val name: String,
            val counter: Int,
        )

    }

}