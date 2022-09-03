package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.documentation.delete
import io.github.smiley4.ktorswaggerui.documentation.get
import io.github.smiley4.ktorswaggerui.documentation.post
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
    embeddedServer(Netty, port = 8080, host = "localhost") {

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
                    queryParameter("tags", Array<String>::class) {
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
                        body(Array<Pet>::class) {
                            mediaType(ContentType.Application.Json)
                            mediaType(ContentType.Application.Xml)
                            example("example", listOf(
                                Pet(1, "Chloe", "cat"),
                                Pet(2, "Oliver", "dog")
                            ))
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
                    body(NewPet::class) {
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
                        body(Array<Pet>::class) {
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
                    pathParameter("id", Int::class) {
                        description = "ID of pet to fetch"
                        required = true
                    }
                    response {
                        HttpStatusCode.OK to {
                            description = "pet response"
                            body(Pet::class) {
                                mediaType(ContentType.Application.Json)
                                mediaType(ContentType.Application.Xml)
                                example("example", Pet(4, "Bella", "dog"))
                            }
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
                    pathParameter("id", Int::class) {
                        description = "ID of pet to delete"
                        required = true
                    }
                    response {
                        HttpStatusCode.NoContent to {
                            description = "pet deleted"
                        }
                    }
                }
            }) {
                // handle request ...
                call.respond(HttpStatusCode.NotImplemented, Unit)
            }

        }

    }.start(wait = true)
}


internal data class Pet(
    val id: Int,
    val name: String,
    val tag: String
)


internal data class NewPet(
    val name: String,
    val tag: String
)