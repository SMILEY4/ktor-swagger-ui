package io.github.smiley4.ktorswaggerui

import io.github.smiley4.ktorswaggerui.dsl.routing.resources.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing

/**
 * A minimal working example
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {
    install(SwaggerUI)

    routing {
        get("hello", {
            request {
                body<Int> {
                    example("example", 42)
                }
            }
            description = "Simple 'Hello World'- Route"
            response {
                HttpStatusCode.OK to {
                    body<MyClass>()
                    description = "Successful Response"
                }
            }
        }) {
            call.respondText("Hello World!")
        }
    }
}


class MyClass(
    val size: Int,
    val tags: List<String>
)