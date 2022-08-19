package de.lruegner.ktorswaggerui.apispec

import de.lruegner.ktorswaggerui.documentation.RouteBody
import de.lruegner.ktorswaggerui.documentation.RoutePlainTextBody
import de.lruegner.ktorswaggerui.documentation.RouteTypedBody
import de.lruegner.ktorswaggerui.routing.SchemaRef
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema

/**
 * Generator for the OpenAPI Content Object (e.g. request and response bodies)
 */
class OApiContentGenerator {

    /**
     * Generate the Content Object from the given config
     */
    fun generate(config: RouteBody): Content {
        return Content().apply {
            when (config) {
                is RouteTypedBody -> {
                    addMediaType("application/json", MediaType().apply {
                        schema = Schema<String>().apply {
                            `$ref` = SchemaRef.ofClass(config.schema)
                        }
                    })
                }
                is RoutePlainTextBody -> {
                    addMediaType("text/plain", MediaType().apply {
                        schema = Schema<String>().apply {
                            type = "string"
                        }
                    })
                }
            }
        }
    }

}