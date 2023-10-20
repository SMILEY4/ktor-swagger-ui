package io.github.smiley4.ktorswaggerui.routing

import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

class ForwardRouteController(
    private val appConfig: ApplicationConfig,
    private val swaggerUiConfig: PluginConfigData,
) {

    fun setup(app: Application) {
        app.routing {
            get {
                call.respondRedirect("${getRootUrl()}/index.html")
            }
        }
    }

    private fun getRootUrl(): String {
        return "/" + listOf(
            ControllerUtils.getRootPath(appConfig),
            swaggerUiConfig.swaggerUI.rootHostPath,
            swaggerUiConfig.swaggerUI.swaggerUrl,
        )
            .filter { it.isNotBlank() }
            .map { ControllerUtils.dropSlashes(it) }
            .joinToString("/")
    }

}
