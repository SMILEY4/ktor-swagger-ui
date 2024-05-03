package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}


/**
 * A "minimal" working example
 */
private fun Application.myModule() {

    // Install the "SwaggerUI"-Plugin and use the default configuration
    install(SwaggerUI)

    routing {

        // Create a route for the swagger-ui pointing the to the openapi-spec at "api.json.
        // This route will not be included in the spec.
        route("swagger") {
            swaggerUI("/api.json")
        }
        // Create a route for the openapi-spec file.
        // This route will not be included in the spec.
        route("api.json") {
            openApiSpec()
        }

        // a documented route
        get("hello", {
            // description of the route
            description = "A Hello-World route"
            // information about possible responses
            response {
                // information about a "200 OK" response
                HttpStatusCode.OK to {
                    // a description of the response
                    description = "successful request - always returns 'Hello World!'"
                }
            }
        }) {
            call.respondText("Hello World!")
        }

    }

}