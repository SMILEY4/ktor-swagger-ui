package io.github.smiley4.ktorswaggerui.builder.openapi

import io.github.smiley4.ktorswaggerui.data.ServerData
import io.swagger.v3.oas.models.servers.Server

class ServerBuilder {

    fun build(server: ServerData): Server =
        Server().also {
            it.url = server.url
            it.description = server.description
        }

}
