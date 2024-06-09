package io.github.smiley4.ktorswaggerui.dsl.config

import io.github.smiley4.ktorswaggerui.data.KTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.SchemaConfigData
import io.github.smiley4.ktorswaggerui.data.SwaggerTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.TypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker
import io.github.smiley4.schemakenerator.swagger.data.CompiledSwaggerSchema
import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Configuration for schemas
 */
@OpenApiDslMarker
class SchemaConfig {

    var generator: (type: KType) -> CompiledSwaggerSchema = SchemaConfigData.DEFAULT.generator

    val schemas = mutableMapOf<String, TypeDescriptor>()

    val overwrite = mutableMapOf<KType, TypeDescriptor>()

    fun overwrite(type: KType, replacement: TypeDescriptor) {
        overwrite[type] = replacement
    }

    inline fun <reified T> overwrite(replacement: TypeDescriptor) = overwrite(typeOf<T>(), replacement)

    inline fun <reified T> overwrite(replacement: Schema<*>) = overwrite(typeOf<T>(), SwaggerTypeDescriptor(replacement))

    inline fun <reified T> overwrite(replacement: KType) = overwrite(typeOf<T>(), KTypeDescriptor(replacement))

    inline fun <reified T, reified R> overwrite() = overwrite(typeOf<T>(), KTypeDescriptor(typeOf<R>()))


    fun schema(schemaId: String, descriptor: TypeDescriptor) {
        schemas[schemaId] = descriptor
    }

    fun schema(schemaId: String, schema: Schema<*>) = schema(schemaId, SwaggerTypeDescriptor(schema))

    fun schema(schemaId: String, schema: KType) = schema(schemaId, KTypeDescriptor(schema))

    inline fun <reified T> schema(schemaId: String) = schema(schemaId, KTypeDescriptor(typeOf<T>()))

    fun build() = SchemaConfigData(
        generator = generator,
        schemas = schemas,
        overwrite = overwrite
    )

}
