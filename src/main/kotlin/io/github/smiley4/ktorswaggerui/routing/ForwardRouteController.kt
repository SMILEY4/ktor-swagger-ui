package io.github.smiley4.ktorswaggerui.routing

import io.github.smiley4.ktorswaggerui.dsl.SwaggerUIDsl
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

class ForwardRouteController(
    private val appConfig: ApplicationConfig,
    private val swaggerUiConfig: SwaggerUIDsl,
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
            swaggerUiConfig.rootHostPath,
            swaggerUiConfig.swaggerUrl,
        )
            .filter { it.isNotBlank() }
            .map { ControllerUtils.dropSlashes(it) }
            .joinToString("/")
    }

}
