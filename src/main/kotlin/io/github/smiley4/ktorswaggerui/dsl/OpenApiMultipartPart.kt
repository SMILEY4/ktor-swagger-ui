package io.github.smiley4.ktorswaggerui.dsl

import com.fasterxml.jackson.core.type.TypeReference
import io.ktor.http.ContentType
import java.lang.reflect.Type
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

    val type: Type?
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

    /**
     * Possible headers for this part
     */
    fun header(name: String, type: Type, block: OpenApiHeader.() -> Unit) {
        headers[name] = OpenApiHeader().apply(block).apply {
            this.type = type
        }
    }

    /**
     * Possible headers for this part
     */
    fun header(name: String, type: KClass<*>) = header(name, type.java) {}

    /**
     * Possible headers for this part
     */
    inline fun <reified TYPE> header(name: String) = header(name, object : TypeReference<TYPE>() {}.type) {}

    /**
     * Possible headers for this part
     */
    inline fun <reified TYPE> header(name: String, noinline block: OpenApiHeader.() -> Unit) =
        header(name, object : TypeReference<TYPE>() {}.type, block)

    fun getHeaders(): Map<String, OpenApiHeader> = headers

}