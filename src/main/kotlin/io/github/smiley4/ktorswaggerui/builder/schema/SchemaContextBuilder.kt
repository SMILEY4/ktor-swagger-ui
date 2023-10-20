package io.github.smiley4.ktorswaggerui.builder.schema

import io.github.smiley4.ktorswaggerui.builder.route.RouteMeta
import io.github.smiley4.ktorswaggerui.data.BaseCustomSchema
import io.github.smiley4.ktorswaggerui.data.CustomJsonSchema
import io.github.smiley4.ktorswaggerui.data.CustomOpenApiSchema
import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.github.smiley4.ktorswaggerui.data.RemoteSchema
import io.github.smiley4.ktorswaggerui.dsl.BodyTypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.CollectionBodyTypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.CustomRefBodyTypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.EmptyBodyTypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.OneOfBodyTypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.OpenApiBaseBody
import io.github.smiley4.ktorswaggerui.dsl.OpenApiMultipartBody
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRequestParameter
import io.github.smiley4.ktorswaggerui.dsl.OpenApiResponse
import io.github.smiley4.ktorswaggerui.dsl.OpenApiSimpleBody
import io.github.smiley4.ktorswaggerui.dsl.SchemaBodyTypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.SchemaType
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
                        ctx.addSchema(id, createSchema(schema))
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
        addSchemas(ctx, body.type)
    }

    private fun handle(ctx: SchemaContext, body: OpenApiMultipartBody) {
        body.getParts().forEach { part ->
            part.type.also { addSchemas(ctx, part.type) }
        }
    }

    private fun addSchemas(ctx: SchemaContext, typeDescriptor: BodyTypeDescriptor) {
        when (typeDescriptor) {
            is EmptyBodyTypeDescriptor -> Unit
            is SchemaBodyTypeDescriptor -> {
                ctx.addSchema(typeDescriptor.schemaType, createSchema(typeDescriptor.schemaType))
            }
            is CollectionBodyTypeDescriptor -> {
                addSchemas(ctx, typeDescriptor.schemaType)
            }
            is OneOfBodyTypeDescriptor -> {
                typeDescriptor.elements.forEach { addSchemas(ctx, it) }
            }
            is CustomRefBodyTypeDescriptor -> {
                ctx.addSchema(typeDescriptor.customSchemaId, createSchema(typeDescriptor.customSchemaId))
            }
        }
    }


    private fun handle(ctx: SchemaContext, parameter: OpenApiRequestParameter) {
        ctx.addSchema(parameter.type, createSchema(parameter.type))
    }


    private fun createSchema(type: SchemaType): SchemaDefinitions {
        return schemaBuilder.create(type)
    }


    private fun createSchema(customSchemaId: String): SchemaDefinitions {
        val customSchema = config.customSchemas[customSchemaId]
        return if (customSchema == null) {
            SchemaDefinitions(
                root = Schema<Any>(),
                definitions = emptyMap()
            )
        } else {
            createSchema(customSchema)
        }
    }


    private fun createSchema(customSchema: BaseCustomSchema): SchemaDefinitions {
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
        }
    }

}
