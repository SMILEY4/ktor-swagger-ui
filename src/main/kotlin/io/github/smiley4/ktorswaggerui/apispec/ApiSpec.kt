package io.github.smiley4.ktorswaggerui.apispec

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.ktor.server.application.Application
import io.swagger.v3.core.util.Json
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI


object ApiSpec {

    var jsonSpec: String = ""

    fun build(application: Application, config: SwaggerUIPluginConfig) {
        val openAPI = OpenAPI().apply {
            info = OApiInfoGenerator().generate(config.getInfo())
            servers = OApiServersGenerator().generate(config.getServers())
            if (config.getSecuritySchemes().isNotEmpty()) {
                components = Components().apply {
                    securitySchemes = OApiSecuritySchemesGenerator().generate(config.getSecuritySchemes())
                }
            }
            tags = OApiTagsGenerator().generate(config.getTags())
            paths = OApiPathsGenerator().generate(config, application)
        }
        jsonSpec = Json.pretty(openAPI)
    }

}
