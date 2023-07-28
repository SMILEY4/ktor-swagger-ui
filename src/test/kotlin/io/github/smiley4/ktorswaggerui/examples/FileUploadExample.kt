package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import java.io.File

/**
 * An example showcasing file uploads
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {

    install(SwaggerUI)

    routing {

		post("single", {
			request {
				body<File> {
					mediaType(ContentType.Image.PNG)
					mediaType(ContentType.Image.JPEG)
					mediaType(ContentType.Image.SVG)
				}
			}
		}) {
			call.respond(HttpStatusCode.NotImplemented, "...")
		}

        post("multipart", {
            request {
                multipartBody {
                    mediaType(ContentType.MultiPart.FormData)
                    part<File>("firstImage") {
                        mediaTypes = setOf(
                            ContentType.Image.PNG,
                            ContentType.Image.JPEG,
                            ContentType.Image.GIF
                        )
                    }
                    part<File>("secondImage") {
                        mediaTypes = setOf(
                            ContentType.Image.PNG,
                            ContentType.Image.JPEG,
                            ContentType.Image.GIF
                        )
                    }
                    part<String>("name")
                }
            }
        }) {
            call.respond(HttpStatusCode.NotImplemented, "...")
        }

    }

}
