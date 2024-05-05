package io.github.smiley4.ktorswaggerui.dsl.config

import io.github.smiley4.ktorswaggerui.data.TypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker
import io.swagger.v3.oas.models.media.Schema

/**
 * Configuration for schemas
 */
@OpenApiDslMarker
class SchemaConfig {

    private val schemas = mutableMapOf<String, Schema<*>>()

    fun schema(descriptor: TypeDescriptor) {
        TODO()
    }

}