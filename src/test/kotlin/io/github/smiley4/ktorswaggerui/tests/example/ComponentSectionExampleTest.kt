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

class ComponentSectionExampleTest : StringSpec({


    "component section example" {
        val routes = listOf(
            route {
                request {
                    body<SimpleObject> {
                        example("example_1", SimpleObject("e1", 1)) {
                            summary = "example 1"
                            description = "example 1"
                        }
                    }
                }
            },
            route {
                request {
                    body<SimpleObject> {
                        example("example_1", SimpleObject("e1", 1)) {
                            summary = "example 1"
                            description = "example 1"
                        }
                    }
                }
            },
            route {
                request {
                    body<SimpleObject> {
                        example("example_1", SimpleObject("e1_different", 1)) {
                            summary = "example 1 but different"
                            description = "example 1 but different"
                        }
                    }
                }
            },
            route {
                request {
                    body<DifferentSimpleObject> {
                        example("example_1", SimpleObject("e1", 1)) {
                            summary = "example 1"
                            description = "example 1"
                        }
                    }
                }
            },
            route {
                request {
                    body<SimpleObject> {
                        example("example_2", SimpleObject("e2", 2)) {
                            summary = "example 2"
                            description = "example 2"
                        }
                    }
                }
            }

        )
        val exampleContext = exampleContext(routes)
        println()
    }

}) {

    companion object {

        private data class SimpleObject(
            val text: String,
            val number: Int
        )

        private data class DifferentSimpleObject(
            val text: String,
            val number: Int
        )

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