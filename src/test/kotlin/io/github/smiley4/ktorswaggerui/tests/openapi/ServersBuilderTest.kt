package io.github.smiley4.ktorswaggerui.tests.openapi

import io.github.smiley4.ktorswaggerui.dsl.OpenApiServer
import io.github.smiley4.ktorswaggerui.spec.openapi.ServerBuilder
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.swagger.v3.oas.models.servers.Server

class ServersBuilderTest : StringSpec({

    "default server object" {
        buildServerObject {}.also { server ->
            server.url shouldBe "/"
            server.description shouldBe null
            server.variables shouldBe null
            server.extensions shouldBe null

        }
    }

    "complete server object" {
        buildServerObject {
            url = "Test URL"
            description = "Test Description"
        }.also { server ->
            server.url shouldBe "Test URL"
            server.description shouldBe "Test Description"
            server.variables shouldBe null
            server.extensions shouldBe null
        }
    }

}) {

    companion object {

        private fun buildServerObject(builder: OpenApiServer.() -> Unit): Server {
            return ServerBuilder().build(OpenApiServer().apply(builder))
        }

    }

}