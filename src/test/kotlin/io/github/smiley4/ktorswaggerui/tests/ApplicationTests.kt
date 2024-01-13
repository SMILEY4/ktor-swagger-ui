package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.PluginConfigDsl
import io.github.smiley4.ktorswaggerui.dsl.get
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotBeEmpty
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class ApplicationTests {

    @Test
    fun minimal() = swaggerUITestApplication {
        get("hello").also {
            it.status shouldBe HttpStatusCode.OK
            it.body shouldBe "Hello Test"
        }
        get("/").also {
            it.status shouldBe HttpStatusCode.NotFound
        }
        get("/swagger-ui").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Html
            it.body.shouldNotBeEmpty()
        }
        get("/swagger-ui/index.html").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Html
            it.body.shouldNotBeEmpty()
        }
        get("/swagger-ui/swagger-initializer.js").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Application.JavaScript
            it.body shouldContain "url: \"/swagger-ui/api.json\""
        }
        get("/swagger-ui/api.json").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Application.Json
            it.body.shouldNotBeEmpty()
        }
    }


    @Test
    fun customRootHost() = swaggerUITestApplication(
        {
            swagger {
                rootHostPath = "my-root"
            }
        },
        followRedirects = false,
    ) {
        get("hello").also {
            it.status shouldBe HttpStatusCode.OK
            it.body shouldBe "Hello Test"
        }
        get("/").also {
            it.status shouldBe HttpStatusCode.NotFound
        }
        get("swagger-ui").also {
            it.status shouldBe HttpStatusCode.Found
            it.redirect shouldBe "my-root/swagger-ui/index.html"
        }
        get("swagger-ui/index.html").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Html
            it.body.shouldNotBeEmpty()
        }
        get("swagger-ui/swagger-initializer.js").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Application.JavaScript
            it.body shouldContain "url: \"/my-root/swagger-ui/api.json\""
        }
        get("swagger-ui/api.json").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Application.Json
            it.body.shouldNotBeEmpty()
        }
    }


    @Test
    fun forwardRoot() = swaggerUITestApplication({
        swagger {
            forwardRoot = true
        }
    }) {
        get("hello").also {
            it.status shouldBe HttpStatusCode.OK
            it.body shouldBe "Hello Test"
        }
        get("/").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Html
            it.body.shouldNotBeEmpty()
        }
        get("/swagger-ui").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Html
            it.body.shouldNotBeEmpty()
        }
        get("/swagger-ui/index.html").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Html
            it.body.shouldNotBeEmpty()
        }
        get("/swagger-ui/swagger-initializer.js").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Application.JavaScript
            it.body shouldContain "url: \"/swagger-ui/api.json\""
        }
        get("/swagger-ui/api.json").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Application.Json
            it.body.shouldNotBeEmpty()
        }
    }


    @Test
    fun forwardRootWithCustomSwaggerUrl() = swaggerUITestApplication({
        swagger {
            forwardRoot = true
            swaggerUrl = "test-swagger"
        }
    }) {
        get("/").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Html
            it.body.shouldNotBeEmpty()
        }
        get("/test-swagger").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Html
            it.body.shouldNotBeEmpty()
        }
        get("/test-swagger/index.html").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Html
            it.body.shouldNotBeEmpty()
        }
    }


    @Test
    fun protectedSwaggerUI() = swaggerUITestApplication({
        swagger {
            authentication = "my-auth"
        }
    }) {
        get("hello").also {
            it.status shouldBe HttpStatusCode.OK
            it.body shouldBe "Hello Test"
        }
        get("/").also {
            it.status shouldBe HttpStatusCode.NotFound
        }
        get("/swagger-ui").also {
            it.status shouldBe HttpStatusCode.Unauthorized
        }
        get("/swagger-ui/index.html").also {
            it.status shouldBe HttpStatusCode.Unauthorized
        }
        get("/swagger-ui/swagger-initializer.js").also {
            it.status shouldBe HttpStatusCode.Unauthorized
        }
        get("/swagger-ui/api.json").also {
            it.status shouldBe HttpStatusCode.Unauthorized
        }
    }


    @Test
    fun forwardRootAndProtectedSwaggerUI() = swaggerUITestApplication({
        swagger {
            authentication = "my-auth"
            forwardRoot = true
        }
    }) {
        get("hello").also {
            it.status shouldBe HttpStatusCode.OK
            it.body shouldBe "Hello Test"
        }
        get("/").also {
            it.status shouldBe HttpStatusCode.Unauthorized
        }
        get("/swagger-ui").also {
            it.status shouldBe HttpStatusCode.Unauthorized
        }
        get("/swagger-ui/index.html").also {
            it.status shouldBe HttpStatusCode.Unauthorized
        }
        get("/swagger-ui/swagger-initializer.js").also {
            it.status shouldBe HttpStatusCode.Unauthorized
        }
        get("/swagger-ui/api.json").also {
            it.status shouldBe HttpStatusCode.Unauthorized
        }
    }


    @Test
    fun customSwaggerUrl() = swaggerUITestApplication({
        swagger {
            swaggerUrl = "test-swagger"
        }
    }) {
        get("hello").also {
            it.status shouldBe HttpStatusCode.OK
            it.body shouldBe "Hello Test"
        }
        get("/").also {
            it.status shouldBe HttpStatusCode.NotFound
        }
        get("/swagger-ui").also {
            it.status shouldBe HttpStatusCode.NotFound
        }
        get("/swagger-ui/index.html").also {
            it.status shouldBe HttpStatusCode.NotFound
        }
        get("/swagger-ui/swagger-initializer.js").also {
            it.status shouldBe HttpStatusCode.NotFound
        }
        get("/swagger-ui/api.json").also {
            it.status shouldBe HttpStatusCode.NotFound
        }
        get("/test-swagger").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Html
            it.body.shouldNotBeEmpty()
        }
        get("/test-swagger/index.html").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Html
            it.body.shouldNotBeEmpty()
        }
        get("/test-swagger/swagger-initializer.js").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Application.JavaScript
            it.body shouldContain "url: \"/test-swagger/api.json\""

        }
        get("/test-swagger/api.json").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Application.Json
            it.body.shouldNotBeEmpty()
        }
    }


    @Test
    fun customSwaggerUrlAndProtected() = swaggerUITestApplication({
        swagger {
            authentication = "my-auth"
            swaggerUrl = "test-swagger"
        }
    }) {
        get("hello").also {
            it.status shouldBe HttpStatusCode.OK
            it.body shouldBe "Hello Test"
        }
        get("/").also {
            it.status shouldBe HttpStatusCode.NotFound
        }
        get("/swagger-ui").also {
            it.status shouldBe HttpStatusCode.NotFound
        }
        get("/swagger-ui/index.html").also {
            it.status shouldBe HttpStatusCode.NotFound
        }
        get("/swagger-ui/swagger-initializer.js").also {
            it.status shouldBe HttpStatusCode.NotFound
        }
        get("/swagger-ui/api.json").also {
            it.status shouldBe HttpStatusCode.NotFound
        }
        get("/test-swagger").also {
            it.status shouldBe HttpStatusCode.Unauthorized
        }
        get("/test-swagger/index.html").also {
            it.status shouldBe HttpStatusCode.Unauthorized
        }
        get("/test-swagger/swagger-initializer.js").also {
            it.status shouldBe HttpStatusCode.Unauthorized
        }
        get("/test-swagger/api.json").also {
            it.status shouldBe HttpStatusCode.Unauthorized
        }
    }


    @Test
    fun multipleSwaggerUI() = swaggerUITestApplication({
        specAssigner = { _, tags -> tags.firstOrNull() ?: "other" }
    }) {
        get("hello").also {
            it.status shouldBe HttpStatusCode.OK
            it.body shouldBe "Hello Test"
        }
        get("/").also {
            it.status shouldBe HttpStatusCode.NotFound
        }
        get("/swagger-ui/hello").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Html
            it.body.shouldNotBeEmpty()
        }
        get("/swagger-ui/hello/index.html").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Html
            it.body.shouldNotBeEmpty()
        }
        get("/swagger-ui/hello/swagger-initializer.js").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Application.JavaScript
            it.body shouldContain "url: \"/swagger-ui/hello/hello.json\""
        }
        get("/swagger-ui/hello/hello.json").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Application.Json
            it.body.shouldNotBeEmpty()
        }
        get("/swagger-ui/world").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Html
            it.body.shouldNotBeEmpty()
        }
        get("/swagger-ui/world/index.html").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Html
            it.body.shouldNotBeEmpty()
        }
        get("/swagger-ui/world/swagger-initializer.js").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Application.JavaScript
            it.body shouldContain "url: \"/swagger-ui/world/world.json\""
        }
        get("/swagger-ui/world/world.json").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Application.Json
            it.body.shouldNotBeEmpty()
        }
    }


    @Test
    fun multipleSwaggerUIWithDifferentAuthConfig() = swaggerUITestApplication({
        specAssigner = { _, tags -> tags.firstOrNull() ?: "other" }
        spec("hello") {
            swagger {
                authentication = null
            }
        }
        spec("world") {
            swagger {
                authentication = "my-auth"
            }
        }
    }) {
        get("/swagger-ui/hello/index.html").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Html
            it.body.shouldNotBeEmpty()
        }
        get("/swagger-ui/hello/hello.json").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Application.Json
            it.body.shouldNotBeEmpty()
        }
        get("/swagger-ui/world/index.html").also {
            it.status shouldBe HttpStatusCode.Unauthorized
        }
        get("/swagger-ui/world/world.json").also {
            it.status shouldBe HttpStatusCode.Unauthorized
        }
    }


    private fun swaggerUITestApplication(followRedirects: Boolean = true, block: suspend TestContext.() -> Unit) {
        swaggerUITestApplication({}, followRedirects, block)
    }

    private fun swaggerUITestApplication(
        pluginConfig: PluginConfigDsl.() -> Unit,
        followRedirects: Boolean = true,
        block: suspend TestContext.() -> Unit
    ) {
        testApplication {
            val client = createClient {
                this.followRedirects = followRedirects
            }
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
                install(SwaggerUI, pluginConfig)
                routing {
                    get("hello", {
                        tags = listOf("hello")
                        description = "Simple 'Hello World'- Route"
                        response {
                            HttpStatusCode.OK to {
                                description = "Successful Response"
                            }
                        }
                    }) {
                        call.respondText("Hello Test")
                    }
                    get("world", {
                        tags = listOf("world")
                        description = "Another simple 'Hello World'- Route"
                        response {
                            HttpStatusCode.OK to {
                                description = "Successful Response"
                            }
                        }
                    }) {
                        call.respondText("Hello World")
                    }
                }
                Thread.sleep(500)
            }
            TestContext(client).apply { block() }
        }
    }

    class TestContext(private val client: HttpClient) {

        suspend fun get(path: String): GetResult {
            return client.get(path)
                .let {
                    GetResult(
                        path = path,
                        status = it.status,
                        contentType = it.contentType(),
                        body = it.bodyAsText(),
                        redirect = it.headers["Location"]
                    )
                }
                .also { it.print() }
        }


        private fun GetResult.print() {
            println("GET ${this.path}  =>  ${this.status} (${this.contentType}): ${this.body}")
        }
    }


    data class GetResult(
        val path: String,
        val status: HttpStatusCode,
        val contentType: ContentType?,
        val body: String,
        val redirect: String?
    )

}
