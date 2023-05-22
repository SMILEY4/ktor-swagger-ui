package io.github.smiley4.ktorswaggerui.dsl

import io.github.smiley4.ktorswaggerui.dsl.OpenApiRequestParameter.Location
import kotlin.reflect.KClass


@OpenApiDslMarker
class OpenApiRequest {

    /**
     * A list of parameters that are applicable for this operation
     */
    private val parameters = mutableListOf<OpenApiRequestParameter>()

    fun getParameters(): List<OpenApiRequestParameter> = parameters


    /**
     * A path parameters that is applicable for this operation
     */
    fun parameter(location: Location, name: String, type: SchemaType, block: OpenApiRequestParameter.() -> Unit) {
        parameters.add(OpenApiRequestParameter(name, type, location).apply(block))
    }


    /**
     * A path parameters that is applicable for this operation
     */
    fun pathParameter(name: String, type: KClass<*>, block: OpenApiRequestParameter.() -> Unit) =
        parameter(Location.PATH, name, type.asSchemaType(), block)


    /**
     * A path parameters that is applicable for this operation
     */
    fun pathParameter(name: String, type: KClass<*>) = pathParameter(name, type) {}


    /**
     * A path parameters that is applicable for this operation
     */
    inline fun <reified TYPE> pathParameter(name: String) =
        parameter(Location.PATH, name, getSchemaType<TYPE>()) {}


    /**
     * A path parameters that is applicable for this operation
     */
    inline fun <reified TYPE> pathParameter(name: String, noinline block: OpenApiRequestParameter.() -> Unit) =
        parameter(Location.PATH, name, getSchemaType<TYPE>(), block)


    /**
     * A query parameters that is applicable for this operation
     */
    fun queryParameter(name: String, type: KClass<*>, block: OpenApiRequestParameter.() -> Unit) =
        parameter(Location.QUERY, name, type.asSchemaType(), block)


    /**
     * A query parameters that is applicable for this operation
     */
    fun queryParameter(name: String, type: KClass<*>) = queryParameter(name, type) {}


    /**
     * A query parameters that is applicable for this operation
     */
    inline fun <reified TYPE> queryParameter(name: String) =
        parameter(Location.QUERY, name, getSchemaType<TYPE>()) {}


    /**
     * A query parameters that is applicable for this operation
     */
    inline fun <reified TYPE> queryParameter(name: String, noinline block: OpenApiRequestParameter.() -> Unit) =
        parameter(Location.QUERY, name, getSchemaType<TYPE>(), block)


    /**
     * A header parameters that is applicable for this operation
     */
    fun headerParameter(name: String, type: KClass<*>, block: OpenApiRequestParameter.() -> Unit) =
        parameter(Location.HEADER, name, type.asSchemaType(), block)


    /**
     * A header parameters that is applicable for this operation
     */
    fun headerParameter(name: String, type: SchemaType) = parameter(Location.HEADER, name, type) {}


    /**
     * A header parameters that is applicable for this operation
     */
    inline fun <reified TYPE> headerParameter(name: String) =
        parameter(Location.HEADER, name, getSchemaType<TYPE>()) {}


    /**
     * A header parameters that is applicable for this operation
     */
    inline fun <reified TYPE> headerParameter(name: String, noinline block: OpenApiRequestParameter.() -> Unit) =
        parameter(Location.HEADER, name, getSchemaType<TYPE>(), block)


    private var body: OpenApiBaseBody? = null

    fun getBody() = body


    /**
     * The request body applicable for this operation
     */
    fun body(type: SchemaType?, block: OpenApiSimpleBody.() -> Unit) {
        body = OpenApiSimpleBody(type).apply(block)
    }

    /**
     * The request body applicable for this operation
     */
    fun body(type: KClass<*>, block: OpenApiSimpleBody.() -> Unit) = body(type.asSchemaType(), block)


    /**
     * The request body applicable for this operation
     */
    @JvmName("bodyGenericType")
    inline fun <reified TYPE> body(noinline block: OpenApiSimpleBody.() -> Unit) = body(getSchemaType<TYPE>(), block)


    /**
     * The request body applicable for this operation
     */
    fun body(type: KClass<*>) = body(type) {}


    /**
     * The request body applicable for this operation
     */
    inline fun <reified TYPE> body() = body(getSchemaType<TYPE>()) {}


    /**
     * The request body applicable for this operation
     */
    fun body(block: OpenApiSimpleBody.() -> Unit) = body(null, block)


    /**
     * The body returned with this request
     */
    fun body(customSchema: CustomSchemaRef, block: OpenApiSimpleBody.() -> Unit) {
        body = OpenApiSimpleBody(null).apply(block).apply {
            this.customSchema = customSchema
        }
    }


    /**
     * The body returned with this request
     */
    fun body(customSchema: CustomSchemaRef) = body(customSchema) {}


    /**
     * The body returned with this request
     */
    fun body(customSchemaId: String, block: OpenApiSimpleBody.() -> Unit) = body(obj(customSchemaId), block)


    /**
     * The body returned with this request
     */
    fun body(customSchemaId: String) = body(customSchemaId) {}


    /**
     * The multipart-body returned with this request
     */
    fun multipartBody(block: OpenApiMultipartBody.() -> Unit) {
        body = OpenApiMultipartBody().apply(block)
    }


    /**
     * Set the body of this request. Intended for internal use.
     */
    fun setBody(body: OpenApiBaseBody?) {
        this.body = body
    }


}
