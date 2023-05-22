package io.github.smiley4.ktorswaggerui.dsl

import io.ktor.http.ContentType
import kotlin.reflect.KClass

/**
 * Describes one section of a multipart-body.
 * See https://swagger.io/docs/specification/describing-request-body/multipart-requests/ for more info
 */
@OpenApiDslMarker
class OpenApiMultipartPart(
    /**
     * The name of this part
     */
    val name: String,

    val type: SchemaType?
) {

    /**
     * reference to a custom schema (alternative to 'type')
     */
    var customSchema: CustomSchemaRef? = null


    /**
     * Set a specific content type for this part
     */
    var mediaTypes: Collection<ContentType> = setOf()

    private val headers = mutableMapOf<String, OpenApiHeader>()

    fun getHeaders(): Map<String, OpenApiHeader> = headers


    /**
     * Possible headers for this part
     */
    fun header(name: String, type: SchemaType, block: OpenApiHeader.() -> Unit) {
        headers[name] = OpenApiHeader().apply(block).apply {
            this.type = type
        }
    }

    /**
     * Possible headers for this part
     */
    fun header(name: String, type: KClass<*>, block: OpenApiHeader.() -> Unit)  = header(name, type.asSchemaType(), block)

    /**
     * Possible headers for this part
     */
    fun header(name: String, type: KClass<*>) = header(name, type) {}


    /**
     * Possible headers for this part
     */
    inline fun <reified TYPE> header(name: String) = header(name, getSchemaType<TYPE>()) {}


    /**
     * Possible headers for this part
     */
    inline fun <reified TYPE> header(name: String, noinline block: OpenApiHeader.() -> Unit) = header(name, getSchemaType<TYPE>(), block)


}