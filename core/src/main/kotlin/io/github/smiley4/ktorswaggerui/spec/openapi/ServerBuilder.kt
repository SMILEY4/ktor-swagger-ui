package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.dsl.OpenApiServer
import io.swagger.v3.oas.models.servers.Server

class ServerBuilder {

    fun build(server: OpenApiServer): Server =
        Server().also {
            it.url = server.url
            it.description = server.description
        }

}