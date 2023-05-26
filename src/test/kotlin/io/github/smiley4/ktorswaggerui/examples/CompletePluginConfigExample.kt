package io.github.smiley4.ktorswaggerui.examples

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.victools.jsonschema.generator.SchemaGenerator
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.jvm.javaType

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

/**
 * Example of an (almost) complete plugin config. This config will (probably) not work, but is only supposed to show all/most configuration options.
 */
private fun Application.myModule() {

    install(SwaggerUI) {
        securityScheme("ApiAuth") {
            type = AuthType.HTTP
            scheme = AuthScheme.BASIC
        }
        securityScheme("SwaggerAuth") {
            type = AuthType.HTTP
            scheme = AuthScheme.BASIC
        }
        defaultSecuritySchemeName = "ApiAuth"
        defaultUnauthorizedResponse {
            description = "invalid username or password"
        }
        swagger {
            forwardRoot = false
            swaggerUrl = "/api/swagger-ui"
            authentication = "SwaggerAuth"
            disableSpecValidator()
            displayOperationId = true
            showTagFilterInput = true
            sort = SwaggerUiSort.ALPHANUMERICALLY
            syntaxHighlight = SwaggerUiSyntaxHighlight.AGATE
        }
        pathFilter = { _, url -> url.firstOrNull() != "test" }
        info {
            title = "Example API"
            version = "latest"
            description = "This is an example api"
            termsOfService = "example.com"
            contact {
                name = "Mr. Example"
                url = "example.com/contact"
                email = "example@mail.com"
            }
            license {
                name = "Mr. Example"
                url = "example.com/license"
            }
        }
        server {
            url = "localhost:8080"
            description = "develop server"
        }
        server {
            url = "127.0.0.1:8080"
            description = "production server"
        }
        tag("greet") {
            description = "routes for greeting"
            externalDocDescription = "documentation for greetings"
            externalDocUrl = "example.com/doc"
        }
        generateTags { url -> listOf(url.firstOrNull()) }
        customSchemas {
            json("customSchema1") {
                """{"type": "string"}"""
            }
            openApi("customSchema2") {
                Schema<Any>().also {
                    it.type = "string"
                }
            }
            remote("customSchema3", "example.com/schema")
        }
        encoding {
            schemaEncoder { type ->
                SchemaGenerator(EncodingConfig.schemaGeneratorConfigBuilder().build())
                    .generateSchema(type.javaType)
                    .toPrettyString()
            }
            schemaDefinitionsField = "\$defs"
            exampleEncoder { type, example ->
                jacksonObjectMapper().writeValueAsString(example)
            }
        }
    }
}
