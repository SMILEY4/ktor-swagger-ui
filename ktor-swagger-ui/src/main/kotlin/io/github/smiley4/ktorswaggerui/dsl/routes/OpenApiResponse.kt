package io.github.smiley4.ktorswaggerui.dsl.routes

import io.github.smiley4.ktorswaggerui.data.KTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.OpenApiResponseData
import io.github.smiley4.ktorswaggerui.data.SwaggerTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.TypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker
import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.KType
import kotlin.reflect.typeOf

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

    val headers = mutableMapOf<String, OpenApiHeader>()


    /**
     * Possible headers returned with this response
     */
    fun header(name: String, type: TypeDescriptor, block: OpenApiHeader.() -> Unit = {}) {
        headers[name] = OpenApiHeader().apply(block).apply {
            this.type = type
        }
    }


    /**
     * Possible headers returned with this response
     */
    fun header(name: String, type: Schema<*>, block: OpenApiHeader.() -> Unit = {}) = header(name, SwaggerTypeDescriptor(type), block)


    /**
     * Possible headers returned with this response
     */
    fun header(name: String, type: KType, block: OpenApiHeader.() -> Unit = {}) = header(name, KTypeDescriptor(type), block)


    /**
     * Possible headers returned with this response
     */
    inline fun <reified T> header(name: String, noinline block: OpenApiHeader.() -> Unit = {}) =
        header(name, KTypeDescriptor(typeOf<T>()), block)


    private var body: OpenApiBaseBody? = null


    /**
     * The body returned with this response
     */
    fun body(type: TypeDescriptor, block: OpenApiSimpleBody.() -> Unit = {}) {
        body = OpenApiSimpleBody(type).apply(block)
    }

    /**
     * The body returned with this response
     */
    fun body(type: Schema<*>, block: OpenApiSimpleBody.() -> Unit = {}) = body(SwaggerTypeDescriptor(type), block)

    /**
     * The body returned with this response
     */
    fun body(type: KType, block: OpenApiSimpleBody.() -> Unit = {}) = body(KTypeDescriptor(type), block)

    /**
     * The body returned with this response
     */
    inline fun <reified T> body(noinline block: OpenApiSimpleBody.() -> Unit = {}) = body(KTypeDescriptor(typeOf<T>()), block)




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
