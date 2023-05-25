package io.github.smiley4.ktorswaggeruiexamples

import io.ktor.server.application.Application
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.ContentType
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
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}


private fun Application.myModule() {

    data class Coords(
        val lat: Float,
        val long: Float
    )

    data class Metadata(
        val format: String,
        val location: Coords
    )

    install(SwaggerUI)

    routing {
        post("example", {
            request {
                multipartBody {
                    mediaType(ContentType.MultiPart.FormData)
                    part<File>("image") {
                        mediaTypes = setOf(
                            ContentType.Image.PNG,
                            ContentType.Image.JPEG,
                            ContentType.Image.GIF
                        )
                    }
                    part<Metadata>("metadata") {
                        mediaTypes = setOf(ContentType.Application.Json)
                    }
                }
            }
        }) {
            call.respondText("Upload complete")
        }
    }
}
