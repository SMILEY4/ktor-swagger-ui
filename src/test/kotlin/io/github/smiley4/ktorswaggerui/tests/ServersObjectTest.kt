package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.OpenApiServerConfig
import io.github.smiley4.ktorswaggerui.apispec.OApiServersGenerator
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.swagger.v3.oas.models.servers.Server

class ServersObjectTest : StringSpec({

    "test default server object" {
        val server = generateServerObject {}
        server shouldBeServer {
            url = "/"
        }
    }

    "test complete server object" {
        val server = generateServerObject {
            url = "Test URL"
            description = "Test Description"
        }
        server shouldBeServer {
            url = "Test URL"
            description = "Test Description"
        }
    }

    "test multiple server objects" {
        val servers = generateServerObjects(
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

        private fun generateServerObject(builder: OpenApiServerConfig.() -> Unit): Server {
            return OApiServersGenerator().generate(listOf(OpenApiServerConfig().apply(builder))).let {
                it shouldHaveSize 1
                it.first()
            }
        }

        private fun generateServerObjects(vararg builder: OpenApiServerConfig.() -> Unit): List<Server> {
            return OApiServersGenerator().generate(builder.map { OpenApiServerConfig().apply(it) }).also {
                it shouldHaveSize builder.size
            }
        }

    }

}