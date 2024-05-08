package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.data.KTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.RefExampleDescriptor
import io.github.smiley4.ktorswaggerui.data.ValueExampleDescriptor
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
import kotlin.reflect.typeOf

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
            example(ValueExampleDescriptor("Unexpected Error", ErrorModel("Unexpected Error"), null, null))
        }
        schemas {
            schema("Pet", KTypeDescriptor(typeOf<Pet>()))
            schema("NewPet", KTypeDescriptor(typeOf<NewPet>()))
            schema("PetList", KTypeDescriptor(typeOf<List<Pet>>()))
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
                    queryParameter("tags", KTypeDescriptor(typeOf<List<String>>())) {
                        description = "tags to filter by"
                        required = false
                        example = ValueExampleDescriptor("dog", "default", null, null)
                    }
                    queryParameter("limit", KTypeDescriptor(typeOf<Int>())) {
                        description = "maximum number of results to return"
                        required = false
                        example = ValueExampleDescriptor("default", 100, null, null)
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        body(KTypeDescriptor(typeOf<List<Pet>>())) {
                            description = "the list of available pets"
                            example(
                                ValueExampleDescriptor(
                                    "Pet List",
                                    listOf(
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
                                    ), null, null
                                )
                            )
                        }
                    }
                    default {
                        body(KTypeDescriptor(typeOf<ErrorModel>())) {
                            description = "unexpected error"
                            example(RefExampleDescriptor("Unexpected Error", "Unexpected Error"))
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
                    body(KTypeDescriptor(typeOf<NewPet>())) {
                        description = "Pet to add to the store"
                        required = true
                        example(
                            ValueExampleDescriptor(
                                "New Bird",
                                NewPet(
                                    name = "Big Bird",
                                    tag = "bird"
                                ), null, null
                            )
                        )
                        example(
                            ValueExampleDescriptor(
                                "New Dog",
                                NewPet(
                                    name = "Charlie",
                                    tag = "dog"
                                ), null, null
                            )
                        )
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        body(KTypeDescriptor(typeOf<Pet>())) {
                            description = "the created pet"
                            example(
                                ValueExampleDescriptor(
                                    "Bird",
                                    Pet(
                                        id = 123,
                                        name = "Big Bird",
                                        tag = "bird"
                                    ), null, null
                                )
                            )
                            example(
                                ValueExampleDescriptor(
                                    "Dog",
                                    Pet(
                                        id = 456,
                                        name = "Charlie",
                                        tag = "dog"
                                    ), null, null
                                )
                            )
                        }
                    }
                    default {
                        body(KTypeDescriptor(typeOf<ErrorModel>())) {
                            description = "unexpected error"
                            example(RefExampleDescriptor("Unexpected Error", "Unexpected Error"))
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
                        pathParameter("id", KTypeDescriptor(typeOf<Long>())) {
                            description = "Id of pet to fetch"
                            required = true
                            example = ValueExampleDescriptor("default", 123L, null, null)
                        }
                    }
                    response {
                        HttpStatusCode.OK to {
                            body(KTypeDescriptor(typeOf<Pet>())) {
                                description = "the pet with the given id"
                                example(
                                    ValueExampleDescriptor(
                                        "Bird",
                                        Pet(
                                            id = 123,
                                            name = "Big Bird",
                                            tag = "bird"
                                        ), null, null
                                    )
                                )
                                example(
                                    ValueExampleDescriptor(
                                        "Dog",
                                        Pet(
                                            id = 123,
                                            name = "Charlie",
                                            tag = "dog"
                                        ), null, null
                                    )
                                )
                            }
                        }
                        HttpStatusCode.NotFound to {
                            description = "the pet with the given id was not found"
                        }
                        default {
                            body(KTypeDescriptor(typeOf<ErrorModel>())) {
                                description = "unexpected error"
                                example(RefExampleDescriptor("Unexpected Error", "Unexpected Error"))
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
                        pathParameter("id", KTypeDescriptor(typeOf<Long>())) {
                            description = "Id of pet to delete"
                            required = true
                            example = ValueExampleDescriptor("default", 123L, null, null)
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
                            body(KTypeDescriptor(typeOf<ErrorModel>())) {
                                description = "unexpected error"
                                example(RefExampleDescriptor("Unexpected Error", "Unexpected Error"))
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