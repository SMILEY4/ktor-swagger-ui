package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.specbuilder.ComponentsContext
import io.github.smiley4.ktorswaggerui.specbuilder.RouteMeta
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.github.smiley4.ktorswaggerui.dsl.OpenApiResponse
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.headers.Header
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.oas.models.security.SecurityRequirement

class PathObjectTest : StringSpec({

    "test get-route" {
        val path = buildPath(HttpMethod.Get, "test/path") {}
        path.first shouldBe "test/path"
        path.second shouldBePath {
            get = Operation().apply {
                tags = emptyList()
                parameters = emptyList()
                responses = ApiResponses()
            }
        }
    }

    "test post-route" {
        val path = buildPath(HttpMethod.Post, "test/path") {}
        path.first shouldBe "test/path"
        path.second shouldBePath {
            post = Operation().apply {
                tags = emptyList()
                parameters = emptyList()
                responses = ApiResponses()
            }
        }
    }

    "test complete route" {
        val path = buildPath(HttpMethod.Get, "test/path") {
            tags = mutableListOf("tag1", "tag2")
            summary = "Test Summary"
            description = "Test Description"
            request {
                queryParameter("testParam", Int::class)
                body(String::class) {
                    description = "Test Request body"
                    required = true
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "Test OK-Response"
                    header("testHeader") {
                        type = String::class.java
                        description = "Test Header"
                    }
                    body(String::class) {
                        description = "Test Response body"
                        required = true
                    }
                }
                HttpStatusCode.Conflict to {
                    description = "Test Conflict-Response"
                }
            }
        }
        path.first shouldBe "test/path"
        path.second shouldBePath {
            get = Operation().apply {
                tags = listOf("tag1", "tag2")
                summary = "Test Summary"
                description = "Test Description"
                parameters = listOf(
                    Parameter().apply {
                        name = "testParam"
                    }
                )
                requestBody = RequestBody().apply {
                    description = "Test Request body"
                    required = true
                    content = Content().apply {/*...*/ }
                }
                responses = ApiResponses().apply {
                    addApiResponse("200", ApiResponse().apply {
                        description = "Test OK-Response"
                        headers = mapOf(
                            "testHeader" to Header().apply {
                                description = "Test Header"
                                schema = Schema<Any>().apply {
                                    type = "string"
                                }
                            }
                        )
                    })
                    addApiResponse("409", ApiResponse().apply {
                        description = "Test Conflict-Response"
                    })
                }
            }
        }
    }

    "test automatic tag generator" {
        val path = buildPath(HttpMethod.Get, "test/path", tagGenerator = tagGenerator()) {
            tags = mutableListOf("tag1", "tag2")
        }
        path.second shouldBePath {
            get = Operation().apply {
                tags = listOf("tag1", "tag2", "test")
                parameters = emptyList()
                responses = ApiResponses()
            }
        }
    }

    "test security scheme for non-protected path" {
        val path = buildPath(HttpMethod.Get, "test/path") {
            securitySchemeName = "TestAuth"
        }
        path.second shouldBePath {
            get = Operation().apply {
                tags = emptyList()
                parameters = emptyList()
                responses = ApiResponses()
            }
        }
    }

    "test security scheme for protected path" {
        val path = buildProtectedPath(HttpMethod.Get, "test/path") {
            securitySchemeName = "TestAuth"
        }
        path.second shouldBePath {
            get = Operation().apply {
                tags = emptyList()
                parameters = emptyList()
                responses = ApiResponses()
                security = listOf(SecurityRequirement().apply {
                    addList("TestAuth", emptyList())
                })
            }
        }
    }

    "test default security scheme for non-protected path" {
        val path = buildPath(HttpMethod.Get, "test/path", defaultSecuritySchemeName = "DefaultAuth") {}
        path.second shouldBePath {
            get = Operation().apply {
                tags = emptyList()
                parameters = emptyList()
                responses = ApiResponses()
            }
        }
    }

    "test default security scheme" {
        val path = buildProtectedPath(HttpMethod.Get, "test/path", defaultSecuritySchemeName = "DefaultAuth") {}
        path.second shouldBePath {
            get = Operation().apply {
                tags = emptyList()
                parameters = emptyList()
                responses = ApiResponses()
                security = listOf(SecurityRequirement().apply {
                    addList("DefaultAuth", emptyList())
                })
            }
        }
    }

    "test overwriting default security scheme" {
        val path = buildProtectedPath(HttpMethod.Get, "test/path", defaultSecuritySchemeName = "DefaultAuth") {
            securitySchemeName = "TestAuth"
        }
        path.second shouldBePath {
            get = Operation().apply {
                tags = emptyList()
                parameters = emptyList()
                responses = ApiResponses()
                security = listOf(SecurityRequirement().apply {
                    addList("TestAuth", emptyList())
                })
            }
        }
    }

    "test default unauthorized response for non-protected path" {
        val path = buildPath(HttpMethod.Get, "test/path", defaultUnauthorizedResponse()) {}
        path.second shouldBePath {
            get = Operation().apply {
                tags = emptyList()
                parameters = emptyList()
                responses = ApiResponses()
            }
        }
    }

    "test default unauthorized response" {
        val path = buildProtectedPath(HttpMethod.Get, "test/path", defaultUnauthorizedResponse()) {}
        path.second shouldBePath {
            get = Operation().apply {
                tags = emptyList()
                parameters = emptyList()
                responses = ApiResponses().apply {
                    addApiResponse("401", ApiResponse().apply {
                        description = "Authentication failed"
                    })
                }
            }
        }
    }

    "test overwriting default unauthorized response" {
        val path = buildProtectedPath(HttpMethod.Get, "test/path", defaultUnauthorizedResponse()) {
            response {
                HttpStatusCode.Unauthorized to {
                    description = "Test Unauthorized-Response"
                }
            }
        }
        path.second shouldBePath {
            get = Operation().apply {
                tags = emptyList()
                parameters = emptyList()
                responses = ApiResponses().apply {
                    addApiResponse("401", ApiResponse().apply {
                        description = "Test Unauthorized-Response"
                    })
                }
            }
        }
    }

}) {

    companion object {

        private fun buildPath(
            method: HttpMethod,
            path: String,
            defaultUnauthorizedResponse: OpenApiResponse? = null,
            tagGenerator: ((url: List<String>) -> String?)? = null,
            defaultSecuritySchemeName: String? = null,
            builder: OpenApiRoute.() -> Unit
        ): Pair<String, PathItem> {
            return getOApiPathBuilder().build(
                routeMeta(method, path, builder),
                defaultUnauthorizedResponse,
                defaultSecuritySchemeName,
                tagGenerator,
                ComponentsContext.NOOP
            )
        }

        private fun buildProtectedPath(
            method: HttpMethod,
            path: String,
            defaultUnauthorizedResponse: OpenApiResponse? = null,
            tagGenerator: ((url: List<String>) -> String?)? = null,
            defaultSecuritySchemeName: String? = null,
            builder: OpenApiRoute.() -> Unit
        ): Pair<String, PathItem> {
            return getOApiPathBuilder().build(
                protectedRouteMeta(method, path, builder),
                defaultUnauthorizedResponse,
                defaultSecuritySchemeName,
                tagGenerator,
                ComponentsContext.NOOP
            )
        }

        private fun routeMeta(method: HttpMethod, path: String, builder: OpenApiRoute.() -> Unit): RouteMeta {
            return RouteMeta(
                path = path,
                method = method,
                documentation = OpenApiRoute().apply(builder),
                protected = false
            )
        }

        private fun protectedRouteMeta(method: HttpMethod, path: String, builder: OpenApiRoute.() -> Unit): RouteMeta {
            return RouteMeta(
                path = path,
                method = method,
                documentation = OpenApiRoute().apply(builder),
                protected = true
            )
        }

        private fun defaultUnauthorizedResponse(): OpenApiResponse {
            return OpenApiResponse(HttpStatusCode.Unauthorized).apply {
                description = "Authentication failed"
            }
        }

        private fun tagGenerator(): ((url: List<String>) -> String?) {
            return { url -> url.firstOrNull() }
        }

    }

}