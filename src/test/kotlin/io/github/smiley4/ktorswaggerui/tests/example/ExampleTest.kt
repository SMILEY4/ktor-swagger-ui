package io.github.smiley4.ktorswaggerui.tests.example

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRequestParameter
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.github.smiley4.ktorswaggerui.dsl.OpenApiSimpleBody
import io.github.smiley4.ktorswaggerui.spec.example.ExampleContext
import io.github.smiley4.ktorswaggerui.spec.example.ExampleContextBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.ExampleBuilder
import io.github.smiley4.ktorswaggerui.spec.route.RouteMeta
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.http.HttpMethod

class ExampleTest : StringSpec({

    "no request parameter example" {
        val route = route {
            request {
                queryParameter<String>("param")
            }
        }
        val exampleContext = exampleContext(listOf(route))
        exampleContext.getExample(route.getRequestParameter("param")) shouldBe null
    }

    "primitive request parameter examples" {
        val route = route {
            request {
                queryParameter<String>("stringParam") {
                    example = "Example Value"
                }
                queryParameter<String>("intParam") {
                    example = 42
                }
                queryParameter<String>("boolParam") {
                    example = true
                }
            }
        }
        val exampleContext = exampleContext(listOf(route))
        exampleContext.getExample(route.getRequestParameter("stringParam"))
            .also { it shouldNotBe null }
            ?.also { example ->
                example shouldBe "Example Value"
            }
        exampleContext.getExample(route.getRequestParameter("intParam"))
            .also { it shouldNotBe null }
            ?.also { example ->
                example shouldBe "42"
            }
        exampleContext.getExample(route.getRequestParameter("boolParam"))
            .also { it shouldNotBe null }
            ?.also { example ->
                example shouldBe "true"
            }
    }


    "no body example" {
        val route = route {
            request {
                body<String> {
                    example("differentExample", "Example Value")
                }
            }
        }
        val exampleContext = exampleContext(listOf(route))
        exampleContext.getExample(route.getRequestBody(), "testExample") shouldBe null
    }

    "simple body example" {
        val route = route {
            request {
                body<String> {
                    example("testExample", "Example Value") {
                        summary = "test summary"
                        description = "test description"
                    }
                }
            }
        }
        val exampleContext = exampleContext(listOf(route))
        exampleContext.getExample(route.getRequestBody(), "testExample")
            .also { it shouldNotBe null }
            ?.also { example ->
                example.value shouldBe "Example Value"
                example.description shouldBe "test description"
                example.summary shouldBe "test summary"
            }
    }

    "object body example" {
        val route = route {
            request {
                body<SimpleObject> {
                    example("testExample", SimpleObject("someText", 42)) {
                        summary = "test summary"
                        description = "test description"
                    }
                }
            }
        }
        val exampleContext = exampleContext(listOf(route))
        exampleContext.getExample(route.getRequestBody(), "testExample")
            .also { it shouldNotBe null }
            ?.also { example ->
                example.value shouldBe "{\"text\":\"someText\",\"number\":42}"
                example.description shouldBe "test description"
                example.summary shouldBe "test summary"
            }
    }

    "object inheritance body example" {
        val route = route {
            request {
                body<ExampleRequest> {
                    example("a", ExampleRequest.A("test a")) {
                        summary = "a summary"
                        description = "a description"
                    }
                    example("b", ExampleRequest.B(42)) {
                        summary = "b summary"
                        description = "b description"
                    }
                }
            }
        }
        val exampleContext = exampleContext(listOf(route))
        exampleContext.getExample(route.getRequestBody(), "a")
            .also { it shouldNotBe null }
            ?.also { example ->
                example.value shouldBe "{\"thisIsA\":\"test a\"}"
                example.description shouldBe "a description"
                example.summary shouldBe "a summary"
            }
        exampleContext.getExample(route.getRequestBody(), "b")
            .also { it shouldNotBe null }
            ?.also { example ->
                example.value shouldBe "{\"thisIsB\":42}"
                example.description shouldBe "b description"
                example.summary shouldBe "b summary"
            }
    }

}) {

    companion object {

        private data class SimpleObject(
            val text: String,
            val number: Int
        )

        private sealed class ExampleRequest {

            data class A(
                val thisIsA: String
            ) : ExampleRequest()


            data class B(
                val thisIsB: Int
            ) : ExampleRequest()

        }

        private val defaultPluginConfig = SwaggerUIPluginConfig()

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

        fun route(block: OpenApiRoute.() -> Unit): RouteMeta {
            return RouteMeta(
                path = "/test",
                method = HttpMethod.Get,
                documentation = OpenApiRoute().apply(block),
                protected = false
            )
        }

        fun RouteMeta.getRequestParameter(name: String): OpenApiRequestParameter {
            return this.documentation.getRequest().getParameters().find { it.name == name }!!
        }

        fun RouteMeta.getRequestBody(): OpenApiSimpleBody {
            return this.documentation.getRequest().getBody()!! as OpenApiSimpleBody
        }

    }
}