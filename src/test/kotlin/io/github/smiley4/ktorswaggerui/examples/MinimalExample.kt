package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
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
 * A minimal working example
 */
fun main() {
	embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {
    // Install the "SwaggerUI"-Plugin and use the default configuration
	// By default, swagger is available at /swagger-ui (i.e. localhost:8080/swagger-ui)
	install(SwaggerUI)

	routing {
		// documented "get"-route
		get("hello", {

			request {
				body<Int> {
					example("example", 42)
				}
			}

			// a description of the route
			description = "Simple 'Hello World'- Route"
			// information about possible responses
			response {
				// information about a "200 OK" response
				HttpStatusCode.OK to {
					// the description of the response
					description = "Successful Response"
				}
			}
		}) {
			call.respondText("Hello World!")
		}
	}
}
