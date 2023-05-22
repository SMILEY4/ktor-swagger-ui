package io.github.smiley4.ktorswaggerui.dsl

/**
 * Configuration for serializing examples, schemas, ...
 */
@OpenApiDslMarker
class SerializationConfig {

    /**
     * Serialize the given example object into a json-string.
     * Return 'null' to use the default serializer for the given value instead.
     */
    fun exampleSerializer(serializer: CustomExampleSerializer) {
        customExampleSerializer = serializer
    }

    private var customExampleSerializer: CustomExampleSerializer = { _, _ -> null }

    fun getCustomExampleSerializer() = customExampleSerializer


    /**
     * Serialize the given type into a valid json-schema.
     * Return 'null' to use the default serializer for the given type instead.
     * This serializer does not affect custom-schemas provided in the plugin-config.
     */
    fun schemaSerializer(serializer: CustomSchemaSerializer) {
        customSchemaSerializer = serializer
    }

    private var customSchemaSerializer: CustomSchemaSerializer = { null }


    fun getCustomSchemaSerializer() = customSchemaSerializer

}

typealias CustomExampleSerializer = (type: SchemaType?, example: Any) -> String?

typealias CustomSchemaSerializer = (type: SchemaType) -> String?
