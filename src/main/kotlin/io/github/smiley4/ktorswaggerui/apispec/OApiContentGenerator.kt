package io.github.smiley4.ktorswaggerui.apispec

import io.github.smiley4.ktorswaggerui.documentation.RouteBody
import io.github.smiley4.ktorswaggerui.documentation.RoutePlainTextBody
import io.github.smiley4.ktorswaggerui.documentation.RouteTypedBody
import io.github.smiley4.ktorswaggerui.routing.SchemaRef
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
                            `$ref` = SchemaRef.refOfClass(config.schema)
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