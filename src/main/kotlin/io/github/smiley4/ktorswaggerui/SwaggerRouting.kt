package io.github.smiley4.ktorswaggerui

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import mu.KotlinLogging

/**
 * Registers and handles routes required for the swagger-ui
 */
class SwaggerRouting(
    private val swaggerUrl: String,
    private val forwardRoot: Boolean,
    private val authentication: String?,
    swaggerWebjarVersion: String,
    jsonSpecProvider: () -> String
) {

    private val logger = KotlinLogging.logger {}

    private val controller = SwaggerController(
        swaggerWebjarVersion = swaggerWebjarVersion,
        apiSpecUrl = "/" + (if (swaggerUrl.startsWith("/")) swaggerUrl.substring(1) else swaggerUrl) + "/api.json",
        jsonSpecProvider = jsonSpecProvider
    )


    /**
     * registers the required routes
     */
    fun setup(app: Application) {
        logger.info("Registering routes for swagger-ui: $swaggerUrl (forwardRoot=$forwardRoot)")
        app.routing {
            if (forwardRoot) {
                get("/") {
                    call.respondRedirect("$swaggerUrl/index.html")
                }
            }
            if (authentication == null) {
                setupSwaggerRoutes()
            } else {
                authenticate(authentication) {
                    setupSwaggerRoutes()
                }
            }
        }
    }

    private fun Route.setupSwaggerRoutes() {
        route(swaggerUrl) {
            get {
                call.respondRedirect("$swaggerUrl/index.html")
            }
            get("api.json") {
                controller.serveOpenApiSpec(call)
            }
            get("{filename}") {
                controller.serverSwaggerUI(call)
            }
        }
    }

}
