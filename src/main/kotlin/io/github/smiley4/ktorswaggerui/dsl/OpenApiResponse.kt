package io.github.smiley4.ktorswaggerui.dsl

import com.fasterxml.jackson.core.type.TypeReference
import java.lang.reflect.Type
import kotlin.reflect.KClass

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
    fun header(name: String, type: Type, block: OpenApiHeader.() -> Unit) {
        headers[name] = OpenApiHeader().apply(block).apply {
            this.type = type
        }
    }


    /**
     * Possible headers returned with this response
     */
    fun header(name: String, type: KClass<*>) = header(name, type.java) {}


    /**
     * Possible headers returned with this response
     */
    inline fun <reified TYPE> header(name: String) = header(name, object : TypeReference<TYPE>() {}.type) {}


    /**
     * Possible headers returned with this response
     */
    inline fun <reified TYPE> header(name: String, noinline block: OpenApiHeader.() -> Unit) =
        header(name, object : TypeReference<TYPE>() {}.type, block)

    fun getHeaders(): Map<String, OpenApiHeader> = headers

    private var body: OpenApiBaseBody? = null


    /**
     * The body returned with this response
     */
    fun body(type: Type, block: OpenApiSimpleBody.() -> Unit) {
        body = OpenApiSimpleBody(type).apply(block)
    }


    /**
     * The body returned with this response
     */
    fun body(type: KClass<*>, block: OpenApiSimpleBody.() -> Unit) {
        body = OpenApiSimpleBody(type.java).apply(block)
    }


    /**
     * The body returned with this response
     */
    @JvmName("bodyGenericType")
    inline fun <reified TYPE> body(noinline block: OpenApiSimpleBody.() -> Unit) =
        body(object : TypeReference<TYPE>() {}.type, block)


    /**
     * The body returned with this response
     */
    fun body(type: KClass<*>) = body(type.java) {}


    /**
     * The body returned with this response
     */
    inline fun <reified TYPE> body() = body(object : TypeReference<TYPE>() {}.type) {}


    /**
     * The body returned with this response
     */
    fun body(block: OpenApiSimpleBody.() -> Unit) {
        body = OpenApiSimpleBody(null).apply(block)
    }


    /**
     * The body returned with this response
     */
    fun body(customSchema: CustomSchemaRef, block: OpenApiSimpleBody.() -> Unit) {
        body = OpenApiSimpleBody(null).apply(block).apply {
            this.customSchema = customSchema
        }
    }


    /**
     * The body returned with this response
     */
    fun body(customSchema: CustomSchemaRef) = body(customSchema) {}


    /**
     * The body returned with this response
     */
    fun body(customSchemaId: String, block: OpenApiSimpleBody.() -> Unit) = body(obj(customSchemaId), block)


    /**
     * The body returned with this response
     */
    fun body(customSchemaId: String) = body(customSchemaId) {}


    /**
     * The multipart-body returned with this response
     */
    fun multipartBody(block: OpenApiMultipartBody.() -> Unit) {
        body = OpenApiMultipartBody().apply(block)
    }


    /**
     * Set the body of this response. Intended for internal use.
     */
    fun setBody(body: OpenApiBaseBody?) {
        this.body = body
    }

    fun getBody() = body

}
