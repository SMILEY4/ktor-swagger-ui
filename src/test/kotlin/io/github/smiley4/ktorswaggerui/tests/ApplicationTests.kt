package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.get
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.withCharset
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class ApplicationTests {

    private fun ApplicationTestBuilder.setupTestApplication(pluginConfig: SwaggerUIPluginConfig.() -> Unit) {
        application {
            install(Authentication) {
                basic("my-auth") {
                    validate { credentials ->
                        if (credentials.name == "user" && credentials.password == "pass") {
                            UserIdPrincipal(credentials.name)
                        } else {
                            null
                        }
                    }
                }
            }
            install(SwaggerUI) { pluginConfig() }
            routing {
                get("hello", {
                    description = "Simple 'Hello World'- Route"
                    response {
                        HttpStatusCode.OK to {
                            description = "Successful Response"
                        }
                    }
                }) {
                    call.respondText("Hello Test")
                }
            }
        }
    }


    @Test
    fun testDefaultSwaggerUi() = testApplication {
        setupTestApplication {}
        client.get("/hello").bodyAsText() shouldBe "Hello Test"
        client.get("/").status shouldBe HttpStatusCode.NotFound
        client.get("/swagger-ui").let {
            it.status shouldBe HttpStatusCode.OK
            it.contentType() shouldBe ContentType.Text.Html
            it.bodyAsText().shouldNotBeEmpty()
        }
        client.get("/swagger-ui/api.json").let {
            it.status shouldBe HttpStatusCode.OK
            it.contentType() shouldBe ContentType.Application.Json.withCharset(Charsets.UTF_8)
            it.bodyAsText().shouldNotBeEmpty()
        }
    }

    @Test
    fun testSwaggerUiForwardRoot() = testApplication {
        setupTestApplication {
            swagger {
                forwardRoot = true
            }
        }
        client.get("/hello").bodyAsText() shouldBe "Hello Test"
        client.get("/").let {
            it.status shouldBe HttpStatusCode.OK
            it.contentType() shouldBe ContentType.Text.Html
            it.bodyAsText().shouldNotBeEmpty()
        }
        client.get("/swagger-ui").let {
            it.status shouldBe HttpStatusCode.OK
            it.contentType() shouldBe ContentType.Text.Html
            it.bodyAsText().shouldNotBeEmpty()
        }
        client.get("/swagger-ui/api.json").let {
            it.status shouldBe HttpStatusCode.OK
            it.contentType() shouldBe ContentType.Application.Json.withCharset(Charsets.UTF_8)
            it.bodyAsText().shouldNotBeEmpty()
        }
    }

    @Test
    fun testSwaggerUiProtected() = testApplication {
        setupTestApplication {
            swagger {
                authentication = "my-auth"
            }
        }
        client.get("/hello").bodyAsText() shouldBe "Hello Test"
        client.get("/").status shouldBe HttpStatusCode.NotFound
        client.get("/swagger-ui").status shouldBe HttpStatusCode.Unauthorized
        client.get("/swagger-ui/api.json").status shouldBe HttpStatusCode.Unauthorized
    }

    @Test
    fun testSwaggerUiProtectedAndForwardRoot() = testApplication {
        setupTestApplication {
            swagger {
                authentication = "my-auth"
                forwardRoot = true
            }
        }
        client.get("/hello").bodyAsText() shouldBe "Hello Test"
        client.get("/").status shouldBe HttpStatusCode.Unauthorized
        client.get("/swagger-ui").status shouldBe HttpStatusCode.Unauthorized
        client.get("/swagger-ui/api.json").status shouldBe HttpStatusCode.Unauthorized
    }


    @Test
    fun testSwaggerUiCustomRoute() = testApplication {
        setupTestApplication {
            swagger {
                swaggerUrl = "test-swagger"
            }
        }
        client.get("/hello").bodyAsText() shouldBe "Hello Test"
        client.get("/swagger-ui").status shouldBe HttpStatusCode.NotFound
        client.get("/swagger-ui/api.json").status shouldBe HttpStatusCode.NotFound
        client.get("/test-swagger").let {
            it.status shouldBe HttpStatusCode.OK
            it.contentType() shouldBe ContentType.Text.Html
            it.bodyAsText().shouldNotBeEmpty()
        }
        client.get("/test-swagger/api.json").let {
            it.status shouldBe HttpStatusCode.OK
            it.contentType() shouldBe ContentType.Application.Json.withCharset(Charsets.UTF_8)
            it.bodyAsText().shouldNotBeEmpty()
        }
    }

    @Test
    fun testSwaggerUiCustomRouteProtected() = testApplication {
        setupTestApplication {
            swagger {
                authentication = "my-auth"
                swaggerUrl = "test-swagger"
            }
        }
        client.get("/hello").bodyAsText() shouldBe "Hello Test"
        client.get("/swagger-ui").status shouldBe HttpStatusCode.NotFound
        client.get("/swagger-ui/api.json").status shouldBe HttpStatusCode.NotFound
        client.get("/test-swagger").status shouldBe HttpStatusCode.Unauthorized
        client.get("/test-swagger/api.json").status shouldBe HttpStatusCode.Unauthorized
    }

}