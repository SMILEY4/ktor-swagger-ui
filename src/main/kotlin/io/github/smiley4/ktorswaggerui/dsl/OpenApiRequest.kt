package io.github.smiley4.ktorswaggerui.dsl

import com.fasterxml.jackson.core.type.TypeReference
import java.lang.reflect.Type
import kotlin.reflect.KClass


@OpenApiDslMarker
class OpenApiRequest {

    /**
     * A list of parameters that are applicable for this operation
     */
    private val parameters = mutableListOf<OpenApiRequestParameter>()


    /**
     * A path parameters that is applicable for this operation
     */
    fun pathParameter(name: String, type: Type, block: OpenApiRequestParameter.() -> Unit) {
        parameters.add(OpenApiRequestParameter(name, type, OpenApiRequestParameter.Location.PATH).apply(block))
    }


    /**
     * A path parameters that is applicable for this operation
     */
    fun pathParameter(name: String, type: KClass<*>, block: OpenApiRequestParameter.() -> Unit) = pathParameter(name, type.java, block)


    /**
     * A path parameters that is applicable for this operation
     */
    fun pathParameter(name: String, type: KClass<*>) = pathParameter(name, type) {}


    /**
     * A path parameters that is applicable for this operation
     */
    inline fun <reified TYPE> pathParameter(name: String) = pathParameter(name, object : TypeReference<TYPE>() {}.type) {}


    /**
     * A path parameters that is applicable for this operation
     */
    inline fun <reified TYPE> pathParameter(name: String, noinline block: OpenApiRequestParameter.() -> Unit) =
        pathParameter(name, object : TypeReference<TYPE>() {}.type, block)


    /**
     * A query parameters that is applicable for this operation
     */
    fun queryParameter(name: String, type: Type, block: OpenApiRequestParameter.() -> Unit) {
        parameters.add(OpenApiRequestParameter(name, type, OpenApiRequestParameter.Location.QUERY).apply(block))
    }


    /**
     * A query parameters that is applicable for this operation
     */
    fun queryParameter(name: String, type: KClass<*>, block: OpenApiRequestParameter.() -> Unit) = queryParameter(name, type.java, block)


    /**
     * A query parameters that is applicable for this operation
     */
    fun queryParameter(name: String, type: KClass<*>) = queryParameter(name, type) {}


    /**
     * A query parameters that is applicable for this operation
     */
    inline fun <reified TYPE> queryParameter(name: String) = queryParameter(name, object : TypeReference<TYPE>() {}.type) {}


    /**
     * A query parameters that is applicable for this operation
     */
    inline fun <reified TYPE> queryParameter(name: String, noinline block: OpenApiRequestParameter.() -> Unit) =
        queryParameter(name, object : TypeReference<TYPE>() {}.type, block)


    /**
     * A header parameters that is applicable for this operation
     */
    fun headerParameter(name: String, type: Type, block: OpenApiRequestParameter.() -> Unit) {
        parameters.add(OpenApiRequestParameter(name, type, OpenApiRequestParameter.Location.HEADER).apply(block))
    }


    /**
     * A header parameters that is applicable for this operation
     */
    fun headerParameter(name: String, type: KClass<*>, block: OpenApiRequestParameter.() -> Unit) = headerParameter(name, type.java, block)


    /**
     * A header parameters that is applicable for this operation
     */
    fun headerParameter(name: String, type: KClass<*>) = headerParameter(name, type) {}


    /**
     * A header parameters that is applicable for this operation
     */
    inline fun <reified TYPE> headerParameter(name: String) = headerParameter(name, object : TypeReference<TYPE>() {}.type) {}


    /**
     * A header parameters that is applicable for this operation
     */
    inline fun <reified TYPE> headerParameter(name: String, noinline block: OpenApiRequestParameter.() -> Unit) =
        headerParameter(name, object : TypeReference<TYPE>() {}.type, block)


    fun getParameters(): List<OpenApiRequestParameter> = parameters


    private var body: OpenApiBody? = null


    /**
     * The request body applicable for this operation
     */
    fun body(type: Type, block: OpenApiBody.() -> Unit) {
        body = OpenApiBody(type).apply(block)
    }


    /**
     * The request body applicable for this operation
     */
    fun body(type: KClass<*>, block: OpenApiBody.() -> Unit) {
        body = OpenApiBody(type.java).apply(block)
    }


    /**
     * The request body applicable for this operation
     */
    @JvmName("bodyGenericType")
    inline fun <reified TYPE> body(noinline block: OpenApiBody.() -> Unit) = body(object : TypeReference<TYPE>() {}.type, block)


    /**
     * The request body applicable for this operation
     */
    fun body(type: KClass<*>) = body(type.java) {}


    /**
     * The request body applicable for this operation
     */
    inline fun <reified TYPE> body() = body(object : TypeReference<TYPE>() {}.type) {}


    /**
     * The request body applicable for this operation
     */
    fun body(block: OpenApiBody.() -> Unit) {
        body = OpenApiBody(null).apply(block)
    }


    /**
     * The body returned with this response
     */
    fun body(schemaUrl: String, block: OpenApiBody.() -> Unit) {
        body = OpenApiBody(null).apply(block).apply {
            externalSchemaUrl = schemaUrl
        }
    }


    /**
     * The body returned with this response
     */
    fun body(schemaUrl: String) = body(schemaUrl) {}


    /**
     * Set the body of this request. Intended for internal use.
     */
    fun setBody(body: OpenApiBody?) {
        this.body = body
    }

    fun getBody() = body

}
