package io.github.smiley4.ktorswaggerui.dsl.routes

import io.github.smiley4.ktorswaggerui.data.KTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.OpenApiRequestData
import io.github.smiley4.ktorswaggerui.data.ParameterLocation
import io.github.smiley4.ktorswaggerui.data.SwaggerTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.TypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker
import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Describes a single request.
 */
@OpenApiDslMarker
class OpenApiRequest {

    /**
     * A list of parameters that are applicable for this operation
     */
    val parameters = mutableListOf<OpenApiRequestParameter>()


    /**
     * A path parameters that is applicable for this operation
     */
    fun parameter(location: ParameterLocation, name: String, type: TypeDescriptor, block: OpenApiRequestParameter.() -> Unit = {}) {
        parameters.add(OpenApiRequestParameter(name, type, location).apply(block))
    }


    /**
     * A path parameters that is applicable for this operation
     */
    fun pathParameter(name: String, type: TypeDescriptor, block: OpenApiRequestParameter.() -> Unit = {}) =
        parameter(ParameterLocation.PATH, name, type, block)


    /**
     * A path parameters that is applicable for this operation
     */
    fun pathParameter(name: String, type: Schema<*>, block: OpenApiRequestParameter.() -> Unit = {}) =
        parameter(ParameterLocation.PATH, name, SwaggerTypeDescriptor(type), block)


    /**
     * A path parameters that is applicable for this operation
     */
    fun pathParameter(name: String, type: KType, block: OpenApiRequestParameter.() -> Unit = {}) =
        parameter(ParameterLocation.PATH, name, KTypeDescriptor(type), block)


    /**
     * A path parameters that is applicable for this operation
     */
    inline fun <reified T> pathParameter(name: String, noinline block: OpenApiRequestParameter.() -> Unit = {}) =
        parameter(ParameterLocation.PATH, name, KTypeDescriptor(typeOf<T>()), block)


    /**
     * A query parameters that is applicable for this operation
     */
    fun queryParameter(name: String, type: TypeDescriptor, block: OpenApiRequestParameter.() -> Unit = {}) =
        parameter(ParameterLocation.QUERY, name, type, block)


    /**
     * A query parameters that is applicable for this operation
     */
    fun queryParameter(name: String, type: Schema<*>, block: OpenApiRequestParameter.() -> Unit = {}) =
        parameter(ParameterLocation.QUERY, name, SwaggerTypeDescriptor(type), block)


    /**
     * A query parameters that is applicable for this operation
     */
    fun queryParameter(name: String, type: KType, block: OpenApiRequestParameter.() -> Unit = {}) =
        parameter(ParameterLocation.QUERY, name, KTypeDescriptor(type), block)


    /**
     * A query parameters that is applicable for this operation
     */
    inline fun <reified T> queryParameter(name: String, noinline block: OpenApiRequestParameter.() -> Unit = {}) =
        parameter(ParameterLocation.QUERY, name, KTypeDescriptor(typeOf<T>()), block)


    /**
     * A header parameters that is applicable for this operation
     */
    fun headerParameter(name: String, type: TypeDescriptor, block: OpenApiRequestParameter.() -> Unit = {}) =
        parameter(ParameterLocation.HEADER, name, type, block)


    /**
     * A header parameters that is applicable for this operation
     */
    fun headerParameter(name: String, type: Schema<*>, block: OpenApiRequestParameter.() -> Unit = {}) =
        parameter(ParameterLocation.HEADER, name, SwaggerTypeDescriptor(type), block)


    /**
     * A header parameters that is applicable for this operation
     */
    fun headerParameter(name: String, type: KType, block: OpenApiRequestParameter.() -> Unit = {}) =
        parameter(ParameterLocation.HEADER, name, KTypeDescriptor(type), block)


    /**
     * A header parameters that is applicable for this operation
     */
    inline fun <reified T> headerParameter(name: String, noinline block: OpenApiRequestParameter.() -> Unit = {}) =
        parameter(ParameterLocation.HEADER, name, KTypeDescriptor(typeOf<T>()), block)


    /**
     * A cookie parameters that is applicable for this operation
     */
    fun cookieParameter(name: String, type: TypeDescriptor, block: OpenApiRequestParameter.() -> Unit = {}) =
        parameter(ParameterLocation.COOKIE, name, type, block)


    /**
     * A cookie parameters that is applicable for this operation
     */
    fun cookieParameter(name: String, type: Schema<*>, block: OpenApiRequestParameter.() -> Unit = {}) =
        parameter(ParameterLocation.COOKIE, name, SwaggerTypeDescriptor(type), block)


    /**
     * A cookie parameters that is applicable for this operation
     */
    fun cookieParameter(name: String, type: KType, block: OpenApiRequestParameter.() -> Unit = {}) =
        parameter(ParameterLocation.COOKIE, name, KTypeDescriptor(type), block)


    /**
     * A cookie parameters that is applicable for this operation
     */
    inline fun <reified T> cookieParameter(name: String, noinline block: OpenApiRequestParameter.() -> Unit = {}) =
        parameter(ParameterLocation.COOKIE, name, KTypeDescriptor(typeOf<T>()), block)


    private var body: OpenApiBaseBody? = null

    fun getBody() = body


    /**
     * The body returned with this request
     */
    fun body(type: TypeDescriptor, block: OpenApiSimpleBody.() -> Unit = {}) {
        val result = OpenApiSimpleBody(type).apply(block)
        if (!result.isEmptyBody()) {
            body = result
        }
    }


    /**
     * The body returned with this request
     */
    fun body(type: Schema<*>, block: OpenApiSimpleBody.() -> Unit = {}) = body(SwaggerTypeDescriptor(type), block)


    /**
     * The body returned with this request
     */
    fun body(type: KType, block: OpenApiSimpleBody.() -> Unit = {}) = body(KTypeDescriptor(type), block)


    /**
     * The body returned with this request
     */
    inline fun <reified T> body(noinline block: OpenApiSimpleBody.() -> Unit = {}) = body(KTypeDescriptor(typeOf<T>()), block)


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


    /**
     * Build the data object for this config.
     */
    fun build() = OpenApiRequestData(
        parameters = parameters.map { it.build() },
        body = body?.build()
    )

    private fun OpenApiBaseBody.isEmptyBody(): Boolean {
        return when (this) {
            is OpenApiSimpleBody -> when (type) {
                is KTypeDescriptor -> type.type == typeOf<Unit>()
                else -> false
            }
            is OpenApiMultipartBody -> false
        }
    }

}
