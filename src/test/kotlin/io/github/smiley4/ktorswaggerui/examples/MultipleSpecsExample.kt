package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing

/**
 * An example showcasing multiple openapi-specs in a single application
 * - localhost:8080/swagger-ui/v1/index.html
 *      * /v1/hello
 * - localhost:8080/swagger-ui/v2/index.html
 *      * /v2/hello
 *      * /hi
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {
    install(SwaggerUI) {
        specAssigner = { _, _ -> "v2" }
    }
    routing {
        route("v1", {
            specId = "v1"
        }) {
            get("hello", {
                description = "Simple version 1 'Hello World'-Route"
            }) {
                call.respondText("Hello World!")
            }
        }

        route("v2", {
            specId = "v2"
        }) {
            get("hello", {
                description = "Simple version 2 'Hello World'-Route"
            }) {
                call.respondText("Improved Hello World!")
            }
        }

        get("hi", {
            description = "Alternative version of 'Hello World'-Route"
        }) {
            call.respondText("Alternative Hello World!")
        }

    }
}
