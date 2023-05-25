package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.dsl.OpenApiBaseBody
import io.github.smiley4.ktorswaggerui.dsl.OpenApiExample
import io.github.smiley4.ktorswaggerui.dsl.OpenApiMultipartBody
import io.github.smiley4.ktorswaggerui.dsl.OpenApiSimpleBody
import io.github.smiley4.ktorswaggerui.dsl.OpenApiMultipartPart
import io.github.smiley4.ktorswaggerui.dsl.SchemaType
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaContext
import io.ktor.http.ContentType
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.Encoding
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import kotlin.collections.Map
import kotlin.collections.MutableMap
import kotlin.collections.associateWith
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.filter
import kotlin.collections.flatMap
import kotlin.collections.forEach
import kotlin.collections.ifEmpty
import kotlin.collections.isNotEmpty
import kotlin.collections.joinToString
import kotlin.collections.mapValues
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.collections.setOf

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

    private fun buildSimpleMediaTypes(body: OpenApiSimpleBody, schema: Schema<*>?): Map<ContentType, MediaType> {
        val mediaTypes = body.getMediaTypes().ifEmpty { schema?.let { setOf(chooseMediaType(schema)) } ?: setOf() }
        return mediaTypes.associateWith { buildSimpleMediaType(schema, body.type, body.getExamples()) }
    }

    private fun buildSimpleMediaType(schema: Schema<*>?, type: SchemaType?, examples: Map<String, OpenApiExample>): MediaType {
        return MediaType().also {
            it.schema = schema
            examples.forEach { (name, obj) ->
                it.addExamples(name, exampleBuilder.build(type, obj))
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
        return if (body.customSchema != null) {
            schemaContext.getSchema(body.customSchema!!)
        } else if (body.type != null) {
            schemaContext.getSchema(body.type)
        } else {
            null
        }
    }

    private fun getSchema(part: OpenApiMultipartPart): Schema<*>? {
        return if (part.customSchema != null) {
            schemaContext.getSchema(part.customSchema!!)
        } else if (part.type != null) {
            schemaContext.getSchema(part.type)
        } else {
            null
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