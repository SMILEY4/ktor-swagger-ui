package io.github.smiley4.ktorswaggerui.spec.schema

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.CustomArraySchemaRef
import io.github.smiley4.ktorswaggerui.dsl.CustomJsonSchema
import io.github.smiley4.ktorswaggerui.dsl.CustomObjectSchemaRef
import io.github.smiley4.ktorswaggerui.dsl.CustomOpenApiSchema
import io.github.smiley4.ktorswaggerui.dsl.CustomSchemaRef
import io.github.smiley4.ktorswaggerui.dsl.OpenApiBaseBody
import io.github.smiley4.ktorswaggerui.dsl.OpenApiMultipartBody
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRequestParameter
import io.github.smiley4.ktorswaggerui.dsl.OpenApiResponse
import io.github.smiley4.ktorswaggerui.dsl.OpenApiSimpleBody
import io.github.smiley4.ktorswaggerui.dsl.RemoteSchema
import io.github.smiley4.ktorswaggerui.spec.route.RouteMeta
import io.github.smiley4.ktorswaggerui.spec.schema.JsonSchemaBuilder.Companion.OpenApiSchemaInfo
import io.swagger.v3.oas.models.media.Schema
import java.lang.reflect.Type
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class SchemaContext(
    private val config: SwaggerUIPluginConfig,
    private val jsonSchemaBuilder: JsonSchemaBuilder
) {

    private val schemas = mutableMapOf<String, OpenApiSchemaInfo>()
    private val customSchemas = mutableMapOf<String, Schema<*>>()


    fun initialize(routes: Collection<RouteMeta>): SchemaContext {
        routes.forEach { handle(it) }
        config.getDefaultUnauthorizedResponse()?.also { handle(it) }
        return this
    }


    private fun handle(route: RouteMeta) {
        route.documentation.getRequest().getBody()?.also { handle(it) }
        route.documentation.getRequest().getParameters().forEach { handle(it) }
        route.documentation.getResponses().getResponses().forEach { handle(it) }
    }


    private fun handle(response: OpenApiResponse) {
        response.getHeaders().forEach { (_, header) ->
            header.type?.also { headerType ->
                createSchema(headerType)
            }
        }
        response.getBody()?.also { handle(it) }
    }


    private fun handle(body: OpenApiBaseBody) {
        return when (body) {
            is OpenApiSimpleBody -> handle(body)
            is OpenApiMultipartBody -> handle(body)
        }
    }


    private fun handle(body: OpenApiSimpleBody) {
        if (body.customSchema != null) {
            body.customSchema?.also { createSchema(it) }
        } else {
            body.type?.also { createSchema(it) }
        }
    }


    private fun handle(body: OpenApiMultipartBody) {
        body.getParts().forEach { part ->
            if (part.customSchema != null) {
                part.customSchema?.also { createSchema(it) }
            } else {
                part.type?.also { createSchema(it) }
            }
        }
    }


    private fun handle(parameter: OpenApiRequestParameter) {
        createSchema(parameter.type)
    }


    private fun createSchema(type: Type) {
        if (schemas.containsKey(type.typeName)) {
            return
        }
        schemas[type.typeName] = jsonSchemaBuilder.build(type)
    }


    private fun createSchema(customSchemaRef: CustomSchemaRef) {
        if (customSchemas.containsKey(customSchemaRef.schemaId)) {
            return
        }
        val customSchema = config.getCustomSchemas().getSchema(customSchemaRef.schemaId)
        if (customSchema == null) {
            customSchemas[customSchemaRef.schemaId] = Schema<Any>()
        } else {
            when (customSchema) {
                is CustomJsonSchema -> {
                    jsonSchemaBuilder.build(ObjectMapper().readTree(customSchema.provider())).let {
                        it.schemas[it.rootSchema]!!
                    }
                }
                is CustomOpenApiSchema -> {
                    customSchema.provider()
                }
                is RemoteSchema -> {
                    Schema<Any>().apply {
                        type = "object"
                        `$ref` = customSchema.url
                    }
                }
            }.let { schema ->
                when (customSchemaRef) {
                    is CustomObjectSchemaRef -> schema
                    is CustomArraySchemaRef -> Schema<Any>().apply {
                        this.type = "array"
                        this.items = schema
                    }
                }
            }.also {
                customSchemas[customSchemaRef.schemaId] = it
            }
        }
    }


    fun getComponentSection(): Map<String, Schema<*>> {
        val componentSection = mutableMapOf<String, Schema<*>>()
        schemas.forEach { (_, schemaInfo) ->
            val rootSchema = schemaInfo.schemas[schemaInfo.rootSchema]!!
            if (isPrimitive(rootSchema) || isPrimitiveArray(rootSchema)) {
                // skip
            } else if (isWrapperArray(rootSchema)) {
                schemaInfo.schemas.toMutableMap()
                    .also { it.remove(schemaInfo.rootSchema) }
                    .also { componentSection.putAll(it) }
            } else {
                componentSection.putAll(schemaInfo.schemas)
            }
        }
        customSchemas.forEach { (schemaId, schema) ->
            componentSection[schemaId] = schema
        }
        return componentSection
    }


    fun getSchema(customSchemaRef: CustomSchemaRef): Schema<*> {
        val schema = customSchemas[customSchemaRef.schemaId]
            ?: throw IllegalStateException("Could not retrieve schema for type '${customSchemaRef.schemaId}'")
        return buildInlineSchema(customSchemaRef.schemaId, schema, 1)
    }


    fun getSchema(type: Type): Schema<*> {
        val schemaInfo = getSchemaInfo(type)
        val rootSchema = schemaInfo.schemas[schemaInfo.rootSchema]!!
        return buildInlineSchema(schemaInfo.rootSchema, rootSchema, schemaInfo.schemas.size)
    }


    private fun buildInlineSchema(schemaId: String, schema: Schema<*>, connectedSchemaCount: Int): Schema<*> {
        if (isPrimitive(schema) && connectedSchemaCount == 1) {
            return schema
        }
        if (isPrimitiveArray(schema) && connectedSchemaCount == 1) {
            return schema
        }
        if (isWrapperArray(schema)) {
            return Schema<Any>().also { wrapper ->
                wrapper.type = "array"
                wrapper.items = Schema<Any>().also {
                    it.`$ref` = schema.items.`$ref`
                }
            }
        }
        return Schema<Any>().also {
            it.`$ref` = "#/components/schemas/$schemaId"
        }
    }


    private fun getSchemaInfo(type: Type): OpenApiSchemaInfo {
        return type.typeName.let { typeName ->
            schemas[typeName] ?: throw IllegalStateException("Could not retrieve schema for type '${typeName}'")
        }
    }


    private fun isPrimitive(schema: Schema<*>): Boolean {
        return schema.type != "object" && schema.type != "array" && schema.type != null
    }

    private fun isPrimitiveArray(schema: Schema<*>): Boolean {
        return schema.type == "array" && (isPrimitive(schema.items) || isPrimitiveArray(schema.items))
    }

    private fun isWrapperArray(schema: Schema<*>): Boolean {
        return schema.type == "array" && schema.items.type == null && schema.items.`$ref` != null
    }

}
