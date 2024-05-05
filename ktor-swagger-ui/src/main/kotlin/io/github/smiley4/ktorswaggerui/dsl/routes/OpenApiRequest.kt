package io.github.smiley4.ktorswaggerui.dsl.routes

import io.github.smiley4.ktorswaggerui.data.EmptyTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.KTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.OpenApiRequestData
import io.github.smiley4.ktorswaggerui.data.ParameterLocation
import io.github.smiley4.ktorswaggerui.data.TypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker
import kotlin.reflect.KType


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
    fun parameter(location: ParameterLocation, name: String, type: TypeDescriptor, block: OpenApiRequestParameter.() -> Unit) {
        parameters.add(OpenApiRequestParameter(name, type, location).apply(block))
    }


    /**
     * A path parameters that is applicable for this operation
     */
    fun pathParameter(name: String, type: TypeDescriptor, block: OpenApiRequestParameter.() -> Unit) =
        parameter(ParameterLocation.PATH, name, type, block)

//
//    /**
//     * A path parameters that is applicable for this operation
//     */
//    fun pathParameter(name: String, type: KClass<*>) = pathParameter(name, type) {}
//
//
//    /**
//     * A path parameters that is applicable for this operation
//     */
//    inline fun <reified TYPE> pathParameter(name: String) =
//        parameter(ParameterLocation.PATH, name, getSchemaType<TYPE>()) {}
//
//
//    /**
//     * A path parameters that is applicable for this operation
//     */
//    inline fun <reified TYPE> pathParameter(name: String, noinline block: OpenApiRequestParameter.() -> Unit) =
//        parameter(ParameterLocation.PATH, name, getSchemaType<TYPE>(), block)
//

    /**
     * A query parameters that is applicable for this operation
     */
    fun queryParameter(name: String, type: TypeDescriptor, block: OpenApiRequestParameter.() -> Unit) =
        parameter(ParameterLocation.QUERY, name, type, block)


//    /**
//     * A query parameters that is applicable for this operation
//     */
//    fun queryParameter(name: String, type: KClass<*>) = queryParameter(name, type) {}
//
//
//    /**
//     * A query parameters that is applicable for this operation
//     */
//    inline fun <reified TYPE> queryParameter(name: String) =
//        parameter(ParameterLocation.QUERY, name, getSchemaType<TYPE>()) {}
//
//
//    /**
//     * A query parameters that is applicable for this operation
//     */
//    inline fun <reified TYPE> queryParameter(name: String, noinline block: OpenApiRequestParameter.() -> Unit) =
//        parameter(ParameterLocation.QUERY, name, getSchemaType<TYPE>(), block)


    /**
     * A header parameters that is applicable for this operation
     */
    fun headerParameter(name: String, type: TypeDescriptor, block: OpenApiRequestParameter.() -> Unit) =
        parameter(ParameterLocation.HEADER, name, type, block)

//
//    /**
//     * A header parameters that is applicable for this operation
//     */
//    fun headerParameter(name: String, type: SchemaType) = parameter(ParameterLocation.HEADER, name, type) {}
//
//
//    /**
//     * A header parameters that is applicable for this operation
//     */
//    inline fun <reified TYPE> headerParameter(name: String) =
//        parameter(ParameterLocation.HEADER, name, getSchemaType<TYPE>()) {}
//
//
//    /**
//     * A header parameters that is applicable for this operation
//     */
//    inline fun <reified TYPE> headerParameter(name: String, noinline block: OpenApiRequestParameter.() -> Unit) =
//        parameter(ParameterLocation.HEADER, name, getSchemaType<TYPE>(), block)


    private var body: OpenApiBaseBody? = null

    fun getBody() = body


    /**
     * The body returned with this request
     */
    fun body(typeDescriptor: TypeDescriptor, block: OpenApiSimpleBody.() -> Unit) {
        body = OpenApiSimpleBody(typeDescriptor).apply(block)
    }


    /**
     * The body returned with this request
     */
    fun body(typeDescriptor: TypeDescriptor) = body(typeDescriptor) {}


    /**
     * The request body applicable for this operation
     */
    fun body(type: KType, block: OpenApiSimpleBody.() -> Unit) = body(KTypeDescriptor(type), block)


//    /**
//     * The request body applicable for this operation
//     */
//    fun body(type: KClass<*>) = body(type) {}


//    /**
//     * The request body applicable for this operation
//     */
//    fun body(type: KClass<*>, block: OpenApiSimpleBody.() -> Unit) = body(type.asSchemaType(), block)
//
//
//    /**
//     * The request body applicable for this operation
//     */
//    @JvmName("bodyGenericType")
//    inline fun <reified TYPE> body(noinline block: OpenApiSimpleBody.() -> Unit) = body(getSchemaType<TYPE>(), block)
//
//
//    /**
//     * The request body applicable for this operation
//     */
//    inline fun <reified TYPE> body() = body(getSchemaType<TYPE>()) {}


    /**
     * The request body applicable for this operation
     */
    fun body(block: OpenApiSimpleBody.() -> Unit) = body(EmptyTypeDescriptor(), block)


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

    fun build() = OpenApiRequestData(
        parameters = parameters.map { it.build() },
        body = body?.build()
    )

}
