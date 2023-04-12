package io.github.smiley4.ktorswaggerui.tests

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.victools.jsonschema.generator.Option
import com.github.victools.jsonschema.generator.OptionPreset
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder
import com.github.victools.jsonschema.generator.SchemaVersion
import com.github.victools.jsonschema.module.jackson.JacksonModule
import com.github.victools.jsonschema.module.swagger2.Swagger2Module
import io.github.smiley4.ktorswaggerui.experimental.SchemaBuilder
import io.kotest.core.spec.style.StringSpec
import io.swagger.v3.oas.models.media.Schema
import java.lang.reflect.Type

class JsonSchemToOpenApiSchema : StringSpec({

    "test" {
        val jsonSchema = generateJsonSchema<X>()
        val oapiSchema = generateOpenApiSchema<X>()
        println(jsonSchema)
        println(oapiSchema)
    }

    "test 2" {
        val type: Type = object : TypeReference<JsonSchemaGenerationTests.Companion.Superclass>() {}.type
        val schema = SchemaBuilder().build(type)
        println(schema)
    }

}) {

    companion object {

        private val generator = SchemaGenerator(
            SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON)
                .with(JacksonModule())
                .with(Swagger2Module())
                .without(Option.DEFINITIONS_FOR_ALL_OBJECTS)
                .with(Option.INLINE_ALL_SCHEMAS)
                .with(Option.EXTRA_OPEN_API_FORMAT_VALUES)
                .with(Option.ALLOF_CLEANUP_AT_THE_END)
                .with(Option.MAP_VALUES_AS_ADDITIONAL_PROPERTIES)

                .with(Option.DEFINITIONS_FOR_ALL_OBJECTS)
                .with(Option.DEFINITION_FOR_MAIN_SCHEMA)
                .without(Option.INLINE_ALL_SCHEMAS)

                .build()
        )

        private inline fun <reified T> generateJsonSchema(): String {
            val type: Type = object : TypeReference<T>() {}.type
            return generator.generateSchema(type).toPrettyString()
        }

        private inline fun <reified T> generateOpenApiSchema(): Schema<*> {
            return ObjectMapper().readValue(generateJsonSchema<T>(), Schema::class.java)
        }

        data class GenericObject<T>(
            val flag: Boolean,
            val data: T
        )

        data class SpecificObject(
            val text: String,
            val number: Long
        )

        data class Y(val a: String)

        data class X(val y: Y)

        enum class SimpleEnum {
            RED, GREEN, BLUE
        }

        data class SimpleDataClass(
            val text: String,
            val value: Float
        )

        data class DataClassWithMaps(
            val mapStringValues: Map<String, String>,
            val mapLongValues: Map<String, Long>
        )

        data class AnotherDataClass(
            val primitiveValue: Int,
            val primitiveList: List<Int>,
            private val privateValue: String,
            val nestedClass: SimpleDataClass,
            val nestedClassList: List<SimpleDataClass>
        )

        @JsonTypeInfo(
            use = JsonTypeInfo.Id.CLASS,
            include = JsonTypeInfo.As.PROPERTY,
            property = "_type",
        )
        @JsonSubTypes(
            JsonSubTypes.Type(value = SubClassA::class),
            JsonSubTypes.Type(value = SubClassB::class),
        )
        abstract class Superclass(
            val superField: String,
        )

        class SubClassA(
            superField: String,
            val subFieldA: Int
        ) : Superclass(superField)

        class SubClassB(
            superField: String,
            val subFieldB: Boolean
        ) : Superclass(superField)


        data class ClassWithNestedAbstractClass(
            val nestedClass: Superclass,
            val someField: String
        )

        class ClassWithGenerics<T>(
            val genericField: T,
            val genericList: List<T>
        )

        class WrapperForClassWithGenerics(
            val genericClass: ClassWithGenerics<String>
        )

    }


}