package io.github.smiley4.ktorswaggerui.apispec

import io.github.smiley4.ktorswaggerui.documentation.BodyDocumentation
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
    fun generate(config: BodyDocumentation): Content {
        return Content().apply {
            OApiSchemaGenerator().generate(config.schema).let {
                when (it.type) {
                    "integer" -> addPlainText(this, it)
                    "number" -> addPlainText(this, it)
                    "boolean" -> addPlainText(this, it)
                    "string" -> addPlainText(this, it)
                    "object" -> addJson(this, it)
                    "array" -> addJson(this, it)
                    else -> addPlainText(this, it)
                }
            }
        }
    }

    private fun addPlainText(content: Content, schemaObj: Schema<*>) {
        content.addMediaType("text/plain", MediaType().apply {
            schema = schemaObj
        })
    }

    private fun addJson(content: Content, schemaObj: Schema<*>) {
        content.addMediaType("application/json", MediaType().apply {
            schema = schemaObj
        })
    }

}