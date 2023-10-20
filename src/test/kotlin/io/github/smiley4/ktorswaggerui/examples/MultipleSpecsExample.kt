package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
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

    install(Authentication) {
        basic("auth-swagger") {
            realm = "Access to the Swagger UI"
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
        // general configuration
        info {
            title = "Example API"
        }
        specAssigner = { _, _ -> "v2" } // assign all unassigned routes to spec "v2" (here e.g. '/hi')

        // configuration specific for spec "v1"
        spec("v1") {
            info {
                version = "1.0"
            }
        }

        // configuration specific for spec "v2"
        spec("v2") {
            info {
                version = "2.0"
            }
            swagger {
                authentication = "auth-swagger"
            }
        }
    }


    routing {

        // version 1.0 routes
        route("v1", {
            specId = "v1" // assign all sub-routes to spec "v1"
        }) {
            get("hello", {
                description = "Simple version 1 'Hello World'-Route"
            }) {
                call.respondText("Hello World!")
            }
        }

        // version 2.0 routes
        route("v2", {
            specId = "v2" // assign all sub-routes to spec "v2"
        }) {
            get("hello", {
                description = "Simple version 2 'Hello World'-Route"
            }) {
                call.respondText("Improved Hello World!")
            }
        }

        // other routes
        get("hi", {
            description = "Alternative version of 'Hello World'-Route"
        }) {
            call.respondText("Alternative Hello World!")
        }

    }
}
