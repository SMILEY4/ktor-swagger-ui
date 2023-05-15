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
    private val customSchemas = mutableMapOf<CustomSchemaRef, Schema<*>>()


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
        if(customSchemas.containsKey(customSchemaRef)) {
            return
        }
        val customSchema = config.getCustomSchemas().getSchema(customSchemaRef.schemaId)
        if (customSchema == null) {
            customSchemas[customSchemaRef] = Schema<Any>()
        } else {
            when (customSchema) {
                is CustomJsonSchema -> {
                    jsonSchemaBuilder.build(ObjectMapper().readTree(customSchema.provider())).let {
                        it.schemas[it.rootSchema]
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
            }
        }
    }


    fun getComponentSection(): Map<String, Schema<*>> {
        val componentSection = mutableMapOf<String, Schema<*>>()
        schemas.forEach { (schemaId, schemaInfo) ->
            val rootSchema = schemaInfo.schemas[schemaInfo.rootSchema]!!
            if(schemaInfo.schemas.size == 1 && (isPrimitive(rootSchema) || isPrimitiveArray(rootSchema))) {
                // skip
            } else {
                componentSection.putAll(schemaInfo.schemas)
            }
        }
        return componentSection
    }




    fun getSchema(customSchemaRef: CustomSchemaRef): Schema<*> {
        return customSchemas[customSchemaRef]
            ?: throw IllegalStateException("Could not retrieve schema for type '${customSchemaRef.schemaId}'")
    }


    fun getSchema(type: Type): Schema<*> {
        println("Get schema for type '$type'")
        val schemaInfo = getSchemaInfo(type)
        val rootSchema = schemaInfo.schemas[schemaInfo.rootSchema]!!

        if(isPrimitive(rootSchema) && schemaInfo.schemas.size == 1) {
            println("-> is primitive, return root-schema")
            return rootSchema
        }
        if(isPrimitiveArray(rootSchema) && schemaInfo.schemas.size == 1) {
            println("-> is primitive-array, return root-schema")
            return rootSchema
        }
        println("-> is complex, return ref '#/components/schemas/${schemaInfo.rootSchema}'")
        return Schema<Any>().also {
            it.`$ref` = "#/components/schemas/${schemaInfo.rootSchema}"
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
