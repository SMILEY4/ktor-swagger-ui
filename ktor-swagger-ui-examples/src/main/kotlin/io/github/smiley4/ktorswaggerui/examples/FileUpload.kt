package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.data.KTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.SwaggerTypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.swagger.v3.oas.models.media.Schema
import java.io.File
import kotlin.reflect.typeOf

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {

    // Install the "SwaggerUI"-Plugin and use the default configuration
    install(SwaggerUI) {
        schemas {
            // overwrite type "File" with custom schema for binary data
            overwrite[typeOf<File>()] = SwaggerTypeDescriptor(
                Schema<Any>().also {
                    it.type = "string"
                    it.format = "binary"
                }
            )
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

        // upload a single file, either as png, jpeg or svg
        post("single", {
            request {
                body(KTypeDescriptor(typeOf<File>())) {
                    mediaType(ContentType.Image.PNG)
                    mediaType(ContentType.Image.JPEG)
                    mediaType(ContentType.Image.SVG)
                }
            }
        }) {
            call.respond(HttpStatusCode.NotImplemented, "...")
        }

        // upload multiple files
        post("multipart", {
            request {
                multipartBody {
                    mediaType(ContentType.MultiPart.FormData)
                    part("first-image", KTypeDescriptor(typeOf<File>())) {
                        mediaTypes = setOf(
                            ContentType.Image.PNG, // todo: why setOf here and not for single body?
                            ContentType.Image.JPEG,
                            ContentType.Image.SVG
                        )
                    }
                    part("second-image", KTypeDescriptor(typeOf<File>())) {
                        mediaTypes = setOf(
                            ContentType.Image.PNG,
                            ContentType.Image.JPEG,
                            ContentType.Image.SVG
                        )
                    }
                }
            }
        }) {
            call.respond(HttpStatusCode.NotImplemented, "...")
        }

    }

}
