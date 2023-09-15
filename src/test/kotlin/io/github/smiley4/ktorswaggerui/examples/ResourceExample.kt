package io.github.smiley4.ktorswaggerui.examples

import io.ktor.server.application.Application
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.*
import io.ktor.http.HttpStatusCode
import io.ktor.resources.*
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.*

/**
 * Example to showcase usage with the resources plugin
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

@Resource("articles")
class ArticlesRoute

data class Article(
    val title: String,
    val content: String
)

private fun Application.myModule() {

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
        externalDocs {
            url = "https://github.com/SMILEY4/ktor-swagger-ui/wiki"
            description = "Sample external documentation object"
        }
        server {
            url = "http://localhost:8080"
            description = "Development Server"
        }
        tag("articles") {
            description = "Routes that return articles"
        }
        generateTags { url -> listOf(url.firstOrNull()) }
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
        get<ArticlesRoute>({
            tags = listOf("articles")
            description = "Articles endpoint"
            operationId = "get-articles"
            response {
                default {
                    description = "Default Response"
                }
                HttpStatusCode.OK to {
                    description = "Successful Request"
                    body<String> { description = "the response" }
                }
                HttpStatusCode.InternalServerError to {
                    description = "Something unexpected happened"
                }
            }
        }) {
            call.respond(HttpStatusCode.OK, "No articles yet")
        }

        post<ArticlesRoute>({
            tags = listOf("articles")
            description = "Creates a new article"
            operationId = "createArticle"
            request {
                body<Article> {
                    example("First", Article("Ktor openapi resources", "ktor now support openapi for resources!")) {
                        description = "Create a ktor article"
                    }
                }
            }
            response {
                default {
                    description = "Default Response"
                }
                HttpStatusCode.OK to {
                    description = "Successful Request"
                    body<String> { description = "the response" }
                }
                HttpStatusCode.InternalServerError to {
                    description = "Something unexpected happened"
                }
            }
        }) {
            call.respond(HttpStatusCode.OK, "Article not saved ^^")
        }
    }
}
