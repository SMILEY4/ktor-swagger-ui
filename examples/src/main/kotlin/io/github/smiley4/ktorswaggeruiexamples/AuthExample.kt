package io.github.smiley4.ktorswaggeruiexamples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.AuthScheme
import io.github.smiley4.ktorswaggerui.dsl.AuthType
import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.basic
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing

fun main() {
	embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

/**
 * Example to show how to access protected routes via swagger-ui
 * USERNAME = "user"
 * PASSWORD = "pass"
 */
private fun Application.myModule() {

	// Install "Authentication"-Plugin and setup Basic-Auth
	install(Authentication) {
		basic {
			realm = "Access to the API"
			validate { credentials ->
				if (credentials.name == "user" && credentials.password == "pass") {
					UserIdPrincipal(credentials.name)
				} else {
					null
				}
			}
		}
	}

	// Install "Swagger-UI"-Plugin
	install(SwaggerUI) {
		// default value for "401 Unauthorized"-responses.
		// the name of the security scheme (see below) to use for each route when nothing else is specified
		defaultSecuritySchemeName = "MySecurityScheme"
		defaultUnauthorizedResponse {
			description = "Username or password is invalid."
		}
		// specify a security scheme
		securityScheme("MySecurityScheme") {
			type = AuthType.HTTP
			scheme = AuthScheme.BASIC
		}
		// specify another security scheme
		securityScheme("MyOtherSecurityScheme") {
			type = AuthType.HTTP
			scheme = AuthScheme.BASIC
		}
	}

	// configure routes
	routing {
		authenticate {
			// route is in an "authenticate"-block ->  default security scheme will be used (see plugin-config "defaultSecuritySchemeName")
			get("hello", {
				// Set the security schemes to be used by this route
				securitySchemeNames = setOf("MyOtherSecurityScheme", "MySecurityScheme")
				description = "Protected 'Hello World'-Route"
				response {
					HttpStatusCode.OK to {
						description = "Successful Request"
						body<String> { description = "the response" }
					}
					// response for "401 Unauthorized" is automatically added (see plugin-config "defaultUnauthorizedResponse").
				}
			}) {
				call.respondText("Hello World!")
			}
		}
	}
}
