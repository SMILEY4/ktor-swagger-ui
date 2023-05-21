package io.github.smiley4.ktorswaggerui.examples

import io.ktor.server.application.Application
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
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import java.lang.reflect.Type

/**
 * An example for building custom json-schemas
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}


fun typeToJsonSchema(type: Type): String {
    return SchemaGenerator(
        SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON)
            .with(JacksonModule())
            .without(Option.DEFINITIONS_FOR_ALL_OBJECTS)
            .with(Option.INLINE_ALL_SCHEMAS)
            .with(Option.EXTRA_OPEN_API_FORMAT_VALUES)
            .with(Option.ALLOF_CLEANUP_AT_THE_END)
            .build()
    )
        .generateSchema(type)
        .toString()
}

private fun Application.myModule() {

    data class MyRequestData(
        val someText: String,
        val someBoolean: Boolean
    )


    data class MyResponseData(
        val someText: String,
        val someNumber: Long
    )

    install(SwaggerUI) {
        schemas {
            jsonSchemaBuilder { type ->
                // custom converter from the given 'type' to a json-schema
                typeToJsonSchema(type)
            }
        }
    }
    routing {
        get("something", {
            request {
                body<MyRequestData>()
            }
            response {
                HttpStatusCode.OK to {
                    body<MyResponseData>()
                }
            }
        }) {
            val text = call.receive<MyRequestData>().someText
            call.respond(HttpStatusCode.OK, MyResponseData(text, 42))
        }
    }
}
