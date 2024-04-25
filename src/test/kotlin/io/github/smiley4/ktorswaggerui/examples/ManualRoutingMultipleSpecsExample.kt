package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.route
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * An example showcasing manual routing with multiple openapi-specs in a single application
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
        swagger {
            automaticRouter = false
        }
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

        route("api") {
            route("version-1.json") {
                openApiSpec("v1")
            }
            route("version-2.json") {
                openApiSpec("v2")
            }
        }

        route("swagger") {
            route("version-1") {
                swaggerUI("/api/version-1.json")
            }
            route("version-2") {
                swaggerUI("/api/version-2.json")
            }
        }

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
