package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.BodyTypeDescriptor.Companion.custom
import io.github.smiley4.ktorswaggerui.dsl.BodyTypeDescriptor.Companion.multipleOf
import io.github.smiley4.ktorswaggerui.dsl.BodyTypeDescriptor.Companion.oneOf
import io.github.smiley4.ktorswaggerui.dsl.BodyTypeDescriptor.Companion.typeOf
import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import java.awt.SystemColor.text

/**
 * An example for defining custom json-schemas
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {

    data class MyRequestData(
        val someText: String,
        val someBoolean: Boolean
    )

    data class MyResponseData(
        val someText: String,
        val someNumber: Long
    )

    data class Rectangle(
        val width: Int,
        val height: Int
    )

    data class Circle(
        val radius: Int
    )

    data class Point(
        val x: Int,
        val y: Int
    )

    install(SwaggerUI) {
        // don't show the test-routes providing json-schemas
        pathFilter = { _, url -> url.firstOrNull() != "schema" }
        customSchemas {
            // specify a custom json-schema with the id 'myRequestData'
            json("myRequestData") {
                """
                        {
                            "type": "object",
                            "properties": {
                                "someBoolean": {
                                    "type": "boolean"
                                },
                                "someText": {
                                    "type": "string"
                                }
                            }
                        }
                    """.trimIndent()
            }
            // specify a remote json-schema with the id 'myRequestData'
            remote("myResponseData", "http://localhost:8080/schema/myResponseData")
        }
    }

    routing {

        get("something", {
            request {
                // body referencing the custom schema with id 'myRequestData'
                body("myRequestData")
            }
            response {
                HttpStatusCode.OK to {
                    // body referencing the custom schema with id 'myResponseData'
                    body("myResponseData")
                }
            }
        }) {
            val text = call.receive<MyRequestData>().someText
            call.respond(HttpStatusCode.OK, MyResponseData(text, 42))
        }

        get("something/many", {
            request {
                // body referencing the custom schema with id 'myRequestData'
                body(multipleOf(custom("myRequestData")))
            }
            response {
                HttpStatusCode.OK to {
                    // body referencing the custom schema with id 'myResponseData'
                    body(multipleOf(custom("myResponseData")))
                }
            }
        }) {
            val text = call.receive<MyRequestData>().someText
            call.respond(HttpStatusCode.OK, MyResponseData(text, 42))
        }

        get("oneof/shapes", {
            request {
                // body allowing a mixed list of rectangles, circles and points
                body(
                    multipleOf(
                        oneOf(
                            typeOf(Rectangle::class),
                            typeOf(Circle::class),
                            typeOf(Point::class),
                        )
                    )
                )
            }
        }) {
            call.respond(HttpStatusCode.OK, Unit)
        }

        // (external) endpoint providing a json-schema
        get("schema/myResponseData") {
            call.respondText(
                """
                    {
                        "type": "object",
                        "properties": {
                            "someNumber": {
                                "type": "integer",
                                "format": "int64"
                            },
                            "someText": {
                                "type": "string"
                            }
                        }
                    }
                """.trimIndent()
            )
        }
    }
}
