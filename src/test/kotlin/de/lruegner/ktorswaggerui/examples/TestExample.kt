package de.lruegner.ktorswaggerui.examples

import de.lruegner.ktorswaggerui.SwaggerUI
import de.lruegner.ktorswaggerui.documentation.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import org.slf4j.event.Level

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost") {

        install(CallLogging) {
            level = Level.INFO
            format { call ->
                val status = call.response.status()
                val httpMethod = call.request.httpMethod.value
                val route = call.request.uri.replace(Regex("token=.*?(?=(&|\$))"), "token=SECRET")
                "${status.toString()}: $httpMethod - $route"
            }
        }

        install(SwaggerUI)

        routing {
            get("hello", {
                description = "Hello World Endpoint"
                response(HttpStatusCode.OK, "Successful Request")
            }) {
                call.respondText("Hello World!")
            }
        }

    }.start(wait = true)
}
