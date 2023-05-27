package io.github.smiley4.ktorswaggerui.tests.openapi

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.github.smiley4.ktorswaggerui.dsl.obj
import io.github.smiley4.ktorswaggerui.spec.example.ExampleContext
import io.github.smiley4.ktorswaggerui.spec.example.ExampleContextBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.ContentBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.ExampleBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.HeaderBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.OperationBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.OperationTagsBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.ParameterBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.RequestBodyBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.ResponseBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.ResponsesBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.SecurityRequirementsBuilder
import io.github.smiley4.ktorswaggerui.spec.route.RouteMeta
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaBuilder
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaContext
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaContextBuilder
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.Schema
import java.io.File

class OperationBuilderTest : StringSpec({

    "empty operation" {
        val route = RouteMeta(
            path = "/test",
            method = HttpMethod.Get,
            documentation = OpenApiRoute(),
            protected = false
        )
        val schemaContext = schemaContext(listOf(route))
        val exampleContext = exampleContext(listOf(route))
        buildOperationObject(route, schemaContext, exampleContext).also { operation ->
            operation.tags.shouldBeEmpty()
            operation.summary shouldBe null
            operation.description shouldBe null
            operation.externalDocs shouldBe null
            operation.operationId shouldBe null
            operation.parameters.shouldBeEmpty()
            operation.requestBody shouldBe null
            operation.responses.shouldBeEmpty()
            operation.deprecated shouldBe false
            operation.security shouldBe null
            operation.servers shouldBe null
            operation.extensions shouldBe null
        }
    }

    "basic operation" {
        val route = RouteMeta(
            path = "/test",
            method = HttpMethod.Get,
            documentation = OpenApiRoute().also { route ->
                route.tags = listOf("tag1", "tag2")
                route.description = "route for testing"
                route.summary = "this is some test route"
                route.operationId = "testRoute"
                route.deprecated = true
            },
            protected = false
        )
        val schemaContext = schemaContext(listOf(route))
        val exampleContext = exampleContext(listOf(route))
        buildOperationObject(route, schemaContext, exampleContext).also { operation ->
            operation.tags shouldContainExactlyInAnyOrder listOf("tag1", "tag2")
            operation.summary shouldBe "this is some test route"
            operation.description shouldBe "route for testing"
            operation.externalDocs shouldBe null
            operation.operationId shouldBe "testRoute"
            operation.parameters.shouldBeEmpty()
            operation.requestBody shouldBe null
            operation.responses.shouldBeEmpty()
            operation.deprecated shouldBe true
            operation.security shouldBe null
            operation.servers shouldBe null
            operation.extensions shouldBe null
        }
    }

    "operation with auto-generated tags" {
        val config = SwaggerUIPluginConfig().also {
            it.generateTags { url -> listOf(url.firstOrNull()) }
        }
        val routeA = RouteMeta(
            path = "a/test",
            method = HttpMethod.Get,
            documentation = OpenApiRoute().also { route ->
                route.tags = listOf("defaultTag")
            },
            protected = false
        )
        val routeB = RouteMeta(
            path = "b/test",
            method = HttpMethod.Get,
            documentation = OpenApiRoute().also { route ->
                route.tags = listOf("defaultTag")
            },
            protected = false
        )
        val schemaContext = schemaContext(listOf(routeA, routeB), config)
        val exampleContext = exampleContext(listOf(routeA, routeB), config)
        buildOperationObject(routeA, schemaContext, exampleContext, config).also { operation ->
            operation.tags shouldContainExactlyInAnyOrder listOf("a", "defaultTag")
        }
        buildOperationObject(routeB, schemaContext, exampleContext, config).also { operation ->
            operation.tags shouldContainExactlyInAnyOrder listOf("b", "defaultTag")
        }
    }

    "protected route with security-scheme-names" {
        val route = RouteMeta(
            path = "/test",
            method = HttpMethod.Get,
            documentation = OpenApiRoute().also { route ->
                route.securitySchemeNames = listOf("security1", "security2")
            },
            protected = true
        )
        val schemaContext = schemaContext(listOf(route))
        val exampleContext = exampleContext(listOf(route))
        buildOperationObject(route, schemaContext, exampleContext).also { operation ->
            operation.security
                .also { it.shouldNotBeNull() }
                ?.also { security ->
                    security shouldHaveSize 2
                    security.find { it.containsKey("security1") }.shouldNotBeNull()
                    security.find { it.containsKey("security2") }.shouldNotBeNull()
                }
        }
    }

    "protected route without security-scheme-names" {
        val route = RouteMeta(
            path = "/test",
            method = HttpMethod.Get,
            documentation = OpenApiRoute(),
            protected = true
        )
        val schemaContext = schemaContext(listOf(route))
        val exampleContext = exampleContext(listOf(route))
        buildOperationObject(route, schemaContext, exampleContext).also { operation ->
            operation.tags.shouldBeEmpty()
            operation.summary shouldBe null
            operation.description shouldBe null
            operation.externalDocs shouldBe null
            operation.operationId shouldBe null
            operation.parameters.shouldBeEmpty()
            operation.requestBody shouldBe null
            operation.responses.shouldBeEmpty()
            operation.deprecated shouldBe false
            operation.security shouldBe null
            operation.servers shouldBe null
            operation.extensions shouldBe null
        }
    }

    "unprotected route with security-scheme-names" {
        val route = RouteMeta(
            path = "/test",
            method = HttpMethod.Get,
            documentation = OpenApiRoute().also { route ->
                route.securitySchemeNames = listOf("security1", "security2")
            },
            protected = false
        )
        val schemaContext = schemaContext(listOf(route))
        val exampleContext = exampleContext(listOf(route))
        buildOperationObject(route, schemaContext, exampleContext).also { operation ->
            operation.tags.shouldBeEmpty()
            operation.summary shouldBe null
            operation.description shouldBe null
            operation.externalDocs shouldBe null
            operation.operationId shouldBe null
            operation.parameters.shouldBeEmpty()
            operation.requestBody shouldBe null
            operation.responses.shouldBeEmpty()
            operation.deprecated shouldBe false
            operation.security shouldBe null
            operation.servers shouldBe null
            operation.extensions shouldBe null
        }
    }

    "route with basic request" {
        val route = RouteMeta(
            path = "/test",
            method = HttpMethod.Get,
            documentation = OpenApiRoute().also { route ->
                route.request {
                    queryParameter<String>("queryParam")
                    pathParameter<Int>("pathParam")
                    headerParameter<Boolean>("headerParam")
                    body<List<String>>()
                }
            },
            protected = false
        )
        val schemaContext = schemaContext(listOf(route))
        val exampleContext = exampleContext(listOf(route))
        buildOperationObject(route, schemaContext, exampleContext).also { operation ->
            operation.tags.shouldBeEmpty()
            operation.summary shouldBe null
            operation.description shouldBe null
            operation.externalDocs shouldBe null
            operation.operationId shouldBe null
            operation.parameters.also { parameters ->
                parameters shouldHaveSize 3
                parameters.find { it.name == "queryParam" }
                    .also { it.shouldNotBeNull() }
                    ?.also { param ->
                        param.`in` shouldBe "query"
                        param.description shouldBe null
                        param.required shouldBe null
                        param.deprecated shouldBe null
                        param.allowEmptyValue shouldBe null
                        param.`$ref` shouldBe null
                        param.style shouldBe null
                        param.explode shouldBe null
                        param.allowReserved shouldBe null
                        param.schema
                            .also { it.shouldNotBeNull() }
                            ?.also { it.type = "string" }
                        param.example shouldBe null
                        param.examples shouldBe null
                        param.content shouldBe null
                        param.extensions shouldBe null
                    }
                parameters.find { it.name == "pathParam" }
                    .also { it.shouldNotBeNull() }
                    ?.also { param ->
                        param.`in` shouldBe "path"
                        param.description shouldBe null
                        param.required shouldBe null
                        param.deprecated shouldBe null
                        param.allowEmptyValue shouldBe null
                        param.`$ref` shouldBe null
                        param.style shouldBe null
                        param.explode shouldBe null
                        param.allowReserved shouldBe null
                        param.schema
                            .also { it.shouldNotBeNull() }
                            ?.also { it.type = "integer" }
                        param.example shouldBe null
                        param.examples shouldBe null
                        param.content shouldBe null
                        param.extensions shouldBe null
                    }
                parameters.find { it.name == "headerParam" }
                    .also { it.shouldNotBeNull() }
                    ?.also { param ->
                        param.`in` shouldBe "header"
                        param.description shouldBe null
                        param.required shouldBe null
                        param.deprecated shouldBe null
                        param.allowEmptyValue shouldBe null
                        param.`$ref` shouldBe null
                        param.style shouldBe null
                        param.explode shouldBe null
                        param.allowReserved shouldBe null
                        param.schema
                            .also { it.shouldNotBeNull() }
                            ?.also { it.type = "boolean" }
                        param.example shouldBe null
                        param.examples shouldBe null
                        param.content shouldBe null
                        param.extensions shouldBe null
                    }
            }
            operation.requestBody
                .also { it.shouldNotBeNull() }
                ?.also { body ->
                    body.description shouldBe null
                    body.content
                        .also { it.shouldNotBeNull() }
                        ?.also { content ->
                            content shouldHaveSize 1
                            content.get("application/json")
                                .also { it.shouldNotBeNull() }
                                ?.also { mediaType ->
                                    mediaType.schema
                                        .also { it.shouldNotBeNull() }
                                        ?.also { schema ->
                                            schema.type shouldBe "array"
                                            schema.items.also { item -> item.type shouldBe "string" }
                                        }
                                    mediaType.example shouldBe null
                                    mediaType.examples shouldBe null
                                    mediaType.encoding shouldBe null
                                    mediaType.extensions shouldBe null
                                    mediaType.exampleSetFlag shouldBe false
                                }

                        }
                    body.required shouldBe null
                    body.extensions shouldBe null
                    body.`$ref` shouldBe null
                }
            operation.responses.shouldBeEmpty()
            operation.deprecated shouldBe false
            operation.security shouldBe null
            operation.servers shouldBe null
            operation.extensions shouldBe null
        }
    }

    "route with basic response" {
        val route = RouteMeta(
            path = "/test",
            method = HttpMethod.Get,
            documentation = OpenApiRoute().also { route ->
                route.response {
                    "test" to {
                        description = "Test Response"
                        header<String>("test-header")
                        body<List<String>>()
                    }
                }
            },
            protected = false
        )
        val schemaContext = schemaContext(listOf(route))
        val exampleContext = exampleContext(listOf(route))
        buildOperationObject(route, schemaContext, exampleContext).also { operation ->
            operation.tags.shouldBeEmpty()
            operation.summary shouldBe null
            operation.description shouldBe null
            operation.externalDocs shouldBe null
            operation.operationId shouldBe null
            operation.parameters.shouldBeEmpty()
            operation.requestBody shouldBe null
            operation.responses
                .also { it shouldHaveSize 1 }
                .let { it.get("test") }
                .also { it.shouldNotBeNull() }
                ?.also { response ->
                    response.description shouldBe "Test Response"
                    response.headers
                        .also { it.shouldNotBeNull() }
                        .let { it["test-header"] }
                        .also { it.shouldNotBeNull() }
                        ?.also { header ->
                            header.schema
                                .also { it.shouldNotBeNull() }
                                ?.also { it.type shouldBe "string" }
                        }

                    response.content
                        .also { it.shouldNotBeNull() }
                        .let { it.get("application/json") }
                        .also { it.shouldNotBeNull() }
                        ?.also { mediaType ->
                            mediaType.schema
                                .also { it.shouldNotBeNull() }
                                ?.also { schema ->
                                    schema.type shouldBe "array"
                                    schema.items.also { item -> item.type shouldBe "string" }
                                }
                            mediaType.example shouldBe null
                            mediaType.examples shouldBe null
                            mediaType.encoding shouldBe null
                            mediaType.extensions shouldBe null
                            mediaType.exampleSetFlag shouldBe false
                        }
                    response.links shouldBe null
                    response.extensions shouldBe null
                    response.`$ref` shouldBe null
                }
            operation.deprecated shouldBe false
            operation.security shouldBe null
            operation.servers shouldBe null
            operation.extensions shouldBe null
        }
    }

    "documented parameter" {
        val route = RouteMeta(
            path = "/test",
            method = HttpMethod.Get,
            documentation = OpenApiRoute().also { route ->
                route.request {
                    queryParameter<String>("param") {
                        description = "test parameter"
                        example = "MyExample"
                        required = true
                        deprecated = true
                        allowEmptyValue = true
                        explode = true
                        allowReserved = true
                    }
                }
            },
            protected = false
        )
        val schemaContext = schemaContext(listOf(route))
        val exampleContext = exampleContext(listOf(route))
        buildOperationObject(route, schemaContext, exampleContext).also { operation ->
            operation.parameters.also { parameters ->
                parameters shouldHaveSize 1
                parameters[0].also { param ->
                    param.`in` shouldBe "query"
                    param.description shouldBe "test parameter"
                    param.required shouldBe true
                    param.deprecated shouldBe true
                    param.allowEmptyValue shouldBe true
                    param.`$ref` shouldBe null
                    param.style shouldBe null
                    param.explode shouldBe true
                    param.allowReserved shouldBe true
                    param.schema
                        .also { it.shouldNotBeNull() }
                        ?.also { it.type = "string" }
                    param.example shouldBe "MyExample"
                    param.examples shouldBe null
                    param.content shouldBe null
                    param.extensions shouldBe null
                }
            }
        }
    }

    "documented body" {
        val route = RouteMeta(
            path = "/test",
            method = HttpMethod.Get,
            documentation = OpenApiRoute().also { route ->
                route.request {
                    body<String> {
                        description = "the test body"
                        required = true
                        mediaType(ContentType.Application.Json)
                        mediaType(ContentType.Application.Xml)
                        example("example 1", "MyExample1") {
                            summary = "the example 1"
                            description = "the first example"
                        }
                    }
                }
            },
            protected = false
        )
        val schemaContext = schemaContext(listOf(route))
        val exampleContext = exampleContext(listOf(route))
        buildOperationObject(route, schemaContext, exampleContext).also { operation ->
            operation.requestBody
                .also { it.shouldNotBeNull() }
                ?.also { body ->
                    body.description shouldBe "the test body"
                    body.content
                        .also { it.shouldNotBeNull() }
                        ?.also { content ->
                            content shouldHaveSize 2
                            content["application/json"]
                                .also { it.shouldNotBeNull() }
                                ?.also { mediaType ->
                                    mediaType.schema
                                        .also { it.shouldNotBeNull() }
                                        ?.also { it.type shouldBe "string" }
                                    mediaType.example shouldBe null
                                    mediaType.examples
                                        .also { it shouldHaveSize 1 }
                                        .let { it["example 1"] }
                                        .also { it.shouldNotBeNull() }
                                        ?.also { example ->
                                            example.summary shouldBe "the example 1"
                                            example.description shouldBe "the first example"
                                            example.value shouldBe "MyExample1"
                                            example.externalValue shouldBe null
                                            example.`$ref` shouldBe null
                                            example.extensions shouldBe null
                                            example.valueSetFlag shouldBe true
                                        }
                                    mediaType.encoding shouldBe null
                                    mediaType.extensions shouldBe null
                                    mediaType.exampleSetFlag shouldBe false
                                }
                            content["application/xml"]
                                .also { it.shouldNotBeNull() }
                                ?.also { mediaType ->
                                    mediaType.schema
                                        .also { it.shouldNotBeNull() }
                                        ?.also { it.type shouldBe "string" }
                                    mediaType.example shouldBe null
                                    mediaType.examples
                                        .also { it shouldHaveSize 1 }
                                        .let { it["example 1"] }
                                        .also { it.shouldNotBeNull() }
                                        ?.also { example ->
                                            example.summary shouldBe "the example 1"
                                            example.description shouldBe "the first example"
                                            example.value shouldBe "MyExample1"
                                            example.externalValue shouldBe null
                                            example.`$ref` shouldBe null
                                            example.extensions shouldBe null
                                            example.valueSetFlag shouldBe true
                                        }
                                    mediaType.encoding shouldBe null
                                    mediaType.extensions shouldBe null
                                    mediaType.exampleSetFlag shouldBe false
                                }
                        }
                    body.required shouldBe true
                    body.extensions shouldBe null
                    body.`$ref` shouldBe null
                }
        }
    }

    "multipart body" {
        val route = RouteMeta(
            path = "/test",
            method = HttpMethod.Get,
            documentation = OpenApiRoute().also { route ->
                route.request {
                    multipartBody {
                        mediaType(ContentType.MultiPart.FormData)
                        part<File>("image") {
                            mediaTypes = setOf(
                                ContentType.Image.PNG,
                                ContentType.Image.JPEG,
                                ContentType.Image.GIF
                            )
                        }
                        part<String>("data") {
                            mediaTypes = setOf(ContentType.Text.Plain)
                        }
                    }
                }
            },
            protected = false
        )
        val schemaContext = schemaContext(listOf(route))
        val exampleContext = exampleContext(listOf(route))
        buildOperationObject(route, schemaContext, exampleContext).also { operation ->
            operation.requestBody
                .also { it.shouldNotBeNull() }
                ?.also { body ->
                    body.content
                        .also { it.shouldNotBeNull() }
                        ?.also { content ->
                            content shouldHaveSize 1
                            content.get("multipart/form-data")
                                .also { it.shouldNotBeNull() }
                                ?.also { mediaType ->
                                    mediaType.schema
                                        .also { it.shouldNotBeNull() }
                                        ?.also { schema ->
                                            schema.type shouldBe "object"
                                            schema.properties.keys shouldContainExactlyInAnyOrder listOf(
                                                "image",
                                                "data"
                                            )

                                        }
                                    mediaType.example shouldBe null
                                    mediaType.examples shouldBe null
                                    mediaType.encoding
                                        .also { it shouldHaveSize 2 }
                                        .also { it.keys shouldContainExactlyInAnyOrder listOf("image", "data") }
                                        .also { encoding ->
                                            encoding.get("image")?.also { image ->
                                                image.contentType shouldBe "image/png, image/jpeg, image/gif"
                                                image.headers shouldHaveSize 0
                                                image.style shouldBe null
                                                image.explode shouldBe null
                                                image.allowReserved shouldBe null
                                                image.extensions shouldBe null
                                            }
                                            encoding.get("data")?.also { data ->
                                                data.contentType shouldBe "text/plain"
                                                data.headers shouldHaveSize 0
                                                data.style shouldBe null
                                                data.explode shouldBe null
                                                data.allowReserved shouldBe null
                                                data.extensions shouldBe null
                                            }
                                        }
                                    mediaType.extensions shouldBe null
                                    mediaType.exampleSetFlag shouldBe false
                                }
                        }
                }
        }
    }

    "multipart body without parts" {
        val route = RouteMeta(
            path = "/test",
            method = HttpMethod.Get,
            documentation = OpenApiRoute().also { route ->
                route.request {
                    multipartBody {
                        mediaType(ContentType.MultiPart.FormData)
                    }
                }
            },
            protected = false
        )
        val schemaContext = schemaContext(listOf(route))
        val exampleContext = exampleContext(listOf(route))
        buildOperationObject(route, schemaContext, exampleContext).also { operation ->
            operation.requestBody
                .also { it.shouldNotBeNull() }
                ?.also { body ->
                    body.content
                        .also { it.shouldNotBeNull() }
                        ?.also { content ->
                            content shouldHaveSize 1
                            content.get("multipart/form-data")
                                .also { it.shouldNotBeNull() }
                                ?.also { mediaType ->
                                    mediaType.schema.shouldNotBeNull()
                                    mediaType.example shouldBe null
                                    mediaType.examples shouldBe null
                                    mediaType.encoding shouldBe null
                                    mediaType.extensions shouldBe null
                                    mediaType.exampleSetFlag shouldBe false
                                }
                        }
                }
        }
    }

    "multiple responses" {
        val route = RouteMeta(
            path = "/test",
            method = HttpMethod.Get,
            documentation = OpenApiRoute().also { route ->
                route.response {
                    default {
                        description = "Default Response"
                    }
                    HttpStatusCode.OK to {
                        description = "Successful Request"
                    }
                    HttpStatusCode.InternalServerError to {
                        description = "Failed Request"
                    }
                    "Custom" to {
                        description = "Custom Response"
                    }
                }
            },
            protected = false
        )
        val schemaContext = schemaContext(listOf(route))
        val exampleContext = exampleContext(listOf(route))
        buildOperationObject(route, schemaContext, exampleContext).also { operation ->
            operation.responses
                .also { it shouldHaveSize 4 }
                ?.also { responses ->
                    responses.get("default")
                        .also { it.shouldNotBeNull() }
                        ?.also { it.description shouldBe "Default Response" }
                    responses.get("200")
                        .also { it.shouldNotBeNull() }
                        ?.also { it.description shouldBe "Successful Request" }
                    responses.get("500")
                        .also { it.shouldNotBeNull() }
                        ?.also { it.description shouldBe "Failed Request" }
                    responses.get("Custom")
                        .also { it.shouldNotBeNull() }
                        ?.also { it.description shouldBe "Custom Response" }
                }
        }
    }

    "automatic unauthorized response for protected route" {
        val config = SwaggerUIPluginConfig().also {
            it.defaultUnauthorizedResponse {
                description = "Default unauthorized Response"
            }
        }
        val route = RouteMeta(
            path = "/test",
            method = HttpMethod.Get,
            documentation = OpenApiRoute().also { route ->
                route.response {
                    default {
                        description = "Default Response"
                    }
                }
            },
            protected = true
        )
        val schemaContext = schemaContext(listOf(route), config)
        val exampleContext = exampleContext(listOf(route), config)
        buildOperationObject(route, schemaContext, exampleContext, config).also { operation ->
            operation.responses
                .also { it shouldHaveSize 2 }
                ?.also { responses ->
                    responses.get("401")
                        .also { it.shouldNotBeNull() }
                        ?.also { it.description shouldBe "Default unauthorized Response" }
                    responses.get("default")
                        .also { it.shouldNotBeNull() }
                        ?.also { it.description shouldBe "Default Response" }
                }
        }
    }

    "automatic unauthorized response for unprotected route" {
        val config = SwaggerUIPluginConfig().also {
            it.defaultUnauthorizedResponse {
                description = "Default unauthorized Response"
            }
        }
        val route = RouteMeta(
            path = "/test",
            method = HttpMethod.Get,
            documentation = OpenApiRoute().also { route ->
                route.response {
                    default {
                        description = "Default Response"
                    }
                }
            },
            protected = false
        )
        val schemaContext = schemaContext(listOf(route), config)
        val exampleContext = exampleContext(listOf(route), config)
        buildOperationObject(route, schemaContext, exampleContext, config).also { operation ->
            operation.responses
                .also { it shouldHaveSize 1 }
                ?.also { responses ->
                    responses.get("default")
                        .also { it.shouldNotBeNull() }
                        ?.also { it.description shouldBe "Default Response" }
                }
        }
    }

    "complex body datatype" {
        val route = RouteMeta(
            path = "/test",
            method = HttpMethod.Get,
            documentation = OpenApiRoute().also { route ->
                route.request {
                    body<List<SimpleObject>>()
                }
            },
            protected = false
        )
        val schemaContext = schemaContext(listOf(route))
        val exampleContext = exampleContext(listOf(route))
        buildOperationObject(route, schemaContext, exampleContext).also { operation ->
            operation.requestBody
                .also { it.shouldNotBeNull() }
                ?.also { body ->
                    body.description shouldBe null
                    body.content
                        .also { it.shouldNotBeNull() }
                        ?.also { content ->
                            content shouldHaveSize 1
                            content["application/json"]
                                .also { it.shouldNotBeNull() }
                                ?.also { mediaType ->
                                    mediaType.schema
                                        .also { it.shouldNotBeNull() }
                                        ?.also { schema ->
                                            schema.type shouldBe "array"
                                            schema.items.also { item ->
                                                item.type shouldBe null
                                                item.`$ref` shouldBe "#/components/schemas/SimpleObject"
                                            }
                                        }
                                    mediaType.example shouldBe null
                                    mediaType.examples shouldBe null
                                    mediaType.encoding shouldBe null
                                    mediaType.extensions shouldBe null
                                    mediaType.exampleSetFlag shouldBe false
                                }

                        }
                    body.required shouldBe null
                    body.extensions shouldBe null
                    body.`$ref` shouldBe null
                }
        }
        schemaContext.getComponentsSection().also { section ->
            section.keys shouldContainExactlyInAnyOrder listOf("SimpleObject")
            section["SimpleObject"]?.also { schema ->
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactlyInAnyOrder listOf("number", "text")
            }
        }
    }

    "custom body schema" {
        val config = SwaggerUIPluginConfig().also {
            it.customSchemas {
                openApi("myCustomSchema") {
                    Schema<Any>().also { schema ->
                        schema.type = "object"
                        schema.properties = mapOf(
                            "custom" to Schema<Any>().also { prop ->
                                prop.type = "string"
                            }
                        )
                    }
                }
            }
        }
        val route = RouteMeta(
            path = "/test",
            method = HttpMethod.Get,
            documentation = OpenApiRoute().also { route ->
                route.request {
                    body(obj("myCustomSchema"))
                }
            },
            protected = false
        )
        val schemaContext = schemaContext(listOf(route), config)
        val exampleContext = exampleContext(listOf(route), config)
        buildOperationObject(route, schemaContext, exampleContext, config).also { operation ->
            operation.requestBody
                .also { it.shouldNotBeNull() }
                ?.also { body ->
                    body.description shouldBe null
                    body.content
                        .also { it.shouldNotBeNull() }
                        ?.also { content ->
                            content shouldHaveSize 1
                            content["application/json"]
                                .also { it.shouldNotBeNull() }
                                ?.also { mediaType ->
                                    mediaType.schema
                                        .also { it.shouldNotBeNull() }
                                        ?.also { schema -> schema.`$ref` shouldBe "#/components/schemas/myCustomSchema" }
                                    mediaType.example shouldBe null
                                    mediaType.examples shouldBe null
                                    mediaType.encoding shouldBe null
                                    mediaType.extensions shouldBe null
                                    mediaType.exampleSetFlag shouldBe false
                                }

                        }
                    body.required shouldBe null
                    body.extensions shouldBe null
                    body.`$ref` shouldBe null
                }
        }
    }

    "custom multipart-body schema" {
        val config = SwaggerUIPluginConfig().also {
            it.customSchemas {
                openApi("myCustomSchema") {
                    Schema<Any>().also { schema ->
                        schema.type = "object"
                        schema.properties = mapOf(
                            "custom" to Schema<Any>().also { prop ->
                                prop.type = "string"
                            }
                        )
                    }
                }
            }
        }
        val route = RouteMeta(
            path = "/test",
            method = HttpMethod.Get,
            documentation = OpenApiRoute().also { route ->
                route.request {
                    multipartBody {
                        mediaType(ContentType.MultiPart.FormData)
                        part("customData", obj("myCustomSchema"))
                    }
                }
            },
            protected = false
        )
        val schemaContext = schemaContext(listOf(route), config)
        val exampleContext = exampleContext(listOf(route), config)
        buildOperationObject(route, schemaContext, exampleContext, config).also { operation ->
            operation.requestBody
                .also { it.shouldNotBeNull() }
                ?.also { body ->
                    body.content
                        .also { it.shouldNotBeNull() }
                        ?.also { content ->
                            content shouldHaveSize 1
                            content["multipart/form-data"]
                                .also { it.shouldNotBeNull() }
                                ?.also { mediaType ->
                                    mediaType.schema
                                        .also { it.shouldNotBeNull() }
                                        ?.also { schema ->
                                            schema.type shouldBe "object"
                                            schema.properties.keys shouldContainExactlyInAnyOrder listOf("customData")
                                            schema.properties["customData"]!!.`$ref` shouldBe "#/components/schemas/myCustomSchema"
                                        }
                                    mediaType.example shouldBe null
                                    mediaType.examples shouldBe null
                                    mediaType.encoding shouldBe null
                                    mediaType.extensions shouldBe null
                                    mediaType.exampleSetFlag shouldBe false
                                }
                        }
                }
        }
    }

}) {

    companion object {

        private data class SimpleObject(
            val text: String,
            val number: Int
        )

        private val defaultPluginConfig = SwaggerUIPluginConfig()

        private fun schemaContext(
            routes: List<RouteMeta>,
            pluginConfig: SwaggerUIPluginConfig = defaultPluginConfig
        ): SchemaContext {
            return SchemaContextBuilder(
                config = pluginConfig,
                schemaBuilder = SchemaBuilder(
                    definitionsField = pluginConfig.encodingConfig.schemaDefinitionsField,
                    schemaEncoder = pluginConfig.encodingConfig.getSchemaEncoder()
                )
            ).build(routes)
        }

        private fun exampleContext(
            routes: List<RouteMeta>,
            pluginConfig: SwaggerUIPluginConfig = defaultPluginConfig
        ): ExampleContext {
            return ExampleContextBuilder(
                config = pluginConfig,
                exampleBuilder = ExampleBuilder(
                    config = pluginConfig
                )
            ).build(routes.toList())
        }


        private fun buildOperationObject(
            route: RouteMeta,
            schemaContext: SchemaContext,
            exampleContext: ExampleContext,
            pluginConfig: SwaggerUIPluginConfig = defaultPluginConfig
        ): Operation {
            return OperationBuilder(
                operationTagsBuilder = OperationTagsBuilder(pluginConfig),
                parameterBuilder = ParameterBuilder(
                    schemaContext = schemaContext,
                    exampleContext = exampleContext
                ),
                requestBodyBuilder = RequestBodyBuilder(
                    contentBuilder = ContentBuilder(
                        schemaContext = schemaContext,
                        exampleContext = exampleContext,
                        headerBuilder = HeaderBuilder(schemaContext)
                    )
                ),
                responsesBuilder = ResponsesBuilder(
                    responseBuilder = ResponseBuilder(
                        headerBuilder = HeaderBuilder(schemaContext),
                        contentBuilder = ContentBuilder(
                            schemaContext = schemaContext,
                            exampleContext = exampleContext,
                            headerBuilder = HeaderBuilder(schemaContext)
                        )
                    ),
                    config = pluginConfig
                ),
                securityRequirementsBuilder = SecurityRequirementsBuilder(pluginConfig),
            ).build(route)
        }

    }

}