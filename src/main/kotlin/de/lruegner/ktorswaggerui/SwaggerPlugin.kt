package de.lruegner.ktorswaggerui

import de.lruegner.ktorswaggerui.routing.SwaggerRouting
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import io.ktor.server.application.install
import io.ktor.server.application.pluginOrNull
import io.ktor.server.webjars.Webjars

val SwaggerUI = createApplicationPlugin(name = "SwaggerUI", createConfiguration = ::SwaggerPluginConfiguration) {
    val forwardRoot = pluginConfig.forwardRoot
    val swaggerUrl = pluginConfig.swaggerUrl

    if (application.pluginOrNull(Webjars) == null) {
        application.install(Webjars)
    }

    on(MonitoringEvent(ApplicationStarted)) { application ->
        ApiSpec.build(application, pluginConfig)
    }

    SwaggerRouting("4.13.2", swaggerUrl, forwardRoot) { ApiSpec.jsonSpec }.setup(application)

}


