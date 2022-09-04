package io.github.smiley4.ktorswaggerui.dsl

import io.ktor.http.HttpStatusCode
import kotlin.reflect.KClass

/**
 * A container for the expected responses of an operation. The container maps a HTTP response code to the expected response.
 * A response code can only have one response object.
 */
@OpenApiDslMarker
class OpenApiResponse(val statusCode: HttpStatusCode) {

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
    fun header(name: String, schema: KClass<*>) {
        headers[name] = OpenApiHeader().apply {
            this.schema = schema
        }
    }

    fun getHeaders(): Map<String, OpenApiHeader> = headers


    private var body: OpenApiBody? = null


    /**
     * The body returned with this response
     */
    fun body(schema: KClass<*>, block: OpenApiBody.() -> Unit) {
        body = OpenApiBody(schema).apply(block)
    }


    /**
     * The body returned with this response
     */
    fun body(schema: KClass<*>) = body(schema) {}


    /**
     * The body returned with this response
     */
    fun body(block: OpenApiBody.() -> Unit) {
        body = OpenApiBody(null).apply(block)
    }


    fun getBody() = body

}
