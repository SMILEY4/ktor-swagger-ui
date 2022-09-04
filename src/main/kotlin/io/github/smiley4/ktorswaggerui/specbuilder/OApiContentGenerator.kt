package io.github.smiley4.ktorswaggerui.specbuilder

import io.ktor.http.ContentType
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.XML
import kotlin.reflect.KClass

/**
 * Generator for the OpenAPI Content Object (e.g. request and response bodies)
 */
class OApiContentGenerator {

    /**
     * Generate the Content Object from the given config
     */
    fun generate(config: OpenApiBody, componentCtx: ComponentsContext): Content {
        return Content().apply {
            val schemaObj = config.schema
                ?.let { OApiSchemaGenerator().generate(it, componentCtx) }
                ?.let { prepareForXml(config.schema, it) }

            config.getMediaTypes().forEach { mediaType ->
                if (schemaObj == null) {
                    addMediaType(mediaType.toString(), MediaType())
                } else {
                    addMediaType(mediaType.toString(), MediaType().apply {
                        schema = schemaObj
                        config.getExamples().forEach { (name, obj) ->
                            addExamples(name, OApiExampleGenerator().generate(name, obj, componentCtx))
                        }
                    })
                }
            }
            if (config.getMediaTypes().isEmpty() && schemaObj != null) {
                addMediaType(pickMediaType(schemaObj).toString(), MediaType().apply {
                    schema = schemaObj
                    config.getExamples().forEach { (name, obj) ->
                        addExamples(name, OApiExampleGenerator().generate(name, obj, componentCtx))
                    }
                })
            }
        }
    }

    private fun pickMediaType(schema: Schema<*>): ContentType {
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