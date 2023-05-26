package io.github.smiley4.ktorswaggerui.examples

import com.github.ricky12awesome.jss.encodeToSchema
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

/**
 * A minimal working example
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
        get("hello", {
            request {
                queryParameter<Instant>("param") {
                    example = Instant.fromEpochMilliseconds(System.currentTimeMillis())
                }
            }
        }) {
            call.respondText("Hello World!")
        }
    }
}
