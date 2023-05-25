package io.github.smiley4.ktorswaggeruiexamples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import com.github.ricky12awesome.jss.encodeToSchema

/**
 * An example showing compatibility with kotlinx serializer and kotlinx multiplatform using:
 * - https://github.com/Kotlin/kotlinx.serialization
 * - https://github.com/Kotlin/kotlinx-datetime
 * - https://github.com/tillersystems/json-schema-serialization
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {

    val kotlinxJson = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    install(SwaggerUI) {
        encoding {
            schemaEncoder { type ->
                kotlinxJson.encodeToSchema(serializer(type), generateDefinitions = false)
            }
            schemaDefinitionsField = "definitions"
            exampleEncoder { type, value ->
                kotlinxJson.encodeToString(serializer(type!!), value)
            }
        }
    }
    routing {
        get("example/one", {
            request {
                body<ExampleRequest> {
                    example(
                        "default", ExampleRequest.B(
                            thisIsB = Instant.fromEpochMilliseconds(System.currentTimeMillis())
                        )
                    )
                }
            }
        }) {
            call.respondText("...")
        }
        get("example/many", {
            request {
                body<List<ExampleRequest>> {
                    example("default", listOf(
                        ExampleRequest.B(Instant.fromEpochMilliseconds(System.currentTimeMillis())),
                        ExampleRequest.A(true)
                    ))
                }
            }
        }) {
            call.respondText("...")
        }
    }
}


@Serializable
private sealed class ExampleRequest {

    @Serializable
    data class A(
        val thisIsA: Boolean
    ) : ExampleRequest()


    @Serializable
    data class B(
        val thisIsB: Instant
    ) : ExampleRequest()

}