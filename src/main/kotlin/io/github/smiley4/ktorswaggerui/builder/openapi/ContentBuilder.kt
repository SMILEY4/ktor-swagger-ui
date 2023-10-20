package io.github.smiley4.ktorswaggerui.builder.openapi

import io.github.smiley4.ktorswaggerui.builder.example.ExampleContext
import io.github.smiley4.ktorswaggerui.builder.schema.SchemaContext
import io.github.smiley4.ktorswaggerui.dsl.BodyTypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.CollectionBodyTypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.CustomRefBodyTypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.EmptyBodyTypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.OneOfBodyTypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.OpenApiBaseBody
import io.github.smiley4.ktorswaggerui.dsl.OpenApiMultipartBody
import io.github.smiley4.ktorswaggerui.dsl.OpenApiMultipartPart
import io.github.smiley4.ktorswaggerui.dsl.OpenApiSimpleBody
import io.github.smiley4.ktorswaggerui.dsl.SchemaBodyTypeDescriptor
import io.ktor.http.ContentType
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.Encoding
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import kotlin.collections.set

class ContentBuilder(
    private val schemaContext: SchemaContext,
    private val exampleContext: ExampleContext,
    private val headerBuilder: HeaderBuilder
) {

    fun build(body: OpenApiBaseBody): Content =
        when (body) {
            is OpenApiSimpleBody -> buildSimpleBody(body)
            is OpenApiMultipartBody -> buildMultipartBody(body)
        }

    private fun buildSimpleBody(body: OpenApiSimpleBody): Content =
        Content().also { content ->
            buildSimpleMediaTypes(body, getSchema(body)).forEach { (contentType, mediaType) ->
                content.addMediaType(contentType.toString(), mediaType)
            }
        }

    private fun buildMultipartBody(body: OpenApiMultipartBody): Content {
        return Content().also { content ->
            buildMultipartMediaTypes(body).forEach { (contentType, mediaType) ->
                content.addMediaType(contentType.toString(), mediaType)
            }
        }
    }

    private fun buildSimpleMediaTypes(body: OpenApiSimpleBody, schema: Schema<*>?): Map<ContentType, MediaType> {
        val mediaTypes = body.getMediaTypes().ifEmpty { schema?.let { setOf(chooseMediaType(schema)) } ?: setOf() }
        return mediaTypes.associateWith { buildSimpleMediaType(schema, body) }
    }

    private fun buildSimpleMediaType(schema: Schema<*>?, body: OpenApiSimpleBody): MediaType {
        return MediaType().also {
            it.schema = schema
            body.getExamples().forEach { (name, _) ->
                exampleContext.getExample(body, name)
                    ?.also { example -> it.addExamples(name, example) }
            }
        }
    }

    private fun buildMultipartMediaTypes(body: OpenApiMultipartBody): Map<ContentType, MediaType> {
        val mediaTypes = body.getMediaTypes().ifEmpty { setOf(ContentType.MultiPart.FormData) }
        return mediaTypes.associateWith { buildMultipartMediaType(body) }
    }

    private fun buildMultipartMediaType(body: OpenApiMultipartBody): MediaType {
        return MediaType().also { mediaType ->
            mediaType.schema = Schema<Any>().also { schema ->
                schema.type = "object"
                schema.properties = mutableMapOf<String?, Schema<*>?>().also { props ->
                    body.getParts().forEach { part ->
                        getSchema(part)?.also {
                            props[part.name] = getSchema(part)
                        }
                    }
                }
            }
            mediaType.encoding = buildMultipartEncoding(body)
        }
    }

    private fun buildMultipartEncoding(body: OpenApiMultipartBody): MutableMap<String, Encoding>? {
        return if (body.getParts().flatMap { it.mediaTypes }.isEmpty()) {
            null
        } else {
            mutableMapOf<String, Encoding>().also { encodings ->
                body.getParts()
                    .filter { it.mediaTypes.isNotEmpty() || it.getHeaders().isNotEmpty() }
                    .forEach { part ->
                        encodings[part.name] = Encoding().apply {
                            contentType = part.mediaTypes.joinToString(", ") { it.toString() }
                            headers = part.getHeaders().mapValues { headerBuilder.build(it.value) }
                        }
                    }
            }
        }
    }

    private fun getSchema(body: OpenApiSimpleBody): Schema<*>? {
        return getSchema(body.type)
    }

    private fun getSchema(part: OpenApiMultipartPart): Schema<*>? {
        return getSchema(part.type)
    }

    private fun getSchema(typeDescriptor: BodyTypeDescriptor): Schema<*>? {
        return when (typeDescriptor) {
            is EmptyBodyTypeDescriptor -> {
                null
            }
            is SchemaBodyTypeDescriptor -> {
                schemaContext.getSchema(typeDescriptor.schemaType)
            }
            is OneOfBodyTypeDescriptor -> {
                Schema<Any>().also { schema ->
                    typeDescriptor.elements.forEach {
                        schema.addOneOfItem(getSchema(it))
                    }
                }
            }
            is CollectionBodyTypeDescriptor -> {
                Schema<Any>().also { schema ->
                    schema.type = "array"
                    schema.items = getSchema(typeDescriptor.schemaType)
                }
            }
            is CustomRefBodyTypeDescriptor -> {
                schemaContext.getSchema(typeDescriptor.customSchemaId)
            }
        }
    }

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
