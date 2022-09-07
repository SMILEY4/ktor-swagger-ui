package io.github.smiley4.ktorswaggerui.examples

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.routes.get
import io.github.smiley4.ktorswaggerui.routes.delete
import io.github.smiley4.ktorswaggerui.routes.post
import io.github.smiley4.ktorswaggerui.routes.route
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
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
import java.util.Random

/**
 * Arbitrary examples to show (and test) as many features as possible (for authentication see "AuthExample")
 */
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
            tag("random") {
                description = "Routes that return random things"
            }
            tag("math") {
                description = "Routes for math related operations"
            }
            schemasInComponentSection = true
            examplesInComponentSection = true
            automaticTagGenerator = { url -> url.firstOrNull() }
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

            post("math/{operation}", {
                tags = listOf("test")
                description = "Performs the given operation on the given values and returns the result"
                request {
                    pathParameter("operation", String::class) {
                        description = "the math operation to perform. Either 'add' or 'sub'"
                    }
                    body(MathRequest::class) {
                        example("First", MathRequest(13, 19)) {
                            description = "Either an addition of 13 and 19 or a subtraction of 19 from 13"
                        }
                        example("Second", MathRequest(20, 7)) {
                            description = "Either an addition of 20 and 7 or a subtraction of 7 from 20"
                        }
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "The operation was successful"
                        body(MathResult::class) {
                            description = "The result of the operation"
                            example("First", MathResult(42)) {
                                summary = "The first example"
                                description = "For example the result of an addition of 13 and 29"
                            }
                            example("Second", MathResult(-13)) {
                                summary = "The second example"
                                description = "For example the result of an subtracting 20 from 7"
                            }
                        }
                    }
                    HttpStatusCode.BadRequest to {
                        description = "An invalid operation was provided"
                    }
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

            post("random/results", {
                response {
                    HttpStatusCode.OK to {
                        body(Array<MathResult>::class) {
                            mediaType(ContentType.Application.Json)
                            mediaType(ContentType.Application.Xml)
                        }
                    }
                }
            }) {
                call.respond(HttpStatusCode.OK, (0..5).map { MathResult(Random().nextInt()) })
            }

            post("random/numbers", {
                response {
                    HttpStatusCode.OK to {
                        body(IntArray::class)
                    }
                }
            }) {
                call.respond(HttpStatusCode.OK, (0..5).map { Random().nextInt() })
            }

            post("echo/{color}", {
                request {
                    pathParameter("color", Color::class)
                }
                response {
                    HttpStatusCode.OK to {
                        body(String::class)
                    }
                }
            }) {
                call.respond(HttpStatusCode.OK, Color.valueOf(call.parameters["color"]!!).toString())
            }

            route("images", {
                tags = listOf("Image Operations")
                description = "Access images "
            }) {

                post({
                    response {
                        HttpStatusCode.OK to {
                            body {
                                mediaType(ContentType.Image.PNG)
                                mediaType(ContentType.Image.JPEG)
                                mediaType(ContentType.Image.SVG)
                            }
                            header(HttpHeaders.ContentLength, Long::class)
                        }
                    }
                }) {
                    call.respond(HttpStatusCode.NotImplemented, "todo")
                }

                delete("{id}", {
                    description = "Delete the image with the given id."
                    request {
                        pathParameter("id", String::class) {
                            description = "The id of the image to delete"
                        }
                    }
                    response {
                        HttpStatusCode.OK to {
                            description = "The image was deleted"
                        }
                    }
                }) {
                    call.respond(HttpStatusCode.NotImplemented, "todo")
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

enum class Color {
    RED, GREEN, BLUE
}