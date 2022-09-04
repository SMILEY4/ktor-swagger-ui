package io.github.smiley4.ktorswaggerui

import io.github.smiley4.ktorswaggerui.routing.SwaggerRouting
import io.github.smiley4.ktorswaggerui.specbuilder.ApiSpecBuilder
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import io.ktor.server.application.install
import io.ktor.server.application.pluginOrNull
import io.ktor.server.webjars.Webjars

/**
 * This version must match the version of the gradle dependency
 */
internal const val SWAGGER_UI_WEBJARS_VERSION = "4.14.0"

val SwaggerUI = createApplicationPlugin(name = "SwaggerUI", createConfiguration = ::SwaggerUIPluginConfig) {

    var apiSpecJson = "{}"

    on(MonitoringEvent(ApplicationStarted)) { application ->
        if (application.pluginOrNull(Webjars) == null) {
            application.install(Webjars)
        }
        apiSpecJson = ApiSpecBuilder().build(application, pluginConfig)
    }

    SwaggerRouting(
        SWAGGER_UI_WEBJARS_VERSION,
        pluginConfig.getSwaggerUI().swaggerUrl,
        pluginConfig.getSwaggerUI().forwardRoot
    ) { apiSpecJson }.setup(application)

}


