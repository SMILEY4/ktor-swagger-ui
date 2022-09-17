package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.CustomJsonSchema
import io.github.smiley4.ktorswaggerui.dsl.CustomOpenApiSchema
import io.github.smiley4.ktorswaggerui.dsl.CustomSchemas
import io.github.smiley4.ktorswaggerui.dsl.OpenApiBody
import io.github.smiley4.ktorswaggerui.dsl.OpenApiExample
import io.github.smiley4.ktorswaggerui.dsl.RemoteSchema
import io.ktor.http.ContentType
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.XML
import java.lang.reflect.Type

/**
 * Builder for the OpenAPI Content Object (e.g. request and response bodies)
 */
class OApiContentBuilder {

    private val schemaBuilder = OApiSchemaBuilder()
    private val exampleBuilder = OApiExampleBuilder()
    private val jsonToSchemaConverter = JsonToOpenApiSchemaConverter()


    fun build(body: OpenApiBody, components: ComponentsContext, config: SwaggerUIPluginConfig): Content {
        return Content().apply {
            val maybeSchemaObj = buildSchema(body, components, config)
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


    private fun buildSchema(body: OpenApiBody, components: ComponentsContext, config: SwaggerUIPluginConfig): Schema<Any>? {
        return if (body.customSchemaId != null) {
            buildSchemaFromCustom(body.customSchemaId!!, components, config.getCustomSchemas())
        } else {
            buildSchemaFromType(body.type, components, config)
        }
    }


    private fun buildSchemaFromType(type: Type?, components: ComponentsContext, config: SwaggerUIPluginConfig): Schema<Any>? {
        return type
            ?.let { schemaBuilder.build(it, components, config) }
            ?.let { prepareForXml(type, it) }
    }


    private fun buildSchemaFromCustom(customSchemaId: String, components: ComponentsContext, customSchemas: CustomSchemas): Schema<Any> {
        val custom = customSchemas.getSchema(customSchemaId)
        if (custom == null) {
            return Schema<Any>()
        } else {
            return when (custom) {
                is CustomJsonSchema -> {
                    val schema = jsonToSchemaConverter.toSchema(custom.provider())
                    components.addSchema(customSchemaId, schema)
                }
                is CustomOpenApiSchema -> {
                    components.addSchema(customSchemaId, custom.provider())
                }
                is RemoteSchema -> {
                    Schema<Any>().apply {
                        type = "object"
                        `$ref` = custom.url
                    }
                }
            }
        }
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
            null -> ContentType.Application.Json
            else -> ContentType.Text.Plain
        }
    }


    private fun prepareForXml(type: Type, schema: Schema<Any>): Schema<Any> {
        schema.xml = XML().apply {
            if (type is Class<*>) {
                name = if (type.isArray) {
                    type.componentType.simpleName
                } else {
                    type.simpleName
                }
            }
        }
        return schema
    }

}