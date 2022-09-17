package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.routing

/**
 * An example for defining custom json-schemas
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost") {

        install(SwaggerUI) {
            customSchemas {
                custom("myRequestData") {
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
                custom("myResponseData") {
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
                }
            }
        }

        routing {
            get("something", {
                request {
                    body("myRequestData")
                }
                response {
                    HttpStatusCode.OK to {
                        body("myResponseData")
                    }
                }
            }) {
                val text = call.receive<MyRequestData>().someText
                call.respond(HttpStatusCode.OK, MyResponseData(text, 42))
            }

        }

    }.start(wait = true)
}


data class MyRequestData(
    val someText: String,
    val someBoolean: Boolean
)

data class MyResponseData(
    val someText: String,
    val someNumber: Long
)
