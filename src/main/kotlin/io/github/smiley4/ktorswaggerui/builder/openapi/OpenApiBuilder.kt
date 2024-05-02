package io.github.smiley4.ktorswaggerui.builder.openapi

import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.github.smiley4.ktorswaggerui.builder.route.RouteMeta
import io.github.smiley4.ktorswaggerui.builder.schema.SchemaContext
import io.swagger.v3.oas.models.OpenAPI

class OpenApiBuilder(
    private val config: PluginConfigData,
    private val schemaContext: SchemaContext,
    private val infoBuilder: InfoBuilder,
    private val externalDocumentationBuilder: ExternalDocumentationBuilder,
    private val serverBuilder: ServerBuilder,
    private val tagBuilder: TagBuilder,
    private val pathsBuilder: PathsBuilder,
    private val componentsBuilder: ComponentsBuilder,
) {

    fun build(routes: Collection<RouteMeta>): OpenAPI {
        return OpenAPI().also {
            it.info = infoBuilder.build(config.info)
            it.externalDocs = externalDocumentationBuilder.build(config.externalDocs)
            it.servers = config.servers.map { server -> serverBuilder.build(server) }
            it.tags = config.tags.map { tag -> tagBuilder.build(tag) }
            it.paths = pathsBuilder.build(routes)
//            it.components = componentsBuilder.build(schemaContext.getComponentSection(), exampleContext.getComponentsSection()) // todo
            it.components = componentsBuilder.build(schemaContext.getComponentSection(), emptyMap())
        }
    }

}
