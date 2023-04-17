package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.specbuilder.RouteMeta
import io.swagger.v3.oas.models.OpenAPI

class OpenApiBuilder(
    private val config: SwaggerUIPluginConfig,
    private val infoBuilder: InfoBuilder,
    private val serverBuilder: ServerBuilder,
    private val tagBuilder: TagBuilder,
    private val pathsBuilder: PathsBuilder
) {

    fun build(routes: Collection<RouteMeta>): OpenAPI =
        OpenAPI().also {
            it.info = infoBuilder.build(config.getInfo())
            it.servers = config.getServers().map { server -> serverBuilder.build(server) }
            it.tags = config.getTags().map { tag -> tagBuilder.build(tag) }
            it.paths = pathsBuilder.build(routes)
            it.components = TODO()
        }

}