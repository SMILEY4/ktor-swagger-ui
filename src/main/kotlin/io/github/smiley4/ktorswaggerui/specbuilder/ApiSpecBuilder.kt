package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.ktor.server.application.Application
import io.swagger.v3.core.util.Json
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI

/**
 * Build the OpenApi-json for the given application
 */
class ApiSpecBuilder {

    fun build(application: Application, config: SwaggerUIPluginConfig): String {
        val componentCtx = ComponentsContext(
            config.schemasInComponentSection, mutableMapOf(),
            config.examplesInComponentSection, mutableMapOf()
        )
        val openAPI = OpenAPI().apply {
            info = OApiInfoGenerator().generate(config.getInfo())
            servers = OApiServersGenerator().generate(config.getServers())
            if (config.getSecuritySchemes().isNotEmpty()) {
                components = Components().apply {
                    securitySchemes = OApiSecuritySchemesGenerator().generate(config.getSecuritySchemes())
                }
            }
            tags = OApiTagsGenerator().generate(config.getTags())
            paths = OApiPathsGenerator(RouteCollector()).generate(config, application, componentCtx)
            components = OApiComponentsGenerator().generate(componentCtx)
        }
        return Json.pretty(openAPI)
    }

}