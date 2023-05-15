//package io.github.smiley4.ktorswaggerui.tests
//
//import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
//import io.github.smiley4.ktorswaggerui.dsl.CustomSchemaRef
//import io.github.smiley4.ktorswaggerui.dsl.OpenApiMultipartBody
//import io.github.smiley4.ktorswaggerui.dsl.OpenApiSimpleBody
//import io.github.smiley4.ktorswaggerui.dsl.array
//import io.github.smiley4.ktorswaggerui.dsl.obj
//import io.github.smiley4.ktorswaggerui.specbuilder.ComponentsContext
//import io.kotest.core.spec.style.StringSpec
//import io.ktor.http.ContentType
//import io.swagger.v3.oas.models.examples.Example
//import io.swagger.v3.oas.models.media.Content
//import io.swagger.v3.oas.models.media.Encoding
//import io.swagger.v3.oas.models.media.MediaType
//import io.swagger.v3.oas.models.media.Schema
//import io.swagger.v3.oas.models.media.XML
//import java.io.File
//import kotlin.reflect.KClass
//
//class ContentObjectTest : StringSpec({
//
//    "test default (plain-text) content object" {
//        val content = buildContentObject(String::class) {}
//        content shouldBeContent {
//            addMediaType(ContentType.Text.Plain.toString(), MediaType().apply {
//                schema = Schema<Any>().apply {
//                    type = "string"
//                    xml = XML().apply { name = "String" }
//                }
//            })
//        }
//    }
//
//    "test default (json) content object" {
//        val content = buildContentObject(SimpleBody::class) {}
//        content shouldBeContent {
//            addMediaType(ContentType.Application.Json.toString(), MediaType().apply {
//                schema = Schema<Any>().apply {
//                    type = "object"
//                    xml = XML().apply { name = "SimpleBody" }
//                    properties = mapOf(
//                        "someText" to Schema<Any>().apply {
//                            type = "string"
//                        }
//                    )
//                }
//            })
//        }
//    }
//
//    "test complete (plain-text) content object" {
//        val content = buildContentObject(String::class) {
//            description = "Test Description"
//            required = true
//            example("Example1", "Example Value 1")
//            example("Example2", "Example Value 2")
//        }
//        content shouldBeContent {
//            addMediaType(ContentType.Text.Plain.toString(), MediaType().apply {
//                schema = Schema<Any>().apply {
//                    type = "string"
//                    xml = XML().apply { name = "String" }
//                }
//                examples = mapOf(
//                    "Example1" to Example().apply {
//                        value = "Example Value 1"
//                    },
//                    "Example2" to Example().apply {
//                        value = "Example Value 2"
//                    }
//                )
//            })
//        }
//    }
//
//    "test xml content object" {
//        val content = buildContentObject(SimpleBody::class) {
//            mediaType(ContentType.Application.Xml)
//        }
//        content shouldBeContent {
//            addMediaType(ContentType.Application.Xml.toString(), MediaType().apply {
//                schema = Schema<Any>().apply {
//                    type = "object"
//                    xml = XML().apply { name = "SimpleBody" }
//                    properties = mapOf(
//                        "someText" to Schema<Any>().apply {
//                            type = "string"
//                        }
//                    )
//                }
//            })
//        }
//    }
//
//    "test image content object" {
//        val content = buildContentObject(null) {
//            mediaType(ContentType.Image.SVG)
//            mediaType(ContentType.Image.PNG)
//            mediaType(ContentType.Image.JPEG)
//        }
//        content shouldBeContent {
//            addMediaType(ContentType.Image.SVG.toString(), MediaType())
//            addMediaType(ContentType.Image.PNG.toString(), MediaType())
//            addMediaType(ContentType.Image.JPEG.toString(), MediaType())
//        }
//    }
//
//    "test content object with custom (remote) json-schema" {
//        val content = buildCustomContentObject(obj("remote"))
//        content shouldBeContent {
//            addMediaType(ContentType.Application.Json.toString(), MediaType().apply {
//                schema = Schema<Any>().apply {
//                    type = "object"
//                    `$ref` = "/my/test/schema"
//                }
//            })
//        }
//    }
//
//    "test content array with custom (remote) json-schema" {
//        val content = buildCustomContentObject(array("remote"))
//        content shouldBeContent {
//            addMediaType(ContentType.Application.Json.toString(), MediaType().apply {
//                schema = Schema<Any>().apply {
//                    type = "array"
//                    items = Schema<Any>().apply {
//                        type = "object"
//                        `$ref` = "/my/test/schema"
//                    }
//                }
//            })
//        }
//    }
//
//    "test content object with custom (remote) json-schema and components-section enabled" {
//        val content = buildCustomContentObject(obj("remote"), ComponentsContext(true, mutableMapOf(), true, mutableMapOf(), false))
//        content shouldBeContent {
//            addMediaType(ContentType.Application.Json.toString(), MediaType().apply {
//                schema = Schema<Any>().apply {
//                    type = "object"
//                    `$ref` = "/my/test/schema"
//                }
//            })
//        }
//    }
//
//    "test content array with custom (remote) json-schema and components-section enabled" {
//        val content = buildCustomContentObject(array("remote"), ComponentsContext(true, mutableMapOf(), true, mutableMapOf(), false))
//        content shouldBeContent {
//            addMediaType(ContentType.Application.Json.toString(), MediaType().apply {
//                schema = Schema<Any>().apply {
//                    type = "array"
//                    items = Schema<Any>().apply {
//                        type = "object"
//                        `$ref` = "/my/test/schema"
//                    }
//                }
//            })
//        }
//    }
//
//    "test content object with custom json-schema" {
//        val content = buildCustomContentObject(obj("custom"))
//        content shouldBeContent {
//            addMediaType(ContentType.Application.Json.toString(), MediaType().apply {
//                schema = Schema<Any>().apply {
//                    type = "object"
//                    properties = mapOf(
//                        "someBoolean" to Schema<Any>().apply {
//                            type = "boolean"
//                        },
//                        "someText" to Schema<Any>().apply {
//                            type = "string"
//                        }
//                    )
//                }
//            })
//        }
//    }
//
//    "test content array with custom json-schema" {
//        val content = buildCustomContentObject(array("custom"))
//        content shouldBeContent {
//            addMediaType(ContentType.Application.Json.toString(), MediaType().apply {
//                schema = Schema<Any>().apply {
//                    type = "array"
//                    items = Schema<Any>().apply {
//                        type = "object"
//                        properties = mapOf(
//                            "someBoolean" to Schema<Any>().apply {
//                                type = "boolean"
//                            },
//                            "someText" to Schema<Any>().apply {
//                                type = "string"
//                            }
//                        )
//                    }
//                }
//            })
//        }
//    }
//
//    "test content object with custom json-schema and components-section enabled" {
//        val content = buildCustomContentObject(obj("custom"), ComponentsContext(true, mutableMapOf(), true, mutableMapOf(), false))
//        content shouldBeContent {
//            addMediaType(ContentType.Application.Json.toString(), MediaType().apply {
//                schema = Schema<Any>().apply {
//                    `$ref` = "#/components/schemas/custom"
//                }
//            })
//        }
//    }
//
//    "test content array with custom json-schema and components-section enabled" {
//        val content = buildCustomContentObject(array("custom"), ComponentsContext(true, mutableMapOf(), true, mutableMapOf(), false))
//        content shouldBeContent {
//            addMediaType(ContentType.Application.Json.toString(), MediaType().apply {
//                schema = Schema<Any>().apply {
//                    type = "array"
//                    items = Schema<Any>().apply {
//                        `$ref` = "#/components/schemas/custom"
//                    }
//                }
//            })
//        }
//    }
//
//    "test multipart content object" {
//        val content = buildMultipartContentObject {
//            description = "Test Description"
//            part<File>("myFile") {
//                mediaTypes = setOf(ContentType.Image.JPEG, ContentType.Image.PNG)
//            }
//            part<SimpleBody>("myData")
//        }
//        content shouldBeContent {
//            addMediaType(ContentType.MultiPart.FormData.toString(), MediaType().apply {
//                schema = Schema<Any>().apply {
//                    type = "object"
//                    properties = mapOf(
//                        "myFile" to Schema<Any>().apply {
//                            type = "string"
//                            format = "binary"
//                            xml = XML().apply { name = "File" }
//                        },
//                        "myData" to Schema<Any>().apply {
//                            type = "object"
//                            xml = XML().apply { name = "SimpleBody" }
//                            properties = mapOf(
//                                "someText" to Schema<Any>().apply {
//                                    type = "string"
//                                }
//                            )
//                        }
//                    )
//                }
//                addEncoding("myFile", Encoding().contentType("image/png, image/jpeg"))
//            })
//        }
//    }
//
//}) {
//
//    companion object {
//
//        private fun pluginConfig() = SwaggerUIPluginConfig().apply {
//            schemas {
//                remote("remote", "/my/test/schema")
//                json("custom") {
//                    """
//                        {
//                            "type": "object",
//                            "properties": {
//                                "someBoolean": {
//                                    "type": "boolean"
//                                },
//                                "someText": {
//                                    "type": "string"
//                                }
//                            }
//                        }
//                    """.trimIndent()
//                }
//            }
//        }
//
//        private fun buildContentObject(schema: KClass<*>?, builder: OpenApiSimpleBody.() -> Unit): Content {
//            return buildContentObject(ComponentsContext.NOOP, schema, builder)
//        }
//
//        private fun buildContentObject(
//            componentCtx: ComponentsContext,
//            type: KClass<*>?,
//            builder: OpenApiSimpleBody.() -> Unit
//        ): Content {
//            return getOApiContentBuilder().build(OpenApiSimpleBody(type?.java).apply(builder), componentCtx, pluginConfig())
//        }
//
//        private fun buildCustomContentObject(schema: CustomSchemaRef, componentCtx: ComponentsContext = ComponentsContext.NOOP): Content {
//            return getOApiContentBuilder().build(OpenApiSimpleBody(null)
//                .apply { customSchema = schema }, componentCtx, pluginConfig()
//            )
//        }
//
//        private fun buildMultipartContentObject(builder: OpenApiMultipartBody.() -> Unit): Content {
//            return getOApiContentBuilder().build(
//                OpenApiMultipartBody()
//                    .apply(builder), ComponentsContext.NOOP, pluginConfig()
//            )
//        }
//
//        private data class SimpleBody(
//            val someText: String
//        )
//
//    }
//
//}