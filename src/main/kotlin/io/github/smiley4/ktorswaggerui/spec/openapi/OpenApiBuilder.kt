package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.spec.example.ExampleContext
import io.github.smiley4.ktorswaggerui.spec.route.RouteMeta
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaContext
import io.swagger.v3.oas.models.OpenAPI

class OpenApiBuilder(
    private val config: SwaggerUIPluginConfig,
    private val schemaContext: SchemaContext,
    private val exampleContext: ExampleContext,
    private val infoBuilder: InfoBuilder,
    private val serverBuilder: ServerBuilder,
    private val tagBuilder: TagBuilder,
    private val pathsBuilder: PathsBuilder,
    private val componentsBuilder: ComponentsBuilder,
) {

    fun build(routes: Collection<RouteMeta>): OpenAPI {
        return OpenAPI().also {
            it.info = infoBuilder.build(config.getInfo())
            it.servers = config.getServers().map { server -> serverBuilder.build(server) }
            it.tags = config.getTags().map { tag -> tagBuilder.build(tag) }
            it.paths = pathsBuilder.build(routes)
            it.components = componentsBuilder.build(schemaContext.getComponentsSection(), exampleContext.getComponentsSection())
        }
    }

}