package io.github.smiley4.ktorswaggerui.examples

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.documentation.RouteParameter
import io.github.smiley4.ktorswaggerui.documentation.get
import io.github.smiley4.ktorswaggerui.documentation.post
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
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
        install(ContentNegotiation) {
            jackson {
                configure(SerializationFeature.INDENT_OUTPUT, true)
                setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                    indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                    indentObjectsWith(DefaultIndenter("  ", "\n"))
                })
            }
        }
        routing {
            get("hello", {
                description = "Hello World Endpoint"
                response(HttpStatusCode.OK) {
                    description = "Successful Request"
                    textBody { description = "the response" }
                }
                response(HttpStatusCode.InternalServerError) {
                    description = "Something unexpected happened"
                }
            }) {
                call.respondText("Hello World!")
            }
            post("math/{operation}", {
                description = "Performs the given operation on the given values and returns the result"
                pathParameter {
                    name = "operation"
                    description = "the math operation to perform. Either 'add' or 'sub'"
                    schema(RouteParameter.Type.STRING)
                }
                typedRequestBody(MathRequest::class.java) {}
                response(HttpStatusCode.OK) {
                    description = "The operation was successful"
                    typedBody(MathResult::class.java) {
                        description = "The result of the operation"
                    }
                }
                response(HttpStatusCode.BadRequest) {
                    description = "An invalid operation was provided"
                }
            }) {
                val operation = call.parameters["operation"]!!
                call.receive<MathRequest>().let { request ->
                    when (operation) {
                        "add" -> call.respond(HttpStatusCode.OK, MathResult(request.a + request.b))
                        "sub" -> call.respond(HttpStatusCode.OK, MathResult(request.a - request.b))
                        else -> call.respond(HttpStatusCode.BadRequest, Unit)
                    }
                }
            }
        }
    }.start(wait = true)
}

data class MathRequest(
    val a: Int,
    val b: Int
)

data class MathResult(
    val value: Int
)
