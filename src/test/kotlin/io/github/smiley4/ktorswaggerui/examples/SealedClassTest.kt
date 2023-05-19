package io.github.smiley4.ktorswaggerui.examples

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.tests.schema.SchemaContextTest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing

/**
 * A minimal working example
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {
    install(SwaggerUI)


    routing {
        get("test/sealed", {
            response {
                HttpStatusCode.OK to {
                    body<ExampleResponse>()
                }
            }
        }) {
			//...
        }
		get("test/a", {
			response {
				HttpStatusCode.OK to {
					body<ExampleResponse.A>()
				}
			}
		}) {
			//...
		}
		get("test/b", {
			response {
				HttpStatusCode.OK to {
					body<ExampleResponse.B>()
				}
			}
		}) {
			//...
		}
    }
}


@JsonTypeInfo(
	use = JsonTypeInfo.Id.CLASS,
	include = JsonTypeInfo.As.PROPERTY,
	property = "_type",
)
@JsonSubTypes(
	JsonSubTypes.Type(value = ExampleResponse.A::class),
	JsonSubTypes.Type(value = ExampleResponse.B::class),
)
sealed class ExampleResponse {
    data class A(
        val thisIsA: Boolean
    ) : ExampleResponse()

    data class B(
        val thisIsB: Boolean
    ) : ExampleResponse()
}