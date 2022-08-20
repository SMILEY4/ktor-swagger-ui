package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.AuthScheme
import io.github.smiley4.ktorswaggerui.AuthType
import io.github.smiley4.ktorswaggerui.OpenApiSecuritySchemeConfig
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.documentation.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.basic
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost") {
        install(Authentication) {
            basic {
                realm = "Access to the API"
                validate { credentials ->
                    if (credentials.name == "user" && credentials.password == "pass") {
                        UserIdPrincipal(credentials.name)
                    } else {
                        null
                    }
                }
            }
        }
        install(SwaggerUI) {
            swagger {
                swaggerUrl = "swagger-ui"
                forwardRoot = true
                automaticUnauthorizedResponses = true
                defaultSecuritySchemeName = "MySecurityScheme"
            }
            info {
                title = "Example API"
                version = "latest"
            }
            server {
                url = "http://localhost:8080"
            }
            securityScheme("MySecurityScheme") {
                type = AuthType.HTTP
                scheme = AuthScheme.BASIC
            }
        }

        routing {
            authenticate {
                get("hello", {
                    description = "Protected 'Hello World'-Endpoint"
                    response(HttpStatusCode.OK) {
                        description = "Successful Request"
                        body(String::class.java) { description = "the response" }
                    }
                }) {
                    call.respondText("Hello World!")
                }
            }
        }
    }.start(wait = true)
}
