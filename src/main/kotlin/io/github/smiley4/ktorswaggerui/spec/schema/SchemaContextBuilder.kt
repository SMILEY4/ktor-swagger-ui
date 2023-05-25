package io.github.smiley4.ktorswaggerui.spec.schema

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.*
import io.github.smiley4.ktorswaggerui.spec.route.RouteMeta
import io.swagger.v3.oas.models.media.Schema

class SchemaContextBuilder(
    private val config: SwaggerUIPluginConfig,
    private val schemaBuilder: SchemaBuilder
) {

    fun build(routes: Collection<RouteMeta>): SchemaContext {
        return SchemaContext()
            .also { ctx -> routes.forEach { handle(ctx, it) } }
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
        val customSchema = config.getCustomSchemas().getSchema(customSchemaRef.schemaId)
        if (customSchema == null) {
            return SchemaDefinitions(
                root = Schema<Any>(),
                definitions = emptyMap()
            )
        } else {
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
                when (customSchemaRef) {
                    is CustomObjectSchemaRef -> schemaDefinitions
                    is CustomArraySchemaRef -> {
                        SchemaDefinitions(
                            root = Schema<Any>().apply {
                                type = "array"
                                items = schemaDefinitions.root
                            },
                            definitions = schemaDefinitions.definitions
                        )
                    }
                }
            }
        }
    }

}