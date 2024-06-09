package io.github.smiley4.ktorswaggerui.misc

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
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
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class RoutingTests {

    @Test
    fun basicRouting() = swaggerUITestApplication {
        get("hello").also {
            it.status shouldBe HttpStatusCode.OK
            it.body shouldBe "Hello Test"
        }
        get("/").also {
            it.status shouldBe HttpStatusCode.NotFound
        }
        get("/swagger").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Html
            it.body.shouldNotBeEmpty()
        }
        get("/swagger/index.html").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Html
            it.body.shouldNotBeEmpty()
        }
        get("/swagger/swagger-initializer.js").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Application.JavaScript
            it.body shouldContain "url: \"/api.json\""
        }
        get("/api.json").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Application.Json
            it.body.shouldNotBeEmpty()
        }
    }

    private fun swaggerUITestApplication(block: suspend TestContext.() -> Unit) {
        testApplication {
            val client = createClient {
                this.followRedirects = followRedirects
            }
            install(SwaggerUI)
            routing {
                route("api.json") {
                    openApiSpec()
                }
                route("swagger") {
                    swaggerUI("/api.json")
                }
                get("hello") {
                    call.respondText("Hello Test")
                }
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
