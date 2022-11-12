package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import java.io.File

/**
 * An example of a multipart-body
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost") {

        install(SwaggerUI)

        routing {
            post("test", {
                description = "Simple multipart-upload"
                request {
                    multipartBody {
                        mediaType(ContentType.MultiPart.Mixed)
                        mediaType(ContentType.MultiPart.FormData)
                        part<File>("image") {
                            mediaTypes = setOf(
                                ContentType.Image.PNG,
                                ContentType.Image.JPEG,
                                ContentType.Image.GIF
                            )
                        }
                        part<IntArray>("someNumbers") {
                            header<Int>("sum") {
                                description = "the sum of the given values"
                            }
                        }
                        part<Metadata>("metadata") {
                            mediaTypes = setOf(ContentType.Application.Json)
                        }
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "Successful Response"
                    }
                }
            }) {
                call.respondText("Upload complete")
            }
        }

    }.start(wait = true)
}

data class Metadata(
    val format: String,
    val location: Coords
)

data class Coords(
    val lat: Float,
    val long: Float
)