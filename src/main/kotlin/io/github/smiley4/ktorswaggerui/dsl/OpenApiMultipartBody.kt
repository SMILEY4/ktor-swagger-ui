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
    fun part(name: String, type: BodyTypeDescriptor, block: OpenApiMultipartPart.() -> Unit) {
        parts.add(OpenApiMultipartPart(name, type).apply(block))
    }


    /**
     * One part of a multipart-body
     */
    fun part(name: String, type: BodyTypeDescriptor) = part(name, type) {}


    /**
     * One part of a multipart-body
     */
    fun part(name: String, type: SchemaType, block: OpenApiMultipartPart.() -> Unit) = part(name, BodyTypeDescriptor.typeOf(type), block)


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
    fun part(name: String, customSchemaId: String, block: OpenApiMultipartPart.() -> Unit) =
        part(name, BodyTypeDescriptor.custom(customSchemaId), block)


    /**
     * One part of a multipart-body
     */
    fun part(name: String, customSchemaId: String) = part(name, customSchemaId) {}

}
