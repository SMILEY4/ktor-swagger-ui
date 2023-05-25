package io.github.smiley4.ktorswaggerui.dsl


typealias ExampleSerializer = (type: SchemaType?, example: Any) -> String?

typealias SchemaSerializer = (type: SchemaType) -> String?

/**
 * Configuration for serializing examples, schemas, ...
 */
@OpenApiDslMarker
class SerializationConfig {

    /**
     * Serialize the given example object into a json-string.
     */
    fun exampleSerializer(serializer: ExampleSerializer) {
        exampleSerializer = serializer
    }

    private var exampleSerializer: ExampleSerializer = { _, _ -> null }

    fun getExampleSerializer() = exampleSerializer


    /**
     * Serialize the given type into a valid json-schema.
     * This serializer does not affect custom-schemas provided in the plugin-config.
     */
    fun schemaSerializer(serializer: SchemaSerializer) {
        schemaSerializer = serializer
    }

    private var schemaSerializer: SchemaSerializer = { null }

    fun getSchemaSerializer() = schemaSerializer

    /**
     *
     */
    var schemaDefinitionsField = "\$defs"

}

