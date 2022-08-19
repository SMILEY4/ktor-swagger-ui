package de.lruegner.ktorswaggerui.apispec

import de.lruegner.ktorswaggerui.OpenApiServerConfig
import io.swagger.v3.oas.models.servers.Server

/**
 * Generator for the OpenAPI Server-Objects
 */
class OApiServersGenerator() {

    /**
     * Generate the OpenAPI Server-Objects from the given configs
     */
    fun generate(configs: List<OpenApiServerConfig>): List<Server> {
        return configs.map {
            Server().apply {
                url = it.url
                description = it.description
            }
        }
    }

}