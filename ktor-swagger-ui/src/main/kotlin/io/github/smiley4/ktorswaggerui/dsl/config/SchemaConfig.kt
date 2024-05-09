package io.github.smiley4.ktorswaggerui.dsl.config

import io.github.smiley4.ktorswaggerui.data.SchemaConfigData
import io.github.smiley4.ktorswaggerui.data.TypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker
import io.github.smiley4.schemakenerator.swagger.data.CompiledSwaggerSchema
import kotlin.reflect.KType

/**
 * Configuration for schemas
 */
@OpenApiDslMarker
class SchemaConfig {

    private val schemas = mutableMapOf<String, TypeDescriptor>()

    fun schema(schemaId: String, descriptor: TypeDescriptor) {
        schemas[schemaId] = descriptor
    }

    var generator: (type: KType) -> CompiledSwaggerSchema = SchemaConfigData.DEFAULT.generator

    val overwrite = mutableMapOf<KType, TypeDescriptor>()

    fun build() = SchemaConfigData(
        schemas = schemas,
        generator = generator,
        overwrite = overwrite
    )

}
