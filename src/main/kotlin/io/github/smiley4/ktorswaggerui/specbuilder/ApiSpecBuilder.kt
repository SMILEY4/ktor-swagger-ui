package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.ktor.server.application.Application
import io.swagger.v3.core.util.Json
import io.swagger.v3.oas.models.OpenAPI

/**
 * Build the OpenApi-json for the given application
 */
class ApiSpecBuilder {

    private val infoBuilder = OApiInfoBuilder()
    private val serversBuilder = OApiServersBuilder()
    private val tagsBuilder = OApiTagsBuilder()
    private val pathsBuilder = OApiPathsBuilder(RouteCollector())
    private val componentsBuilder = OApiComponentsBuilder()


    fun build(application: Application, config: SwaggerUIPluginConfig): String {
        val componentCtx = ComponentsContext(
            config.schemasInComponentSection, mutableMapOf(),
            config.examplesInComponentSection, mutableMapOf()
        )
        val openAPI = OpenAPI().apply {
            info = infoBuilder.build(config.getInfo())
            servers = serversBuilder.build(config.getServers())
            tags = tagsBuilder.build(config.getTags())
            paths = pathsBuilder.build(config, application, componentCtx)
            components = componentsBuilder.build(componentCtx, config.getSecuritySchemes())
        }
        return Json.pretty(openAPI)
    }

}