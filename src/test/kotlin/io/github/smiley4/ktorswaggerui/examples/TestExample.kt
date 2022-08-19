package de.lruegner.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.documentation.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import org.slf4j.event.Level

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost") {

        install(CallLogging) {
            level = Level.INFO
            format { call ->
                val status = call.response.status()
                val httpMethod = call.request.httpMethod.value
                val route = call.request.uri.replace(Regex("token=.*?(?=(&|\$))"), "token=SECRET")
                "${status.toString()}: $httpMethod - $route"
            }
        }

        install(SwaggerUI) {
            swagger {
                swaggerUrl = "swagger-ui"
                forwardRoot = true
            }
            info {
                title = "Sample API"
                version = "latest"
                description = "Sample API for testing and demonstration purposes."
                contact {
                    name = "Example Name"
                    url = "https://www.example.com"
                }
            }
            server {
                url = "localhost:8080"
                description = "Development Server"
            }
            server {
                url = "127.0.0.1:8080"
                description = "Same Development Server"
            }
        }

        routing {
            get("hello", {
                description = "Hello World Endpoint"
                response(HttpStatusCode.OK, "Successful Request")
            }) {
                call.respondText("Hello World!")
            }
        }

    }.start(wait = true)
}
