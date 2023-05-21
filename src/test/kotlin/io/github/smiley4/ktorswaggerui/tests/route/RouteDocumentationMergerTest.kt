package io.github.smiley4.ktorswaggerui.tests.route

import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.github.smiley4.ktorswaggerui.spec.route.RouteDocumentationMerger
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class RouteDocumentationMergerTest : StringSpec({

    "merge empty routes" {
        merge(
            route {},
            route {}
        ).also { route ->
            route.tags.shouldBeEmpty()
            route.summary shouldBe null
            route.description shouldBe null
            route.operationId shouldBe null
            route.deprecated shouldBe false
            route.hidden shouldBe false
            route.securitySchemeName shouldBe null
            route.securitySchemeNames.shouldBeEmpty()
            route.getRequest().also { requests ->
                requests.getParameters().shouldBeEmpty()
                requests.getBody() shouldBe null
            }
            route.getResponses().also { responses ->
                responses.getResponses().shouldBeEmpty()
            }
        }
    }

    "merge complete routes" {
        merge(
            route {
                tags = listOf("a1", "a2")
                summary = "Summary A"
                description = "Description A"
                operationId = "operationA"
                securitySchemeName = "securitySchemeNameA"
                securitySchemeNames = listOf("securitySchemeNameA1", "securitySchemeNameA2")
                deprecated = true
                hidden = false
                request {
                    queryParameter<String>("query")
                    pathParameter<String>("pathA1")
                    pathParameter<String>("pathA2")
                    body<String> {
                        description = "body A"
                    }
                }
                response {
                    "a1" to { description = "response a1" }
                    "a2" to { description = "response a1" }
                }
            },
            route {
                tags = listOf("b1", "b2")
                summary = "Summary B"
                description = "Description B"
                operationId = "operationB"
                securitySchemeName = "securitySchemeNameB"
                securitySchemeNames = listOf("securitySchemeNameB1", "securitySchemeNameB2")
                deprecated = false
                hidden = true
                request {
                    queryParameter<String>("query")
                    pathParameter<String>("pathB1")
                    pathParameter<String>("pathB2")
                    body<String> {
                        description = "body B"
                    }
                }
                response {
                    "b1" to { description = "response b1" }
                    "b2" to { description = "response b1" }
                }
            }
        ).also { route ->
            route.tags shouldContainExactlyInAnyOrder listOf("a1", "a2", "b1", "b2")
            route.summary shouldBe "Summary A"
            route.description shouldBe "Description A"
            route.operationId shouldBe "operationA"
            route.deprecated shouldBe true
            route.hidden shouldBe true
            route.securitySchemeName shouldBe "securitySchemeNameA"
            route.securitySchemeNames shouldContainExactlyInAnyOrder listOf(
                "securitySchemeNameA1",
                "securitySchemeNameA2",
                "securitySchemeNameB1",
                "securitySchemeNameB2"
            )
            route.getRequest().also { requests ->
                requests.getParameters().map { it.name } shouldContainExactlyInAnyOrder listOf(
                    "query",
                    "pathA1",
                    "pathA2",
                    "query",
                    "pathB1",
                    "pathB2"
                )
                requests.getBody()
                    .also { it shouldNotBe null }
                    ?.also { it.description shouldBe "body A" }
            }
            route.getResponses().also { responses ->
                responses.getResponses().map { it.statusCode } shouldContainExactlyInAnyOrder listOf(
                    "b1", "b2", "a1", "a2"
                )
            }
        }
    }

}) {

    companion object {

        fun route(builder: OpenApiRoute.() -> Unit): OpenApiRoute {
            return OpenApiRoute().apply(builder)
        }

        fun merge(a: OpenApiRoute, b: OpenApiRoute): OpenApiRoute {
            return RouteDocumentationMerger().merge(a, b)
        }

    }

}