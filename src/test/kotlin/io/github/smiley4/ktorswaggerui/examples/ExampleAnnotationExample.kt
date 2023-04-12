package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.Example
import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

/**
 * An example showcasing examples with the [Schema] and [io.github.smiley4.ktorswaggerui.dsl.Example]-Annotation
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {
    install(SwaggerUI)
    routing {
        get("person/example", {
            request {
                body<ExamplePerson>()
            }
            response {
                HttpStatusCode.OK to {
                    body<ExamplePerson>()
                }
            }
        }) {
            call.respondText("...")
        }
        get("person/schema", {
            request {
                body<SchemaPerson>()
            }
            response {
                HttpStatusCode.OK to {
                    body<SchemaPerson>()
                }
            }
        }) {
            call.respondText("...")
        }
    }
}

data class ExamplePerson(

    @Example("red")
    val favColor: String,

    @Example("Steve")
    val name: String,

    @Example("42")
    val age: Int,

    @Example("172")
    val size: Float,

    @Example("false")
    val robot: Boolean,

    val address: ExampleAddress,

    val secondaryAddresses: List<ExampleAddress>
)

data class ExampleAddress(

    @Example("New City")
    val city: String,

    @Example("12345")
    val code: Int

)

@Schema(description = "Schema of some person", title = "Person")
data class SchemaPerson(

    @field:Schema(example = "red")
    val favColor: String,

    @field:Schema(example = "Steve", minLength = 1, maxLength = 32)
    val name: String,

    @field:Schema(example = "42", minimum = "18", maximum = "99")
    val age: Int,

    @field:Schema(example = "172", format = "int32")
    val size: Float,

    @field:Schema(example = "false")
    val robot: Boolean,

    val address: SchemaAddress,

    @field:ArraySchema(minItems = 1, maxItems = 32, uniqueItems = true)
    val secondaryAddresses: List<SchemaAddress>
)

@Schema(description = "Schema of some address", title = "Address")
data class SchemaAddress(

    @field:Schema(example = "New City")
    val city: String,

    @field:Schema(example = "12345")
    val code: Int

)
