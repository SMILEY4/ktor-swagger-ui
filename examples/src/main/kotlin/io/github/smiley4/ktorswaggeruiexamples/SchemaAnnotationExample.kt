package io.github.smiley4.ktorswaggeruiexamples

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import io.swagger.v3.oas.annotations.media.Schema

/**
 * An example showing the [Schema]-annotation, adding additional information to models
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {
    install(SwaggerUI)
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
        get("somebody", {
            response {
                HttpStatusCode.OK to {
					body<Person>()
                }
            }
        }) {
            call.respond(Person("Somebody", 42))
        }
    }
}

@Schema(title = "The Schema for a person")
data class Person(

    @field:Schema(description = "the name of the person")
    val name: String,

    @field:Schema(description = "the age of the person in years", nullable = true)
    val age: Int

)