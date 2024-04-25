package io.github.smiley4.ktorswaggerui.data

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.victools.jsonschema.generator.Option
import com.github.victools.jsonschema.generator.OptionPreset
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder
import com.github.victools.jsonschema.generator.SchemaVersion
import com.github.victools.jsonschema.module.jackson.JacksonModule
import com.github.victools.jsonschema.module.swagger2.Swagger2Module
import io.github.smiley4.ktorswaggerui.dsl.ExampleEncoder
import io.github.smiley4.ktorswaggerui.dsl.SchemaEncoder
import io.github.smiley4.ktorswaggerui.dsl.SchemaType
import io.github.smiley4.ktorswaggerui.builder.schema.SchemaTypeAttributeOverride
import kotlin.reflect.jvm.javaType

data class EncodingData(
    val exampleEncoder: ExampleEncoder,
    val schemaEncoder: SchemaEncoder,
    val schemaDefsField: String
) {

    companion object {
        val DEFAULT = EncodingData(
            exampleEncoder = defaultExampleEncoder(),
            schemaEncoder = defaultSchemaEncoder(),
            schemaDefsField = "\$defs"
        )


        /**
         * The default jackson object mapper used for encoding examples to json.
         */
        var DEFAULT_EXAMPLE_OBJECT_MAPPER = jacksonObjectMapper()


        /**
         * The default [SchemaGenerator] used to encode types to json-schema.
         * See https://victools.github.io/jsonschema-generator/#generator-options for more information.
         */
        var DEFAULT_SCHEMA_GENERATOR = SchemaGenerator(schemaGeneratorConfigBuilder().build())


        /**
         * The default [ExampleEncoder]
         */
        fun defaultExampleEncoder(): ExampleEncoder {
            return { _, value -> encodeExample(value) }
        }


        /**
         * encode the given value to a json string
         */
        fun encodeExample(value: Any?): String {
            return if (value is String) {
                value
            } else {
                DEFAULT_EXAMPLE_OBJECT_MAPPER.writeValueAsString(value)
            }
        }


        /**
         * The default [SchemaEncoder]
         */
        fun defaultSchemaEncoder(): SchemaEncoder {
            return { type -> encodeSchema(type) }
        }


        /**
         * encode the given type to a json-schema
         */
        fun encodeSchema(type: SchemaType): String {
            return DEFAULT_SCHEMA_GENERATOR.generateSchema(type.javaType).toPrettyString()
        }


        /**
         * The default [SchemaGeneratorConfigBuilder]
         */
        fun schemaGeneratorConfigBuilder(): SchemaGeneratorConfigBuilder =
            SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON)
                .with(Swagger2Module())
                .with(Option.EXTRA_OPEN_API_FORMAT_VALUES)
                .with(Option.ALLOF_CLEANUP_AT_THE_END)
                .with(Option.MAP_VALUES_AS_ADDITIONAL_PROPERTIES)
                .with(Option.DEFINITIONS_FOR_ALL_OBJECTS)
                .with(Option.INLINE_NULLABLE_SCHEMAS)
                .with(JacksonModule())
                .without(Option.INLINE_ALL_SCHEMAS)
                .also {
                    it.forTypesInGeneral()
                        .withTypeAttributeOverride(SchemaTypeAttributeOverride())
                }

    }

}
