package io.github.smiley4.ktorswaggerui.builder.schema

import io.github.smiley4.ktorswaggerui.data.BaseCustomSchema
import io.github.smiley4.ktorswaggerui.data.CustomJsonSchema
import io.github.smiley4.ktorswaggerui.data.CustomOpenApiSchema
import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.github.smiley4.ktorswaggerui.data.RemoteSchema
import io.github.smiley4.ktorswaggerui.dsl.CustomArraySchemaRef
import io.github.smiley4.ktorswaggerui.dsl.CustomSchemaRef
import io.github.smiley4.ktorswaggerui.dsl.OpenApiBaseBody
import io.github.smiley4.ktorswaggerui.dsl.OpenApiMultipartBody
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRequestParameter
import io.github.smiley4.ktorswaggerui.dsl.OpenApiResponse
import io.github.smiley4.ktorswaggerui.dsl.OpenApiSimpleBody
import io.github.smiley4.ktorswaggerui.dsl.SchemaType
import io.github.smiley4.ktorswaggerui.dsl.obj
import io.github.smiley4.ktorswaggerui.builder.route.RouteMeta
import io.swagger.v3.oas.models.media.Schema

class SchemaContextBuilder(
    private val config: PluginConfigData,
    private val schemaBuilder: SchemaBuilder
) {

    fun build(routes: Collection<RouteMeta>): SchemaContext {
        return SchemaContext()
            .also { ctx -> routes.forEach { handle(ctx, it) } }
            .also { ctx ->
                if (config.includeAllCustomSchemas) {
                    config.customSchemas.forEach { (id, schema) ->
                        ctx.addSchema(obj(id), createSchema(schema, false))
                    }
                }
            }
            .also { ctx -> ctx.finalize() }
    }


    private fun handle(ctx: SchemaContext, route: RouteMeta) {
        route.documentation.getRequest().getBody()?.also { handle(ctx, it) }
        route.documentation.getRequest().getParameters().forEach { handle(ctx, it) }
        route.documentation.getResponses().getResponses().forEach { handle(ctx, it) }
    }


    private fun handle(ctx: SchemaContext, response: OpenApiResponse) {
        response.getHeaders().forEach { (_, header) ->
            header.type?.also { headerType ->
                ctx.addSchema(headerType, createSchema(headerType))
            }
        }
        response.getBody()?.also { handle(ctx, it) }
    }


    private fun handle(ctx: SchemaContext, body: OpenApiBaseBody) {
        return when (body) {
            is OpenApiSimpleBody -> handle(ctx, body)
            is OpenApiMultipartBody -> handle(ctx, body)
        }
    }


    private fun handle(ctx: SchemaContext, body: OpenApiSimpleBody) {
        if (body.customSchema != null) {
            body.customSchema?.also { ctx.addSchema(it, createSchema(it)) }
        } else {
            body.type?.also { ctx.addSchema(it, createSchema(it)) }
        }
    }


    private fun handle(ctx: SchemaContext, body: OpenApiMultipartBody) {
        body.getParts().forEach { part ->
            if (part.customSchema != null) {
                part.customSchema?.also { ctx.addSchema(it, createSchema(it)) }
            } else {
                part.type?.also { ctx.addSchema(it, createSchema(it)) }
            }
        }
    }


    private fun handle(ctx: SchemaContext, parameter: OpenApiRequestParameter) {
        ctx.addSchema(parameter.type, createSchema(parameter.type))
    }


    private fun createSchema(type: SchemaType): SchemaDefinitions {
        return schemaBuilder.create(type)
    }


    private fun createSchema(customSchemaRef: CustomSchemaRef): SchemaDefinitions {
        val customSchema = config.customSchemas[customSchemaRef.schemaId]
        return if (customSchema == null) {
            SchemaDefinitions(
                root = Schema<Any>(),
                definitions = emptyMap()
            )
        } else {
            createSchema(customSchema, customSchemaRef is CustomArraySchemaRef)
        }
    }


    private fun createSchema(customSchema: BaseCustomSchema, isArray: Boolean): SchemaDefinitions {
        return when (customSchema) {
            is CustomJsonSchema -> {
                schemaBuilder.create(customSchema.provider())
            }
            is CustomOpenApiSchema -> {
                SchemaDefinitions(
                    // provided schema should not have a 'definitions'-section, i.e. schema should be inline-able as is.
                    root = customSchema.provider(),
                    definitions = emptyMap()
                )
            }
            is RemoteSchema -> {
                SchemaDefinitions(
                    root = Schema<Any>().apply {
                        type = "object"
                        `$ref` = customSchema.url
                    },
                    definitions = emptyMap()
                )
            }
        }.let { schemaDefinitions ->
            if (isArray) {
                SchemaDefinitions(
                    root = Schema<Any>().apply {
                        type = "array"
                        items = schemaDefinitions.root
                    },
                    definitions = schemaDefinitions.definitions
                )
            } else {
                schemaDefinitions
            }
        }
    }

}