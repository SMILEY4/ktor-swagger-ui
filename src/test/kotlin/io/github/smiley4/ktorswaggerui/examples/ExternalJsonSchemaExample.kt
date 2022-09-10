package io.github.smiley4.ktorswaggerui.examples

import com.github.victools.jsonschema.generator.Option
import com.github.victools.jsonschema.generator.OptionPreset
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder
import com.github.victools.jsonschema.generator.SchemaVersion
import com.github.victools.jsonschema.module.jackson.JacksonModule
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlin.reflect.KClass

/**
 * An example for using external json-schemas
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost") {

        install(SwaggerUI) {
            // don't show the routes providing the json-schemas
            pathFilter = { _, url -> url.firstOrNull() != "schemas" }
        }

        routing {

            route("schemas") {
                get("myRequestData") {
                    // respond with a json-schema, e.g. loaded from a file
                    call.respond(HttpStatusCode.OK, buildJsonSchema(MyRequestData::class))
                }
                get("myResponseData") {
                    // respond with a json-schema, e.g. loaded from a file
                    call.respond(HttpStatusCode.OK, buildJsonSchema(MyResponseData::class))
                }
            }

            get("something", {
                request {
                    body("/schemas/myRequestData")
                }
                response {
                    HttpStatusCode.OK to {
                        body("http://localhost:8080/schemas/myResponseData")
                    }
                }
            }) {
                call.respond(HttpStatusCode.OK, MyResponseData("Hello", 42))
            }

        }

    }.start(wait = true)
}


data class MyRequestData(
    val someText: String,
    val someBoolean: Boolean
)

data class MyResponseData(
    val someText: String,
    val someNumber: Long
)

fun buildJsonSchema(type: KClass<*>): String {
    return SchemaGenerator(
        SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON)
            .with(JacksonModule())
            .without(Option.DEFINITIONS_FOR_ALL_OBJECTS)
            .with(Option.INLINE_ALL_SCHEMAS)
            .with(Option.ALLOF_CLEANUP_AT_THE_END)
            .with(Option.EXTRA_OPEN_API_FORMAT_VALUES)
            .build()
    ).generateSchema(type.java).toString()
}

