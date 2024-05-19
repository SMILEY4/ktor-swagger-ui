package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.routing.delete
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}


/**
 * Uses the OpenApi-Example "petstore-simple" to demonstrate ktor with swagger-ui
 * https://github.com/OAI/OpenAPI-Specification/blob/main/examples/v2.0/json/petstore-simple.json
 */
private fun Application.myModule() {

    install(SwaggerUI) {
        info {
            title = "Swagger Petstore"
            version = "1.0.0"
            description = "A sample API that uses a petstore as an example to demonstrate features in the swagger-2.0 specification"
            termsOfService = "http://swagger.io/terms/"
            contact {
                name = "Swagger API Team"
            }
            license {
                name = "MIT"
            }
        }
        examples {
            example("Unexpected Error") {
                value = ErrorModel("Unexpected Error")
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

        route("/pets") {

            get({
                operationId = "findPets"
                description = "Returns all pets from the system that the user has access to"
                request {
                    queryParameter<List<String>>("tags") {
                        description = "tags to filter by"
                        required = false
                        example("dog") {
                            value = "default"
                        }
                    }
                    queryParameter<Int>("limit") {
                        description = "maximum number of results to return"
                        required = false
                        example("default") {
                            value = 100
                        }
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        body<List<Pet>> {
                            description = "the list of available pets"
                            example("Pet List") {
                                value = listOf(
                                    Pet(
                                        id = 123,
                                        name = "Big Bird",
                                        tag = "bird"
                                    ),
                                    Pet(
                                        id = 456,
                                        name = "Charlie",
                                        tag = "dog"
                                    )
                                )
                            }
                        }
                    }
                    default {
                        body<ErrorModel> {
                            description = "unexpected error"
                            exampleRef("Unexpected Error")
                        }
                    }
                }
            }) {
                call.respond(HttpStatusCode.NotImplemented, Unit)
            }

            post({
                operationId = "addPet"
                description = "Creates a new pet in the store. Duplicates are allowed"
                request {
                    body<NewPet> {
                        description = "Pet to add to the store"
                        required = true
                        example("New Bird") {
                            value = NewPet(
                                name = "Big Bird",
                                tag = "bird"
                            )
                        }
                        example("New Dog") {
                            value = NewPet(
                                name = "Charlie",
                                tag = "dog"
                            )
                        }
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        body<Pet> {
                            description = "the created pet"
                            example("Bird") {
                                value = Pet(
                                    id = 123,
                                    name = "Big Bird",
                                    tag = "bird"
                                )
                            }
                            example("Dog") {
                                value = Pet(
                                    id = 456,
                                    name = "Charlie",
                                    tag = "dog"
                                )
                            }
                        }
                    }
                    default {
                        body<ErrorModel> {
                            description = "unexpected error"
                            exampleRef("Unexpected Error")
                        }
                    }
                }
            }) {
                call.respond(HttpStatusCode.NotImplemented, Unit)
            }

            route("{id}") {

                get({
                    operationId = "findBetById"
                    description = "Returns a pet based on a single ID."
                    request {
                        pathParameter<Long>("id") {
                            description = "Id of pet to fetch"
                            required = true
                            example("default") {
                                value = 123L
                            }
                        }
                    }
                    response {
                        HttpStatusCode.OK to {
                            body<Pet>{
                                description = "the pet with the given id"
                                example("Bird") {
                                    value = Pet(
                                        id = 123,
                                        name = "Big Bird",
                                        tag = "bird"
                                    )
                                }
                                example("Dog") {
                                    value = Pet(
                                        id = 123,
                                        name = "Charlie",
                                        tag = "dog"
                                    )
                                }
                            }
                        }
                        HttpStatusCode.NotFound to {
                            description = "the pet with the given id was not found"
                        }
                        default {
                            body<ErrorModel> {
                                description = "unexpected error"
                                exampleRef("Unexpected Error")
                            }
                        }
                    }
                }) {
                    call.respond(HttpStatusCode.NotImplemented, Unit)
                }

                delete({
                    operationId = "deletePet"
                    description = "deletes a single pet based on the supplied ID"
                    request {
                        pathParameter<Long>("id") {
                            description = "Id of pet to delete"
                            required = true
                            example("default") {
                                value = 123L
                            }
                        }
                    }
                    response {
                        HttpStatusCode.NoContent to {
                            description = "the pet was successfully deleted"
                        }
                        HttpStatusCode.NotFound to {
                            description = "the pet with the given id was not found"
                        }
                        default {
                            body<ErrorModel> {
                                description = "unexpected error"
                                exampleRef("Unexpected Error")
                            }
                        }
                    }
                }) {
                    call.respond(HttpStatusCode.NotImplemented, Unit)
                }

            }
        }

    }

}

private data class Pet(
    val id: Long,
    val name: String,
    val tag: String
)

private data class NewPet(
    val name: String,
    val tag: String
)

private data class ErrorModel(
    val message: String
)