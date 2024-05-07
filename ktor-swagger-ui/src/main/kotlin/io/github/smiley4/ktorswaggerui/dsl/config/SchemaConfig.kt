package io.github.smiley4.ktorswaggerui.dsl.config

import io.github.smiley4.ktorswaggerui.data.SchemaConfigData
import io.github.smiley4.ktorswaggerui.data.TypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker

/**
 * Configuration for schemas
 */
@OpenApiDslMarker
class SchemaConfig {

    private val schemas = mutableMapOf<String, TypeDescriptor>()

    fun schema(schemaId: String, descriptor: TypeDescriptor) {
        schemas[schemaId] = descriptor
    }

    fun build() = SchemaConfigData(
        schemas = schemas
    )

}