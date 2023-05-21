package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {
    install(SwaggerUI)

    routing {
        get("object", {
            request {
                body<TestData> {
                    example(
                        "example",
                        TestData(
                            someString = "Hello",
                            someNumber = 42
                        )
                    )
                }
            }
        }) {
            call.respondText("Hello World!")
        }
		get("jsonString", {
			request {
				body<TestData> {
					example(
						"example",
						"""{"someString": "World", "someNumber": 420, "somethingElse": true}"""
					)
				}
			}
		}) {
			call.respondText("Hello World!")
		}
    }
}


private class TestData(
    val someString: String,
    val someNumber: Int
)