package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing

/**
 * An example demonstrating a swagger protected by custom authentication
 */
fun main() {
	embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {
	install(Authentication) {
		basic("auth-swagger") {
			realm = "Access to the Swagger UI"
			validate { credentials ->
				if (credentials.name == "user" && credentials.password == "pass") {
					UserIdPrincipal(credentials.name)
				} else {
					null
				}
			}
		}
	}

	install(SwaggerUI) {
		swagger {
			// protect Swagger UI and OpenApi-Spec with the authentication method defined above
			authentication = "auth-swagger"
		}
	}

	routing {
		get("hello", {
			description = "Simple 'Hello World'- Route"
			response {
				HttpStatusCode.OK to {
					description = "Successful Response"
				}
			}
		}) {
			call.respondText("Hello World!")
		}
	}
}
