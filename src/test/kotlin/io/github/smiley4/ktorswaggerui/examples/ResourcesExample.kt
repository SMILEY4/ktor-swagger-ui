package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.github.smiley4.ktorswaggerui.routes.OpenApiRouteDocumentation
import io.github.smiley4.ktorswaggerui.routes.get
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.resources.Resources
import io.ktor.server.resources.delete
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable

/**
 * Example from
 * https://github.com/ktorio/ktor-documentation/blob/main/codeSnippets/snippets/resource-routing/src/main/kotlin/com/example/Application.kt
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost") {
        module()
    }.start(wait = true)
}


@Serializable
@Resource("/articles")
class Articles(val sort: String? = "new") : OpenApiRouteDocumentation(api = OpenApiRoute().apply {
    tags = listOf("Articles")
    description = "Returns the list of articles."
    request {
        queryParameter("sort", String::class)
    }
}) {

    @Serializable
    @Resource("new")
    class New(val parent: Articles = Articles()) : OpenApiRouteDocumentation(api = OpenApiRoute().apply {
        description = "Create a new article"
    })


    @Serializable
    @Resource("{id}")
    class Id(val parent: Articles = Articles(), val id: Long) {

        @Serializable
        @Resource("edit")
        class Edit(val parent: Id)

    }
}

fun Application.module() {
    install(Resources)
    install(SwaggerUI)
    routing {
        get<Articles>({
            description = "In Routing: Returns the list of articles."
        }) { article ->
            // Get all articles ...
            call.respondText("List of articles sorted starting from ${article.sort}")
        }
        get<Articles.New> {
            // Show a page with fields for creating a new article ...
            call.respondText("Create a new article")
        }
        post<Articles> {
            // Save an article ...
            call.respondText("An article is saved", status = HttpStatusCode.Created)
        }
        get<Articles.Id> { article ->
            // Show an article with id ${article.id} ...
            call.respondText("An article with id ${article.id}", status = HttpStatusCode.OK)
        }
        get<Articles.Id.Edit> { article ->
            // Show a page with fields for editing an article ...
            call.respondText("Edit an article with id ${article.parent.id}", status = HttpStatusCode.OK)
        }
        put<Articles.Id> { article ->
            // Update an article ...
            call.respondText("An article with id ${article.id} updated", status = HttpStatusCode.OK)
        }
        delete<Articles.Id> { article ->
            // Delete an article ...
            call.respondText("An article with id ${article.id} deleted", status = HttpStatusCode.OK)
        }
    }
}