package io.github.smiley4.ktorswaggerui

import io.github.smiley4.ktorswaggerui.apispec.ApiSpec
import io.github.smiley4.ktorswaggerui.routing.SwaggerRouting
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import io.ktor.server.application.install
import io.ktor.server.application.pluginOrNull
import io.ktor.server.webjars.Webjars

val SwaggerUI = createApplicationPlugin(name = "SwaggerUI", createConfiguration = ::SwaggerUIPluginConfig) {

    on(MonitoringEvent(ApplicationStarted)) { application ->
        if (application.pluginOrNull(Webjars) == null) {
            application.install(Webjars)
        }
        ApiSpec.build(application, pluginConfig)
    }

    SwaggerRouting(
        "4.13.2",
        pluginConfig.getSwaggerUI().swaggerUrl,
        pluginConfig.getSwaggerUI().forwardRoot
    ) { ApiSpec.jsonSpec }.setup(application)

}


