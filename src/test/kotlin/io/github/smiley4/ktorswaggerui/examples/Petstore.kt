package io.github.smiley4.ktorswaggerui.examples

import io.ktor.server.application.Application
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.routing.routing


/**
 * Uses the OpenApi-Example "petstore-simple" to demonstrate ktor with swagger-ui
 * https://github.com/OAI/OpenAPI-Specification/blob/main/examples/v2.0/json/petstore-simple.json
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {

    data class Pet(
        val id: Int,
        val name: String,
        val tag: String
    )

    data class NewPet(
        val name: String,
        val tag: String
    )

    install(SwaggerUI) {
        info {
            title = "Swagger Petstore"
            description = "A sample API that uses a petstore as an example"
        }
    }

    routing {

        get("pets", {
            description = "Returns all pets from the system that the user has access to."
            request {
                queryParameter<List<String>>("tags") {
                    description = "tags to filter by"
                    required = false
                }
                queryParameter("limit", Int::class) {
                    description = "maximum number of results to return"
                    required = false
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "pet response"
                    body<List<Pet>>() {
                        mediaType(ContentType.Application.Json)
                        mediaType(ContentType.Application.Xml)
                        example(
                            "example", listOf(
                                Pet(1, "Chloe", "cat"),
                                Pet(2, "Oliver", "dog")
                            )
                        )
                    }
                }
            }
        }) {
            // handle request ...
            call.respond(HttpStatusCode.NotImplemented, Unit)
        }

        post("pets", {
            description = "Creates a new pet in the store. Duplicates are allowed."
            request {
                body<NewPet> {
                    description = "Pet to add to the store"
                    required = true
                    mediaType(ContentType.Application.Json)
                    mediaType(ContentType.Application.Xml)
                    example("example", NewPet("Max", "bird"))
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "pet response"
                    body<List<Pet>> {
                        mediaType(ContentType.Application.Json)
                        mediaType(ContentType.Application.Xml)
                        example("example", Pet(3, "Max", "bird"))
                    }
                }
            }
        }) {
            // handle request ...
            call.respond(HttpStatusCode.NotImplemented, Unit)
        }

        get("pets/{id}", {
            description = "Returns a pet based on a single ID, if the user has access to the pet."
            request {
                pathParameter<Int>("id") {
                    description = "ID of pet to fetch"
                    required = true
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "pet response"
                    body<Pet> {
                        mediaType(ContentType.Application.Json)
                        mediaType(ContentType.Application.Xml)
                        example("example", Pet(4, "Bella", "dog"))
                    }
                }
            }
        }) {
            // handle request ...
            call.respond(HttpStatusCode.NotImplemented, Unit)
        }

        delete("pets/{id}", {
            description = "deletes a single pet based on the ID supplied."
            request {
                pathParameter<Int>("id") {
                    description = "ID of pet to delete"
                    required = true
                }
            }
            response {
                HttpStatusCode.NoContent to {
                    description = "pet deleted"
                }
            }
        }) {
            // handle request ...
            call.respond(HttpStatusCode.NotImplemented, Unit)
        }

    }
}
