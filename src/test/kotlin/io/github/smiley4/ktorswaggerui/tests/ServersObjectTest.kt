package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.dsl.OpenApiServer
import io.github.smiley4.ktorswaggerui.specbuilder.OApiServersBuilder
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.swagger.v3.oas.models.servers.Server

class ServersObjectTest : StringSpec({

    "test default server object" {
        val server = buildServerObject {}
        server shouldBeServer {
            url = "/"
        }
    }

    "test complete server object" {
        val server = buildServerObject {
            url = "Test URL"
            description = "Test Description"
        }
        server shouldBeServer {
            url = "Test URL"
            description = "Test Description"
        }
    }

    "test multiple server objects" {
        val servers = buildServerObjects(
            {
                url = "Test URL 1"
                description = "Test Description 1"
            },
            {
                url = "Test URL 2"
                description = "Test Description 2"
            }
        )
        servers[0] shouldBeServer {
            url = "Test URL 1"
            description = "Test Description 1"
        }
        servers[1] shouldBeServer {
            url = "Test URL 2"
            description = "Test Description 2"
        }
    }

}) {

    companion object {

        private fun buildServerObject(builder: OpenApiServer.() -> Unit): Server {
            return getOApiServersBuilder().build(listOf(OpenApiServer().apply(builder))).let {
                it shouldHaveSize 1
                it.first()
            }
        }

        private fun buildServerObjects(vararg builder: OpenApiServer.() -> Unit): List<Server> {
            return getOApiServersBuilder().build(builder.map { OpenApiServer().apply(it) }).also {
                it shouldHaveSize builder.size
            }
        }

    }

}