package io.github.smiley4.ktorswaggerui.dsl.config

import io.github.smiley4.ktorswaggerui.data.*
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

    /**
     * The json-schema generator for all schemas. See https://github.com/SMILEY4/schema-kenerator/wiki for more information.
     */
    var generator: (type: KType) -> CompiledSwaggerSchema = SchemaConfigData.DEFAULT.generator

    private val schemas = mutableMapOf<String, TypeDescriptor>()

    private val overwrite = mutableMapOf<KType, TypeDescriptor>()


    /**
     * Overwrite the given [type] with the given [replacement].
     * When the type is specified as the type of a schema, the replacement is used instead.
     * This only works for "root"-types and not types of e.g. nested fields.
     */
    fun overwrite(type: KType, replacement: TypeDescriptor) {
        overwrite[type] = replacement
    }

    /**
     * Overwrite the given type [T] with the given [replacement].
     * When the type is specified as the type of a schema, the replacement is used instead.
     * This only works for "root"-types and not types of e.g. nested fields.
     */
    inline fun <reified T> overwrite(replacement: TypeDescriptor) = overwrite(typeOf<T>(), replacement)

    /**
     * Overwrite the given type [T] with the given [replacement].
     * When the type is specified as the type of a schema, the replacement is used instead.
     * This only works for "root"-types and not types of e.g. nested fields.
     */
    inline fun <reified T> overwrite(replacement: Schema<*>) = overwrite(typeOf<T>(), SwaggerTypeDescriptor(replacement))

    /**
     * Overwrite the given type [T] with the given [replacement].
     * When the type is specified as the type of a schema, the replacement is used instead.
     * This only works for "root"-types and not types of e.g. nested fields.
     */
    inline fun <reified T> overwrite(replacement: KType) = overwrite(typeOf<T>(), KTypeDescriptor(replacement))

    /**
     * Overwrite the given type [T] with the given replacement [R].
     * When the type is specified as the type of a schema, the replacement is used instead.
     * This only works for "root"-types and not types of e.g. nested fields.
     */
    inline fun <reified T, reified R> overwrite() = overwrite(typeOf<T>(), KTypeDescriptor(typeOf<R>()))


    /**
     * Add a shared schema that can be referenced by all routes by the given id.
     */
    fun schema(schemaId: String, descriptor: TypeDescriptor) {
        schemas[schemaId] = descriptor
    }

    /**
     * Add a shared schema that can be referenced by all routes by the given id.
     */
    fun schema(schemaId: String, schema: Schema<*>) = schema(schemaId, SwaggerTypeDescriptor(schema))

    /**
     * Add a shared schema that can be referenced by all routes by the given id.
     */
    fun schema(schemaId: String, schema: KType) = schema(schemaId, KTypeDescriptor(schema))

    /**
     * Add a shared schema that can be referenced by all routes by the given id.
     */
    inline fun <reified T> schema(schemaId: String) = schema(schemaId, KTypeDescriptor(typeOf<T>()))

    fun build(securityConfig: SecurityData) = SchemaConfigData(
        generator = generator,
        schemas = schemas,
        overwrite = overwrite,
        securitySchemas = securityConfig.defaultUnauthorizedResponse?.body?.let {
            when (it) {
                is OpenApiSimpleBodyData -> listOf(it.type)
                is OpenApiMultipartBodyData -> it.parts.map { it.type }
            }
        } ?: emptyList()
    )

}
