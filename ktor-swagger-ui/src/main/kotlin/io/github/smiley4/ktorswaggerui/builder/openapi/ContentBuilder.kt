package io.github.smiley4.ktorswaggerui.builder.openapi

import io.github.smiley4.ktorswaggerui.builder.schema.SchemaContext
import io.github.smiley4.ktorswaggerui.data.OpenApiBaseBodyData
import io.github.smiley4.ktorswaggerui.data.OpenApiMultipartBodyData
import io.github.smiley4.ktorswaggerui.data.OpenApiSimpleBodyData
import io.ktor.http.ContentType
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.Encoding
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import kotlin.collections.set

class ContentBuilder(
    private val schemaContext: SchemaContext,
    private val headerBuilder: HeaderBuilder
) {

    fun build(body: OpenApiBaseBodyData): Content =
        when (body) {
            is OpenApiSimpleBodyData -> buildSimpleBody(body)
            is OpenApiMultipartBodyData -> buildMultipartBody(body)
        }

    private fun buildSimpleBody(body: OpenApiSimpleBodyData): Content =
        Content().also { content ->
            buildSimpleMediaTypes(body, schemaContext.getSchema(body.type)).forEach { (contentType, mediaType) ->
                content.addMediaType(contentType.toString(), mediaType)
            }
        }

    private fun buildMultipartBody(body: OpenApiMultipartBodyData): Content {
        return Content().also { content ->
            buildMultipartMediaTypes(body).forEach { (contentType, mediaType) ->
                content.addMediaType(contentType.toString(), mediaType)
            }
        }
    }

    private fun buildSimpleMediaTypes(body: OpenApiSimpleBodyData, schema: Schema<*>?): Map<ContentType, MediaType> {
        val mediaTypes = body.mediaTypes.ifEmpty { schema?.let { setOf(chooseMediaType(schema)) } ?: setOf() }
        return mediaTypes.associateWith { buildSimpleMediaType(schema, body) }
    }

    private fun buildSimpleMediaType(schema: Schema<*>?, body: OpenApiSimpleBodyData): MediaType {
        return MediaType().also {
            it.schema = schema
            body.examples.forEach { (name, _) ->
//                todo
//                exampleContext.getExample(body, name)
//                    ?.also { example -> it.addExamples(name, example) }
            }
        }
    }

    private fun buildMultipartMediaTypes(body: OpenApiMultipartBodyData): Map<ContentType, MediaType> {
        val mediaTypes = body.mediaTypes.ifEmpty { setOf(ContentType.MultiPart.FormData) }
        return mediaTypes.associateWith { buildMultipartMediaType(body) }
    }

    private fun buildMultipartMediaType(body: OpenApiMultipartBodyData): MediaType {
        return MediaType().also { mediaType ->
            mediaType.schema = Schema<Any>().also { schema ->
                schema.type = "object"
                schema.properties = mutableMapOf<String?, Schema<*>?>().also { props ->
                    body.parts.forEach { part ->
                        props[part.name] = schemaContext.getSchema(part.type)
                    }
                }
            }
            mediaType.encoding = buildMultipartEncoding(body)
        }
    }

    private fun buildMultipartEncoding(body: OpenApiMultipartBodyData): MutableMap<String, Encoding>? {
        return if (body.parts.flatMap { it.mediaTypes }.isEmpty()) {
            null
        } else {
            mutableMapOf<String, Encoding>().also { encodings ->
                body.parts
                    .filter { it.mediaTypes.isNotEmpty() || it.headers.isNotEmpty() }
                    .forEach { part ->
                        encodings[part.name] = Encoding().apply {
                            contentType = part.mediaTypes.joinToString(", ") { it.toString() }
                            headers = part.headers.mapValues { headerBuilder.build(it.value) }
                        }
                    }
            }
        }
    }

//    private fun getSchema(body: OpenApiSimpleBodyData): Schema<*>? {
//        return getSchema(body.type)
//    }
//
//    private fun getSchema(part: OpenApiMultipartPartData): Schema<*>? {
//        return getSchema(part.type)
//    }
//
//    private fun getSchema(typeDescriptor: TypeDescriptor): Schema<*>? {
//        return when (typeDescriptor) {
//            is EmptyBodyTypeDescriptor -> {
//                null
//            }
//            is SchemaBodyTypeDescriptor -> {
//                schemaContext.getSchema(typeDescriptor)
//            }
//            is OneOfBodyTypeDescriptor -> {
//                Schema<Any>().also { schema ->
//                    typeDescriptor.elements.forEach {
//                        schema.addOneOfItem(getSchema(it))
//                    }
//                }
//            }
//            is CollectionBodyTypeDescriptor -> {
//                Schema<Any>().also { schema ->
//                    schema.type = "array"
//                    schema.items = getSchema(typeDescriptor.schemaType)
//                }
//            }
//            is CustomRefBodyTypeDescriptor -> {
//                schemaContext.getSchema(typeDescriptor.customSchemaId)
//            }
//        }
//    }

    private fun chooseMediaType(schema: Schema<*>): ContentType {
        return when (schema.type) {
            "integer" -> ContentType.Text.Plain
            "number" -> ContentType.Text.Plain
            "boolean" -> ContentType.Text.Plain
            "string" -> ContentType.Text.Plain
            "object" -> ContentType.Application.Json
            "array" -> ContentType.Application.Json
            null -> ContentType.Application.Json
            else -> ContentType.Text.Plain
        }
    }

}
