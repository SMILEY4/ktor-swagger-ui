package io.github.smiley4.ktorswaggerui.dsl

import com.fasterxml.jackson.core.type.TypeReference
import java.lang.reflect.Type
import kotlin.reflect.KClass

/**
 * A container for the expected responses of an operation. The container maps a HTTP response code to the expected response.
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
    fun header(name: String, block: OpenApiHeader.() -> Unit) {
        headers[name] = OpenApiHeader().apply(block)
    }


    /**
     * Possible headers returned with this response
     */
    fun header(name: String, type: Type) {
        headers[name] = OpenApiHeader().apply {
            this.type = type
        }
    }


    /**
     * Possible headers returned with this response
     */
    fun header(name: String, type: KClass<*>) = header(name, type.java)


    /**
     * Possible headers returned with this response
     */
    inline fun <reified TYPE> header(name: String) = header(name, object : TypeReference<TYPE>() {}.type)


    fun getHeaders(): Map<String, OpenApiHeader> = headers


    private var body: OpenApiBody? = null


    /**
     * The body returned with this response
     */
    fun body(type: Type, block: OpenApiBody.() -> Unit) {
        body = OpenApiBody(type).apply(block)
    }


    /**
     * The body returned with this response
     */
    fun body(type: KClass<*>, block: OpenApiBody.() -> Unit) {
        body = OpenApiBody(type.java).apply(block)
    }


    /**
     * The body returned with this response
     */
    @JvmName("bodyGenericType")
    inline fun <reified TYPE> body(noinline block: OpenApiBody.() -> Unit) =
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
    fun body(block: OpenApiBody.() -> Unit) {
        body = OpenApiBody(null).apply(block)
    }


    /**
     * The body returned with this response
     */
    fun body(schemaUrl: String, block: OpenApiBody.() -> Unit) {
        body = OpenApiBody(null).apply(block).apply {
            customSchemaId = schemaUrl
        }
    }


    /**
     * The body returned with this response
     */
    fun body(schemaUrl: String) = body(schemaUrl) {}


    fun getBody() = body

}
