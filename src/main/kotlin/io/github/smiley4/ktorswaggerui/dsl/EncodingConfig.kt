package io.github.smiley4.ktorswaggerui.dsl

import io.github.smiley4.ktorswaggerui.data.DataUtils.mergeDefault
import io.github.smiley4.ktorswaggerui.data.EncodingData


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

    private var exampleEncoder: ExampleEncoder = EncodingData.DEFAULT.exampleEncoder

    fun getExampleEncoder() = exampleEncoder


    /**
     * Encode the given type into a valid json-schema.
     * This encoder does not affect custom-schemas provided in the plugin-config.
     */
    fun schemaEncoder(encoder: SchemaEncoder) {
        schemaEncoder = encoder
    }

    private var schemaEncoder: SchemaEncoder = EncodingData.DEFAULT.schemaEncoder

    fun getSchemaEncoder() = schemaEncoder


    /**
     * the name of the field (if it exists) in the json-schema containing schema-definitions.
     */
    var schemaDefinitionsField = EncodingData.DEFAULT.schemaDefsField


    fun build(base: EncodingData) = EncodingData(
        exampleEncoder = mergeDefault(base.exampleEncoder, exampleEncoder, EncodingData.DEFAULT.exampleEncoder),
        schemaEncoder = mergeDefault(base.schemaEncoder, schemaEncoder, EncodingData.DEFAULT.schemaEncoder),
        schemaDefsField = mergeDefault(base.schemaDefsField, schemaDefinitionsField, EncodingData.DEFAULT.schemaDefsField),
    )

}
