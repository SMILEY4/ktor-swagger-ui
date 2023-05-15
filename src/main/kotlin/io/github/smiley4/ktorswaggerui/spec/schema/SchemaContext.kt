package io.github.smiley4.ktorswaggerui.spec.schema

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.*
import io.github.smiley4.ktorswaggerui.spec.schema.JsonSchemaBuilder.Companion.OpenApiSchemaInfo
import io.github.smiley4.ktorswaggerui.spec.route.RouteMeta
import io.swagger.v3.oas.models.media.Schema
import java.lang.reflect.Type

class SchemaContext(
    private val config: SwaggerUIPluginConfig,
    private val jsonSchemaBuilder: JsonSchemaBuilder
) {

    private val schemas = mutableMapOf<String, OpenApiSchemaInfo>()
    private val customSchemas = mutableMapOf<String, Schema<*>>()


    fun initialize(routes: Collection<RouteMeta>) {
        routes.forEach { handle(it) }
        config.getDefaultUnauthorizedResponse()?.also { handle(it) }
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
        if(schemas.containsKey(type.typeName)) {
            return
        }
        schemas[type.typeName] = jsonSchemaBuilder.build(type)
    }


    private fun createSchema(customSchemaRef: CustomSchemaRef) {
        if(customSchemas.containsKey(customSchemaRef.schemaId)) {
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
            if(schemaInfo.schemas.size == 1 && (isPrimitive(rootSchema) || isPrimitiveArray(rootSchema))) {
                // skip
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
        if(isPrimitive(schema) && connectedSchemaCount == 1) {
            return schema
        }
        if(isPrimitiveArray(schema) && connectedSchemaCount == 1) {
            return schema
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
        return schema.type != "object" && schema.type != "array"
    }

    private fun isPrimitiveArray(schema: Schema<*>): Boolean {
        return schema.type == "array" && (isPrimitive(schema.items) || isPrimitiveArray(schema.items)) // todo: check if recursive nested-nested-arrays really work
    }

}
