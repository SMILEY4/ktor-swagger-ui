package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
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

private fun Application.myModule() {

    // Install and customize the "SwaggerUI"-Plugin
    install(SwaggerUI) {
        examples {

            // specify two shared examples
            example("Shared A") {
                description = "first shared example"
                value = MyExampleClass(
                    someValue = "shared a"
                )
            }
            example("Shared B") {
                description = "second shared example"
                value = MyExampleClass(
                    someValue = "shared b"
                )
            }

        }
    }

    routing {

        // add the routes for swagger-ui and api-spec
        route("swagger") {
            swaggerUI("/api.json")
        }
        route("api.json") {
            openApiSpec()
        }


        get("basic", {
            request {
                body<MyExampleClass> {
                    // specify two example values
                    example("Example 1") {
                            description = "A first example value"
                            value = MyExampleClass(
                                someValue = "example 1"
                            )
                    }
                    example("Example 2") {
                        description = "A second example value"
                        value = MyExampleClass(
                            someValue = "example 2"
                        )
                    }
                }
            }
        }) {
            call.respondText("...")
        }


        get("reference-shared", {
            request {
                body<MyExampleClass> {
                    // reference two shared examples specified in the plugin-config (and placed in the component section)
                    exampleRef("Example 1", "Shared A")
                    exampleRef("Example 2", "Shared B")
                }
            }
        }) {
            call.respondText("...")
        }

    }

}


private data class MyExampleClass(
    val someValue: String
)
