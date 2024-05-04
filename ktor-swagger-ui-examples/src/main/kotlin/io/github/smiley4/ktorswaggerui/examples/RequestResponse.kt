package io.github.smiley4.ktorswaggerui.examples

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.data.KTypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlin.reflect.typeOf

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {

    // Install the "SwaggerUI"-Plugin and use the default configuration
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

        // Create a route for the swagger-ui using the openapi-spec at "/api.json".
        // This route will not be included in the spec.
        route("swagger") {
            swaggerUI("/api.json")
        }
        // Create a route for the openapi-spec file.
        // This route will not be included in the spec.
        route("api.json") {
            openApiSpec()
        }

        // a documented route
        post("calculate", {
            // information about the request
            request {
                // specify the schema of the request-body and some additional information
                body(KTypeDescriptor(typeOf<Calculation>())) {
                    description = "the requested operation and values to perform the operation on"
                    required = true
                }
            }
            // information the possible responses
            response {
                // document the "200 OK"-response
                HttpStatusCode.OK to {
                    description = "Calculation was performed successfully."
                    // specify the schema of the response-body and some additional information
                    body(KTypeDescriptor(typeOf<CalculationResult>())) {
                        description = "the result of an operation together with the original request"
                    }
                }
                // document the "422 UnprocessableEntity"-response
                HttpStatusCode.UnprocessableEntity to {
                    description = "The requested calculation could not be performed, e.g. due to division by zero."
                }
            }
        }) {
            call.receive<Calculation>().let { calculation ->
                when (calculation.operation) {
                    OperationType.ADD -> {
                        call.respond(
                            HttpStatusCode.OK, CalculationResult(
                                calculation = calculation,
                                result = calculation.a + calculation.b
                            )
                        )
                    }
                    OperationType.SUB -> {
                        call.respond(
                            HttpStatusCode.OK, CalculationResult(
                                calculation = calculation,
                                result = calculation.a - calculation.b
                            )
                        )
                    }
                    OperationType.MUL -> {
                        call.respond(
                            HttpStatusCode.OK, CalculationResult(
                                calculation = calculation,
                                result = calculation.a * calculation.b
                            )
                        )
                    }
                    OperationType.DIV -> {
                        if (calculation.b == 0f) {
                            call.respond(HttpStatusCode.UnprocessableEntity)
                        } else {
                            call.respond(
                                HttpStatusCode.OK, CalculationResult(
                                    calculation = calculation,
                                    result = calculation.a / calculation.b
                                )
                            )
                        }
                    }
                }
            }
        }

    }

}

private enum class OperationType {
    ADD, SUB, MUL, DIV
}

private data class Calculation(
    val operation: OperationType,
    val a: Float,
    val b: Float
)

private data class CalculationResult(
    val calculation: Calculation,
    val result: Float
)