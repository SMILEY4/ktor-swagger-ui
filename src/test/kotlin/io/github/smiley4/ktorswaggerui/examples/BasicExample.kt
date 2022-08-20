package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.documentation.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost") {
        install(SwaggerUI) {
            swagger {
                swaggerUrl = "swagger-ui"
                forwardRoot = true
            }
            info {
                title = "Example API"
                version = "latest"
                description = "Example API for testing and demonstration purposes."
            }
            server {
                url = "http://localhost:8080"
                description = "Development Server"
            }
        }
        routing {
            get("hello", {
                tags = listOf("test")
                description = "Hello World Endpoint"
                response {
                    HttpStatusCode.OK to {
                        description = "Successful Request"
                        body(String::class) { description = "the response" }
                    }
                    HttpStatusCode.InternalServerError to {
                        description = "Something unexpected happened"
                    }
                }
            }) {
                call.respondText("Hello World!")
            }
        }
    }.start(wait = true)
}
