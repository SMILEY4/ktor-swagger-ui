package io.github.smiley4.ktorswaggerui.tests

import com.fasterxml.jackson.core.type.TypeReference
import com.github.victools.jsonschema.generator.Option
import com.github.victools.jsonschema.generator.OptionPreset
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder
import com.github.victools.jsonschema.generator.SchemaVersion
import com.github.victools.jsonschema.module.jackson.JacksonModule
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.lang.reflect.Type

class JsonSchemaTest : StringSpec({

    "schema of integer" {
        generateSchema<Int>() shouldBe "{\"type\":\"integer\",\"format\":\"int32\"}"
    }

    "schema of string" {
        generateSchema<String>() shouldBe "{\"type\":\"string\"}"
    }

    "schema of object" {
        generateSchema<SpecificObject>() shouldBe "{\"type\":\"object\",\"properties\":{\"number\":{\"type\":\"integer\",\"format\":\"int64\"},\"text\":{\"type\":\"string\"}}}"
    }

    "schema of float-array" {
        generateSchema<FloatArray>() shouldBe "{\"type\":\"array\",\"items\":{\"type\":\"number\",\"format\":\"float\"}}"
    }

    "schema of object-array" {
        generateSchema<Array<SpecificObject>>() shouldBe "{\"type\":\"array\",\"items\":{\"type\":\"object\",\"properties\":{\"number\":{\"type\":\"integer\",\"format\":\"int64\"},\"text\":{\"type\":\"string\"}}}}"
    }

    "schema of list of string" {
        generateSchema<List<String>>() shouldBe "{\"type\":\"array\",\"items\":{\"type\":\"string\"}}"
    }

    "schema of list of objects" {
        generateSchema<List<SpecificObject>>() shouldBe "{\"type\":\"array\",\"items\":{\"type\":\"object\",\"properties\":{\"number\":{\"type\":\"integer\",\"format\":\"int64\"},\"text\":{\"type\":\"string\"}}}}"
    }

    "schema of generic-object" {
        generateSchema<GenericObject<SpecificObject>>() shouldBe "{\"type\":\"object\",\"properties\":{\"data\":{\"type\":\"object\",\"properties\":{\"number\":{\"type\":\"integer\",\"format\":\"int64\"},\"text\":{\"type\":\"string\"}}},\"flag\":{\"type\":\"boolean\"}}}"
    }

    "schema of pair with generic objects" {
        generateSchema<Pair<String, SpecificObject>>() shouldBe "{\"type\":\"object\",\"properties\":{\"first\":{\"type\":\"string\"},\"second\":{\"type\":\"object\",\"properties\":{\"number\":{\"type\":\"integer\",\"format\":\"int64\"},\"text\":{\"type\":\"string\"}}}}}"
    }

    "schema of nested generic objects" {
        generateSchema<GenericObject<GenericObject<SpecificObject>>>() shouldBe "{\"type\":\"object\",\"properties\":{\"data\":{\"type\":\"object\",\"properties\":{\"data\":{\"type\":\"object\",\"properties\":{\"number\":{\"type\":\"integer\",\"format\":\"int64\"},\"text\":{\"type\":\"string\"}}},\"flag\":{\"type\":\"boolean\"}}},\"flag\":{\"type\":\"boolean\"}}}"
    }

    "schema of generic-object with wildcard" {
        generateSchema<GenericObject<*>>() shouldBe "{\"type\":\"object\",\"properties\":{\"data\":{},\"flag\":{\"type\":\"boolean\"}}}"
    }

    "schema of generic-object with any" {
        generateSchema<GenericObject<Any>>() shouldBe "{\"type\":\"object\",\"properties\":{\"data\":{},\"flag\":{\"type\":\"boolean\"}}}"
    }


}) {

    companion object {

        private val generator = SchemaGenerator(
            SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON)
                .with(JacksonModule())
                .without(Option.DEFINITIONS_FOR_ALL_OBJECTS)
                .with(Option.INLINE_ALL_SCHEMAS)
                .with(Option.ALLOF_CLEANUP_AT_THE_END)
                .with(Option.EXTRA_OPEN_API_FORMAT_VALUES)
                // remove schema-definition for testing
                .without(Option.SCHEMA_VERSION_INDICATOR)
                .without(Option.DEFINITION_FOR_MAIN_SCHEMA)
                .build()
        )

        private inline fun <reified T> generateSchema(): String {
            val type: Type = object : TypeReference<T>() {}.type
            return generator.generateSchema(type).toString()
        }

        data class GenericObject<T>(
            val flag: Boolean,
            val data: T
        )

        data class SpecificObject(
            val text: String,
            val number: Long
        )
    }

}