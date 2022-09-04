package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.dsl.OpenApiServer
import io.swagger.v3.oas.models.servers.Server

/**
 * Builder for the OpenAPI Server-Objects
 */
class OApiServersBuilder {

    fun build(servers: List<OpenApiServer>): List<Server> {
        return servers.map {
            Server().apply {
                url = it.url
                description = it.description
            }
        }
    }

}