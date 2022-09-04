package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.dsl.OpenApiBody
import io.github.smiley4.ktorswaggerui.dsl.OpenApiExample
import io.ktor.http.ContentType
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.XML
import kotlin.reflect.KClass

/**
 * Generator for the OpenAPI Content Object (e.g. request and response bodies)
 */
class OApiContentBuilder(
    private val schemaBuilder: OApiSchemaBuilder,
    private val exampleBuilder: OApiExampleBuilder
) {

    fun build(body: OpenApiBody, components: ComponentsContext): Content {
        return Content().apply {
            val maybeSchemaObj = buildSchema(body, components)
            body.getMediaTypes().forEach { mediaType ->
                if (maybeSchemaObj == null) {
                    addMediaType(mediaType.toString(), MediaType())
                } else {
                    addMediaType(mediaType.toString(), buildMediaType(body.getExamples(), maybeSchemaObj, components))
                }
            }
            if (body.getMediaTypes().isEmpty() && maybeSchemaObj != null) {
                addMediaType(chooseMediaType(maybeSchemaObj).toString(), buildMediaType(body.getExamples(), maybeSchemaObj, components))
            }
        }
    }


    private fun buildSchema(body: OpenApiBody, components: ComponentsContext): Schema<Any>? {
        return body.schema
            ?.let { schemaBuilder.build(it, components) }
            ?.let { prepareForXml(body.schema, it) }
    }


    private fun buildMediaType(examples: Map<String, OpenApiExample>, schema: Schema<*>, components: ComponentsContext): MediaType {
        return MediaType().apply {
            this.schema = schema
            examples.forEach { (name, obj) ->
                addExamples(name, exampleBuilder.build(name, obj, components))
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
            else -> ContentType.Text.Plain
        }
    }


    private fun prepareForXml(type: KClass<*>, schema: Schema<Any>): Schema<Any> {
        schema.xml = XML().apply {
            name = if (type.java.isArray) {
                type.java.componentType.simpleName
            } else {
                type.simpleName
            }
        }
        return schema
    }

}