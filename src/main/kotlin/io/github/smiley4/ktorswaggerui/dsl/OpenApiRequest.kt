package io.github.smiley4.ktorswaggerui.dsl

import io.github.smiley4.ktorswaggerui.specbuilder.OpenApiBody
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
    fun pathParameter(name: String, schema: KClass<*>, block: OpenApiRequestParameter.() -> Unit) {
        parameters.add(OpenApiRequestParameter(name, schema, OpenApiRequestParameter.Location.PATH).apply(block))
    }


    /**
     * A path parameters that is applicable for this operation
     */
    fun pathParameter(name: String, schema: KClass<*>) = pathParameter(name, schema) {}


    /**
     * A query parameters that is applicable for this operation
     */
    fun queryParameter(name: String, schema: KClass<*>, block: OpenApiRequestParameter.() -> Unit) {
        parameters.add(OpenApiRequestParameter(name, schema, OpenApiRequestParameter.Location.QUERY).apply(block))
    }


    /**
     * A query parameters that is applicable for this operation
     */
    fun queryParameter(name: String, schema: KClass<*>) = queryParameter(name, schema) {}


    /**
     * A header parameters that is applicable for this operation
     */
    fun headerParameter(name: String, schema: KClass<*>, block: OpenApiRequestParameter.() -> Unit) {
        parameters.add(OpenApiRequestParameter(name, schema, OpenApiRequestParameter.Location.HEADER).apply(block))
    }


    /**
     * A header parameters that is applicable for this operation
     */
    fun headerParameter(name: String, schema: KClass<*>) = headerParameter(name, schema) {}


    fun getParameters(): List<OpenApiRequestParameter> = parameters


    private var body: OpenApiBody? = null


    /**
     * The request body applicable for this operation
     */
    fun body(schema: KClass<*>, block: OpenApiBody.() -> Unit) {
        body = OpenApiBody(schema).apply(block)
    }


    /**
     * The request body applicable for this operation
     */
    fun body(schema: KClass<*>) = body(schema) {}


    /**
     * The request body applicable for this operation
     */
    fun body(block: OpenApiBody.() -> Unit) {
        body = OpenApiBody(null).apply(block)
    }


    fun setBody(body: OpenApiBody?) {
        this.body = body
    }


    fun getBody() = body

}
