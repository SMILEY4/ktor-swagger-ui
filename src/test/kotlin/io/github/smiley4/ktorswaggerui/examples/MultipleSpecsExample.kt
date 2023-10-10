package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing

/**
 * An example showcasing multiple openapi-specs in a single application
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {
    install(SwaggerUI) {
        specAssigner = { _, tags -> tags.firstOrNull() ?: "other" }
    }
    routing {
        get("v1/hello", {
            description = "Simple version 1 'Hello World'- Route"
            tags = listOf("v1")
        }) {
            call.respondText("Hello World!")
        }
        get("v2/hello", {
            description = "Simple version 2 'Hello World'- Route"
            tags = listOf("v2")
        }) {
            call.respondText("Improved Hello World!")
        }
    }
}
