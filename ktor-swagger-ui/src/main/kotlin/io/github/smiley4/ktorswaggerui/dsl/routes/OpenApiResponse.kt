package io.github.smiley4.ktorswaggerui.dsl.routes

import io.github.smiley4.ktorswaggerui.data.KTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.OpenApiResponseData
import io.github.smiley4.ktorswaggerui.data.TypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker
import kotlin.reflect.KType

/**
 * A container for the expected responses of an operation. The container maps an HTTP response code to the expected response.
 * A response code can only have one response object.
 */
@OpenApiDslMarker
class OpenApiResponse(val statusCode: String) {

    /**
     * A short description of the response
     */
    var description: String? = null

    private val headers = mutableMapOf<String, OpenApiHeader>()

    /**
     * Possible headers returned with this response
     */
    fun header(name: String, type: TypeDescriptor, block: OpenApiHeader.() -> Unit) {
        headers[name] = OpenApiHeader().apply(block).apply {
            this.type = type
        }
    }

//
//    /**
//     * Possible headers returned with this response
//     */
//    fun header(name: String, type: KClass<*>, block: OpenApiHeader.() -> Unit) = header(name, type.asSchemaType(), block)
//
//
//    /**
//     * Possible headers returned with this response
//     */
//    fun header(name: String, type: KClass<*>) = header(name, type.asSchemaType()) {}
//
//
//    /**
//     * Possible headers returned with this response
//     */
//    inline fun <reified TYPE> header(name: String) = header(name, getSchemaType<TYPE>()) {}
//
//
//    /**
//     * Possible headers returned with this response
//     */
//    inline fun <reified TYPE> header(name: String, noinline block: OpenApiHeader.() -> Unit) = header(name, getSchemaType<TYPE>(), block)


    private var body: OpenApiBaseBody? = null

    /**
     * The body returned with this response
     */
    fun body(type: TypeDescriptor, block: OpenApiSimpleBody.() -> Unit) {
        body = OpenApiSimpleBody(type).apply(block)
    }


    /**
     * The body returned with this response
     */
    fun body(type: TypeDescriptor) = body(type) {}


    /**
     * The body returned with this response
     */
    fun body(type: KType, block: OpenApiSimpleBody.() -> Unit) = body(KTypeDescriptor(type), block)


//    /**
//     * The body returned with this response
//     */
//    fun body(type: KClass<*>, block: OpenApiSimpleBody.() -> Unit) = body(type.asSchemaType(), block)
//
//
//    /**
//     * The body returned with this response
//     */
//    @JvmName("bodyGenericType")
//    inline fun <reified TYPE> body(noinline block: OpenApiSimpleBody.() -> Unit) = body(getSchemaType<TYPE>(), block)
//
//
//    /**
//     * The body returned with this response
//     */
//    fun body(type: KClass<*>) = body(type) {}
//
//
//    /**
//     * The body returned with this response
//     */
//    inline fun <reified TYPE> body() = body(getSchemaType<TYPE>()) {}
//
//
//    /**
//     * The body returned with this response
//     */
//    fun body(block: OpenApiSimpleBody.() -> Unit) = body(EmptyTypeDescriptor, block)


//    /**
//     * The body returned with this response
//     */
//    fun body(customSchemaId: String, block: OpenApiSimpleBody.() -> Unit) = body(BodyTypeDescriptor.custom(customSchemaId), block)
//
//
//    /**
//     * The body returned with this response
//     */
//    fun body(customSchemaId: String) = body(customSchemaId) {}


    /**
     * The multipart-body returned with this response
     */
    fun multipartBody(block: OpenApiMultipartBody.() -> Unit) {
        body = OpenApiMultipartBody().apply(block)
    }


    fun build() = OpenApiResponseData(
        statusCode = statusCode,
        description = description,
        headers = headers.mapValues { it.value.build() },
        body = body?.build()
    )

}
