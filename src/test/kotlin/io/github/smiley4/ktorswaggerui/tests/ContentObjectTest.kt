package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.OpenApiBody
import io.github.smiley4.ktorswaggerui.specbuilder.ComponentsContext
import io.kotest.core.spec.style.StringSpec
import io.ktor.http.ContentType
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.XML
import kotlin.reflect.KClass

class ContentObjectTest : StringSpec({

    "test default (plain-text) content object" {
        val content = buildContentObject(String::class) {}
        content shouldBeContent {
            addMediaType(ContentType.Text.Plain.toString(), MediaType().apply {
                schema = Schema<Any>().apply {
                    type = "string"
                    xml = XML().apply { name = "String" }
                }
            })
        }
    }

    "test default (json) content object" {
        val content = buildContentObject(SimpleBody::class) {}
        content shouldBeContent {
            addMediaType(ContentType.Application.Json.toString(), MediaType().apply {
                schema = Schema<Any>().apply {
                    type = "object"
                    xml = XML().apply { name = "SimpleBody" }
                    properties = mapOf(
                        "someText" to Schema<Any>().apply {
                            type = "string"
                        }
                    )
                }
            })
        }
    }

    "test complete (plain-text) content object" {
        val content = buildContentObject(String::class) {
            description = "Test Description"
            required = true
            example("Example1", "Example Value 1")
            example("Example2", "Example Value 2")
        }
        content shouldBeContent {
            addMediaType(ContentType.Text.Plain.toString(), MediaType().apply {
                schema = Schema<Any>().apply {
                    type = "string"
                    xml = XML().apply { name = "String" }
                }
                examples = mapOf(
                    "Example1" to Example().apply {
                        value = "Example Value 1"
                    },
                    "Example2" to Example().apply {
                        value = "Example Value 2"
                    }
                )
            })
        }
    }

    "test xml content object" {
        val content = buildContentObject(SimpleBody::class) {
            mediaType(ContentType.Application.Xml)
        }
        content shouldBeContent {
            addMediaType(ContentType.Application.Xml.toString(), MediaType().apply {
                schema = Schema<Any>().apply {
                    type = "object"
                    xml = XML().apply { name = "SimpleBody" }
                    properties = mapOf(
                        "someText" to Schema<Any>().apply {
                            type = "string"
                        }
                    )
                }
            })
        }
    }

    "test image content object" {
        val content = buildContentObject(null) {
            mediaType(ContentType.Image.SVG)
            mediaType(ContentType.Image.PNG)
            mediaType(ContentType.Image.JPEG)
        }
        content shouldBeContent {
            addMediaType(ContentType.Image.SVG.toString(), MediaType())
            addMediaType(ContentType.Image.PNG.toString(), MediaType())
            addMediaType(ContentType.Image.JPEG.toString(), MediaType())
        }
    }

    "test content object with custom (remote) json-schema" {
        val content = buildCustomContentObject("remote")
        content shouldBeContent {
            addMediaType(ContentType.Application.Json.toString(), MediaType().apply {
                schema = Schema<Any>().apply {
                    type = "object"
                    `$ref` = "/my/test/schema"
                }
            })
        }
    }

    "test content object with custom (remote) json-schema and components-section enabled" {
        val content = buildCustomContentObject("remote", ComponentsContext(true, mutableMapOf(), true, mutableMapOf()))
        content shouldBeContent {
            addMediaType(ContentType.Application.Json.toString(), MediaType().apply {
                schema = Schema<Any>().apply {
                    type = "object"
                    `$ref` = "/my/test/schema"
                }
            })
        }
    }

    "test content object with custom json-schema" {
        val content = buildCustomContentObject("custom")
        content shouldBeContent {
            addMediaType(ContentType.Application.Json.toString(), MediaType().apply {
                schema = Schema<Any>().apply {
                    type = "object"
                    properties = mapOf(
                        "someBoolean" to Schema<Any>().apply {
                            type = "boolean"
                        },
                        "someText" to Schema<Any>().apply {
                            type = "string"
                        }
                    )
                }
            })
        }
    }

    "test content object with custom json-schema and components-section enabled" {
        val content = buildCustomContentObject("custom", ComponentsContext(true, mutableMapOf(), true, mutableMapOf()))
        content shouldBeContent {
            addMediaType(ContentType.Application.Json.toString(), MediaType().apply {
                schema = Schema<Any>().apply {
                    `$ref` = "#/components/schemas/custom"
                }
            })
        }
    }

}) {

    companion object {

        private fun pluginConfig() = SwaggerUIPluginConfig().apply {
            customSchemas {
                remote("remote", "/my/test/schema")
                customOpenApi("custom") {
                    """
                        {
                            "type": "object",
                            "properties": {
                                "someBoolean": {
                                    "type": "boolean"
                                },
                                "someText": {
                                    "type": "string"
                                }
                            }
                        }
                    """.trimIndent()
                }
            }
        }

        private fun buildContentObject(schema: KClass<*>?, builder: OpenApiBody.() -> Unit): Content {
            return buildContentObject(ComponentsContext.NOOP, schema, builder)
        }


        private fun buildContentObject(
            componentCtx: ComponentsContext,
            type: KClass<*>?,
            builder: OpenApiBody.() -> Unit
        ): Content {
            return getOApiContentBuilder().build(OpenApiBody(type?.java).apply(builder), componentCtx, pluginConfig())
        }

        private fun buildCustomContentObject(schemaId: String, componentCtx: ComponentsContext = ComponentsContext.NOOP): Content {
            return getOApiContentBuilder().build(OpenApiBody(null).apply { customSchemaId = schemaId }, componentCtx, pluginConfig())
        }

        private data class SimpleBody(
            val someText: String
        )

    }

}