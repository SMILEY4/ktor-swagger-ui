package io.github.smiley4.ktorswaggerui.dsl

import kotlin.reflect.KClass


/**
 * Describes a single request/response body with multipart content.
 * See https://swagger.io/docs/specification/describing-request-body/multipart-requests/ for more info
 */
@OpenApiDslMarker
class OpenApiMultipartBody : OpenApiBaseBody() {

    private val parts = mutableListOf<OpenApiMultipartPart>()

    fun getParts(): List<OpenApiMultipartPart> = parts


    /**
     * One part of a multipart-body
     */
    fun part(name: String, type: SchemaType, block: OpenApiMultipartPart.() -> Unit) {
        parts.add(OpenApiMultipartPart(name, type).apply(block))
    }


    /**
     * One part of a multipart-body
     */
    fun part(name: String, type: KClass<*>) = part(name, type.asSchemaType()) {}


    /**
     * One part of a multipart-body
     */
    inline fun <reified TYPE> part(name: String) = part(name, getSchemaType<TYPE>()) {}


    /**
     * One part of a multipart-body
     */
    inline fun <reified TYPE> part(name: String, noinline block: OpenApiMultipartPart.() -> Unit) = part(name, getSchemaType<TYPE>(), block)


    /**
     * One part of a multipart-body
     */
    fun part(name: String, customSchema: CustomSchemaRef, block: OpenApiMultipartPart.() -> Unit) {
        parts.add(OpenApiMultipartPart(name, null).apply(block).apply {
            this.customSchema = customSchema
        })
    }


    /**
     * One part of a multipart-body
     */
    fun part(name: String, customSchema: CustomSchemaRef) = part(name, customSchema) {}


    /**
     * One part of a multipart-body
     */
    fun part(name: String, customSchemaId: String, block: OpenApiMultipartPart.() -> Unit) = part(name, obj(customSchemaId), block)


    /**
     * One part of a multipart-body
     */
    fun part(name: String, customSchemaId: String) = part(name, customSchemaId) {}

}