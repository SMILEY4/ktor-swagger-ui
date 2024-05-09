package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.data.AuthScheme
import io.github.smiley4.ktorswaggerui.data.AuthType
import io.github.smiley4.ktorswaggerui.data.KTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.SwaggerTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.SwaggerUiSort
import io.github.smiley4.ktorswaggerui.data.SwaggerUiSyntaxHighlight
import io.github.smiley4.ktorswaggerui.data.ValueExampleDescriptor
import io.github.smiley4.ktorswaggerui.dsl.config.PluginConfigDsl
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.github.smiley4.schemakenerator.reflection.processReflection
import io.github.smiley4.schemakenerator.swagger.compileReferencingRoot
import io.github.smiley4.schemakenerator.swagger.data.TitleType
import io.github.smiley4.schemakenerator.swagger.generateSwaggerSchema
import io.github.smiley4.schemakenerator.swagger.withAutoTitle
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.swagger.v3.oas.models.media.Schema
import java.io.File
import kotlin.reflect.typeOf

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}


/**
 * A (nearly) complete - and mostly nonsensical - plugin configuration
 */
private fun Application.myModule() {

    install(SwaggerUI) {
        info {
            title = "Example API"
            version = "latest"
            description = "An example api."
            termsOfService = "example.com"
            contact {
                name = "Mr. Example"
                url = "example.com"
                email = "example@example.com"
            }
            license {
                name = "Example License"
                url = "example.com"
            }
        }
        externalDocs {
            url = "example.com"
            description = "Project documentation"
        }
        server {
            url = "localhost"
            description = "local dev-server"
        }
        server {
            url = "example.com"
            description = "productive server"
        }
        swagger {
            displayOperationId = true
            showTagFilterInput = true
            sort = SwaggerUiSort.HTTP_METHOD
            syntaxHighlight = SwaggerUiSyntaxHighlight.MONOKAI
            withCredentials = false
        }
        security {
            defaultUnauthorizedResponse {
                description = "Username or password is invalid"
            }
            defaultSecuritySchemeNames = setOf("MySecurityScheme")
            securityScheme("MySecurityScheme") {
                type = AuthType.HTTP
                scheme = AuthScheme.BASIC
            }
        }
        tags {
            tagGenerator = { url -> listOf(url.firstOrNull()) }
            tag("users") {
                description = "routes to manage users"
                externalDocUrl = "example.com"
                externalDocDescription = "Users documentation"
            }
            tag("documents") {
                description = "routes to manage documents"
                externalDocUrl = "example.com"
                externalDocDescription = "Document documentation"
            }
        }
        schemas {
            schema("string", KTypeDescriptor(typeOf<String>()))
            generator = { type ->
                type
                    .processReflection()
                    .generateSwaggerSchema()
                    .withAutoTitle(TitleType.SIMPLE)
                    .compileReferencingRoot()
            }
            overwrite[typeOf<File>()] = SwaggerTypeDescriptor(
                Schema<Any>().also {
                    it.type = "string"
                    it.format = "binary"
                }
            )
        }
        examples {
            example(
                ValueExampleDescriptor(
                    name = "Id 1",
                    description = "First example id",
                    value = "12345"
                )
            )
            example(
                ValueExampleDescriptor(
                    name = "Id 2",
                    description = "Second example id",
                    value = "54321"
                )
            )
        }
        specAssigner = { url, tags -> PluginConfigDsl.DEFAULT_SPEC_ID }
        pathFilter = { method, url -> url.firstOrNull() != "hidden" }
        ignoredRouteSelectors = emptySet()
        postBuild = { api -> println("Completed api: $api") }
    }


    routing {

        // add the routes for swagger-ui and api-spec
        route("swagger") {
            swaggerUI("/api.json")
        }
        route("api.json") {
            openApiSpec()
        }

        // a documented route
        get("hello", {
            description = "A Hello-World route"
            request {
                queryParameter("name", KTypeDescriptor(typeOf<String>())) {
                    description = "the name to greet"
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "successful request - always returns 'Hello World!'"
                }
            }
        }) {
            call.respondText("Hello ${call.request.queryParameters["name"]}")
        }

    }

}
