package io.github.smiley4.ktorswaggerui.dsl.routes

import io.github.smiley4.ktorswaggerui.data.OpenApiMultipartPartData
import io.github.smiley4.ktorswaggerui.data.TypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker
import io.ktor.http.ContentType

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

    val type: TypeDescriptor
) {

    /**
     * Set a specific content type for this part
     */
    var mediaTypes: Collection<ContentType> = setOf()

    private val headers = mutableMapOf<String, OpenApiHeader>()


    /**
     * Possible headers for this part
     */
    fun header(name: String, type: TypeDescriptor, block: OpenApiHeader.() -> Unit) {
        headers[name] = OpenApiHeader().apply(block).apply {
            this.type = type
        }
    }


//    /**
//     * Possible headers for this part
//     */
//    fun header(name: String, type: KClass<*>, block: OpenApiHeader.() -> Unit) = header(name, KTypeDescriptor(type.starProjectedType), block)
//

//    /**
//     * Possible headers for this part
//     */
//    fun header(name: String, type: KClass<*>) = header(name, type) {}

//
//    /**
//     * Possible headers for this part
//     */
//    inline fun <reified TYPE> header(name: String) = header(name, KTypeDescriptor(getKType<TYPE>())) {}
//
//
//    /**
//     * Possible headers for this part
//     */
//    inline fun <reified TYPE> header(name: String, noinline block: OpenApiHeader.() -> Unit) = header(name, getSchemaType<TYPE>(), block)


    fun build() = OpenApiMultipartPartData(
        name = name,
        type = type,
        mediaTypes = mediaTypes.toSet(),
        headers = headers.mapValues { it.value.build() }
    )

}
