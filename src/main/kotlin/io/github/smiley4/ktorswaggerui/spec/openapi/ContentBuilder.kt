package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.dsl.*
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaContext
import io.ktor.http.*
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.Encoding
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema

class ContentBuilder(
    private val schemaContext: SchemaContext,
    private val exampleBuilder: ExampleBuilder,
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

    private fun buildSimpleMediaTypes(body: OpenApiSimpleBody, schema: Schema<*>): Map<ContentType, MediaType> {
        val mediaTypes = body.getMediaTypes().ifEmpty { setOf(chooseMediaType(schema)) }
        return mediaTypes.associateWith { buildSimpleMediaType(schema, body.getExamples()) }
    }

    private fun buildSimpleMediaType(schema: Schema<*>, examples: Map<String, OpenApiExample>): MediaType {
        return MediaType().also {
            it.schema = schema
            examples.forEach { (name, obj) ->
                it.addExamples(name, exampleBuilder.build(obj))
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
                        props[part.name] = getSchema(part)
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

    private fun getSchema(body: OpenApiSimpleBody): Schema<*> {
        return if (body.customSchema != null) {
            schemaContext.getSchema(body.customSchema!!)
        } else {
            schemaContext.getSchema(body.type!!)
        }
    }

    private fun getSchema(part: OpenapiMultipartPart): Schema<*> {
        return if (part.customSchema != null) {
            schemaContext.getSchema(part.customSchema!!)
        } else {
            schemaContext.getSchema(part.type!!)
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