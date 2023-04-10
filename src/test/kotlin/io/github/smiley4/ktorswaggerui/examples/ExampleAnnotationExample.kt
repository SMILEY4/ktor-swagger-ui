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

/**
 * An example showcasing examples with the [io.github.smiley4.ktorswaggerui.dsl.Example]-Annotation
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {
    install(SwaggerUI)
    routing {
        get("person", {
            description = "get some random person"
            response {
                HttpStatusCode.OK to {
                    description = "Successful Response"
                    body<TestPerson>()
                }
            }
        }) {
            call.respondText("This would be some person, but the dev was lazy.")
        }
    }
}


data class TestPerson(

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

    val address: TestAddress,

    val secondaryAddresses: List<TestAddress>
)

data class TestAddress(

    @Example("New City")
    val city: String,

    @Example("12345")
    val code: Int

)
