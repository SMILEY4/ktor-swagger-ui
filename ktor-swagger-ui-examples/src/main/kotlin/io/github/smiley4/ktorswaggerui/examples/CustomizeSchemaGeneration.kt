package io.github.smiley4.ktorswaggerui.examples

import com.fasterxml.jackson.annotation.JsonSubTypes
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.data.AnyOfTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.ArrayTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.KTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.RefTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.SwaggerTypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.github.smiley4.schemakenerator.core.connectSubTypes
import io.github.smiley4.schemakenerator.jackson.collectJacksonSubTypes
import io.github.smiley4.schemakenerator.reflection.processReflection
import io.github.smiley4.schemakenerator.swagger.compileReferencingRoot
import io.github.smiley4.schemakenerator.swagger.data.TitleType
import io.github.smiley4.schemakenerator.swagger.generateSwaggerSchema
import io.github.smiley4.schemakenerator.swagger.withAutoTitle
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.swagger.v3.oas.models.media.Schema
import java.time.LocalDateTime
import kotlin.reflect.typeOf

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {

    // Install the "SwaggerUI"-Plugin and use the default configuration
    install(SwaggerUI) {
        schemas {

            // add a swagger schema to the component-section of the api-spec with the id "swagger-schema"
            schema("swagger-schema", SwaggerTypeDescriptor(
                Schema<Any>().also {
                    it.type = "number"
                    it.title = "Custom Type"
                }
            ))

            // add a type to the component-section of the api-spec with the id "type-schema"
            schema("type-schema", KTypeDescriptor(typeOf<MyClass>()))

            // overwrite 'LocalDateTime' with custom schema (root only)
            overwrite[typeOf<LocalDateTime>()] = SwaggerTypeDescriptor(
                Schema<Any>().also {
                    it.title = "timestamp"
                    it.type = "integer"
                }
            )

            // customized schema generation pipeline
            generator = { type ->
                type
                    .collectJacksonSubTypes(typeProcessing = { types -> types.processReflection() }) // include types from jackson subtype-annotation
                    .processReflection()
                    .connectSubTypes() // connect the supertypes with their subtypes
                    .generateSwaggerSchema()
                    .withAutoTitle(TitleType.SIMPLE)
                    .compileReferencingRoot()
            }

        }
    }

    routing {
        route("swagger") {
            swaggerUI("/api.json")
        }
        route("api.json") {
            openApiSpec()
        }

        get("global-swagger-schema", {
            request {
                // reference and use the schema from the component-section with the id "swagger-schema"
                body(RefTypeDescriptor("swagger-schema"))
            }
        }) {
            call.respondText("...")
        }

        get("global-type-schema", {
            request {
                // reference and use the schema from the component-section with the id "type-schema"
                body(RefTypeDescriptor("type-schema"))
            }
        }) {
            call.respondText("...")
        }

        get("array-schema", {
            request {
                // an array of items with the referenced schema with the id "type-schema"
                body(
                    ArrayTypeDescriptor(
                        RefTypeDescriptor("type-schema")
                    )
                )
            }
        }) {
            call.respondText("...")
        }

        get("anyof-schema", {
            request {
                // either the referenced schema with id "type-schema" or "swagger-schema"
                body(
                    AnyOfTypeDescriptor(
                        listOf(
                            RefTypeDescriptor("type-schema"),
                            RefTypeDescriptor("swagger-schema")
                        )
                    )
                )
            }
        }) {
            call.respondText("...")
        }

        get("type-overwrite", {
            request {
                // schema is not generated the normal way but the overwriting schema from the config is used instead
                body(KTypeDescriptor(typeOf<LocalDateTime>()))
            }
        }) {
            call.respondText("...")
        }

        get("jackson-subtypes", {
            request {
                // jackson subtypes are detected automatically
                body(KTypeDescriptor(typeOf<BaseType>()))
            }
        }) {
            call.respondText("...")
        }

    }

}


private data class MyClass(
    val someValue: String
)


@JsonSubTypes(
    JsonSubTypes.Type(value = SubTypeA::class),
    JsonSubTypes.Type(value = SubTypeB::class),
)
open class BaseType(val base: String)
class SubTypeA(base: String, val a: Int) : BaseType(base)
class SubTypeB(base: String, val b: Boolean) : BaseType(base)