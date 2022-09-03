package io.github.smiley4.ktorswaggerui.apispec

import io.github.smiley4.ktorswaggerui.documentation.BodyDocumentation
import io.github.smiley4.ktorswaggerui.documentation.ExampleDocumentation
import io.ktor.http.ContentType
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.KClass

/**
 * Generator for the OpenAPI Content Object (e.g. request and response bodies)
 */
class OApiContentGenerator {

    /**
     * Generate the Content Object from the given config
     */
    fun generate(config: BodyDocumentation, componentCtx: ComponentsContext): Content {
        return Content().apply {
            OApiSchemaGenerator().generate(config.schema, componentCtx).let {
                when (it.type) {
                    "integer" -> addPlainText(this, it, config.getExamples())
                    "number" -> addPlainText(this, it, config.getExamples())
                    "boolean" -> addPlainText(this, it, config.getExamples())
                    "string" -> addPlainText(this, it, config.getExamples())
                    "object" -> addJson(this, it, config.getExamples())
                    "array" -> addJson(this, it, config.getExamples())
                    else -> addPlainText(this, it, config.getExamples())
                }
            }
        }
    }

    private fun addPlainText(content: Content, schemaObj: Schema<*>, exampleObjects: Map<String, ExampleDocumentation>) {
        content.addMediaType("text/plain", MediaType().apply {
            schema = schemaObj
            exampleObjects.forEach { (name, obj) ->
                addExamples(name, OApiExampleGenerator().generate(obj))
            }
        })
    }

    private fun addJson(content: Content, schemaObj: Schema<*>, exampleObjects: Map<String, ExampleDocumentation>) {
        content.addMediaType("application/json", MediaType().apply {
            schema = schemaObj
            exampleObjects.forEach { (name, obj) ->
                addExamples(name, OApiExampleGenerator().generate(obj))
            }
        })
    }

}