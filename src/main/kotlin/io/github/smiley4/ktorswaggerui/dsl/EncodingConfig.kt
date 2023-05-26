package io.github.smiley4.ktorswaggerui.dsl

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.victools.jsonschema.generator.*
import com.github.victools.jsonschema.module.jackson.JacksonModule
import com.github.victools.jsonschema.module.swagger2.Swagger2Module
import io.swagger.v3.oas.annotations.media.Schema
import kotlin.reflect.jvm.javaType


typealias ExampleEncoder = (type: SchemaType?, example: Any) -> String?

typealias SchemaEncoder = (type: SchemaType) -> String?

/**
 * Configuration for encoding examples, schemas, ...
 */
@OpenApiDslMarker
class EncodingConfig {

    /**
     * Encode the given example object into a json-string.
     */
    fun exampleEncoder(encoder: ExampleEncoder) {
        exampleEncoder = encoder
    }

    private var exampleEncoder: ExampleEncoder = defaultExampleEncoder()

    fun getExampleEncoder() = exampleEncoder


    /**
     * Encode the given type into a valid json-schema.
     * This encoder does not affect custom-schemas provided in the plugin-config.
     */
    fun schemaEncoder(encoder: SchemaEncoder) {
        schemaEncoder = encoder
    }

    private var schemaEncoder: SchemaEncoder = defaultSchemaEncoder()

    fun getSchemaEncoder() = schemaEncoder

    /**
     * the name of the field (if it exists) in the json-schema containing schema-definitions.
     */
    var schemaDefinitionsField = "\$defs"

    companion object {

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
                .with(JacksonModule())
                .with(Swagger2Module())
                .with(Option.EXTRA_OPEN_API_FORMAT_VALUES)
                .with(Option.ALLOF_CLEANUP_AT_THE_END)
                .with(Option.MAP_VALUES_AS_ADDITIONAL_PROPERTIES)
                .with(Option.DEFINITIONS_FOR_ALL_OBJECTS)
                .without(Option.INLINE_ALL_SCHEMAS)
                .also {
                    it.forTypesInGeneral()
                        .withTypeAttributeOverride { objectNode: ObjectNode, typeScope: TypeScope, _: SchemaGenerationContext ->
                            if (typeScope is FieldScope) {
                                typeScope.getAnnotation(Schema::class.java)?.also { annotation ->
                                    if (annotation.example != "") {
                                        objectNode.put("example", annotation.example)
                                    }
                                }
                                typeScope.getAnnotation(Example::class.java)?.also { annotation ->
                                    objectNode.put("example", annotation.value)
                                }
                            }
                        }
                }

    }

}


