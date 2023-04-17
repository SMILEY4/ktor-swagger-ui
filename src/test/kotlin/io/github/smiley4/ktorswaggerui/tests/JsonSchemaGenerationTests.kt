package io.github.smiley4.ktorswaggerui.tests

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.core.type.TypeReference
import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.specbuilder.ComponentsContext
import io.kotest.core.spec.style.StringSpec
import io.swagger.v3.oas.models.media.Schema

class JsonSchemaGenerationTests : StringSpec({

    "generate schema for a simple enum" {
        getOApiSchemaBuilder().build(SimpleEnum::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "string"
            enum = SimpleEnum.values().map { it.name }
        }
    }

    "generate schema for maps" {
        getOApiSchemaBuilder().build(DataClassWithMaps::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "object"
            properties = mapOf(
                "mapStringValues" to Schema<Any>().apply {
                    type = "object"
                    additionalProperties = Schema<Any>().apply {
                        type = "string"
                    }
                },
                "mapLongValues" to Schema<Any>().apply {
                    type = "object"
                    additionalProperties = Schema<Any>().apply {
                        type = "integer"
                        format = "int64"
                    }
                },
            )
        }
    }

    "generate schema for a list of simple classes" {
        getOApiSchemaBuilder().build(Array<SimpleDataClass>::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "array"
            items = Schema<Any>().apply {
                type = "object"
                properties = mapOf(
                    "text" to Schema<Any>().apply {
                        type = "string"
                    },
                    "value" to Schema<Any>().apply {
                        type = "number"
                        format = "float"
                    }
                )
            }
        }
    }

    "generate schema for a simple class" {
        getOApiSchemaBuilder().build(SimpleDataClass::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "object"
            properties = mapOf(
                "text" to Schema<Any>().apply {
                    type = "string"
                },
                "value" to Schema<Any>().apply {
                    type = "number"
                    format = "float"
                }
            )
        }
    }

    "generate schema for a another class" {
        getOApiSchemaBuilder().build(AnotherDataClass::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "object"
            properties = mapOf(
                "primitiveValue" to Schema<Any>().apply {
                    type = "integer"
                    format = "int32"
                },
                "primitiveList" to Schema<Any>().apply {
                    type = "array"
                    items = Schema<Any>().apply {
                        type = "integer"
                        format = "int32"
                    }
                },
                "nestedClass" to Schema<Any>().apply {
                    type = "object"
                    properties = mapOf(
                        "text" to Schema<Any>().apply {
                            type = "string"
                        },
                        "value" to Schema<Any>().apply {
                            type = "number"
                            format = "float"
                        }
                    )
                },
                "nestedClassList" to Schema<Any>().apply {
                    type = "array"
                    items = Schema<Any>().apply {
                        type = "object"
                        properties = mapOf(
                            "text" to Schema<Any>().apply {
                                type = "string"
                            },
                            "value" to Schema<Any>().apply {
                                type = "number"
                                format = "float"
                            }
                        )
                    }
                },
            )
        }
    }

    "generate schema for a class with inheritance" {
        getOApiSchemaBuilder().build(SubClassA::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "object"
            properties = mapOf(
                "superField" to Schema<Any>().apply {
                    type = "string"
                },
                "subFieldA" to Schema<Any>().apply {
                    type = "integer"
                    format = "int32"
                },
                "_type" to Schema<Any>().apply {
                    setConst("io.github.smiley4.ktorswaggerui.tests.JsonSchemaGenerationTests\$Companion\$SubClassA")
                },
            )
            required = listOf("_type")
        }
    }

    "generate schema for a class with sub-classes" {
        getOApiSchemaBuilder().build(Superclass::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            anyOf = listOf(
                Schema<Any>().apply {
                    type = "object"
                    properties = mapOf(
                        "superField" to Schema<Any>().apply {
                            type = "string"
                        },
                        "subFieldA" to Schema<Any>().apply {
                            type = "integer"
                            format = "int32"
                        },
                        "_type" to Schema<Any>().apply {
                            setConst("io.github.smiley4.ktorswaggerui.tests.JsonSchemaGenerationTests\$Companion\$SubClassA")
                        }
                    )
                    required = listOf("_type")
                },
                Schema<Any>().apply {
                    type = "object"
                    properties = mapOf(
                        "superField" to Schema<Any>().apply {
                            type = "string"
                        },
                        "subFieldB" to Schema<Any>().apply {
                            type = "boolean"
                        },
                        "_type" to Schema<Any>().apply {
                            setConst("io.github.smiley4.ktorswaggerui.tests.JsonSchemaGenerationTests\$Companion\$SubClassB")
                        }
                    )
                    required = listOf("_type")
                }
            )
        }
    }

    "generate schema for a class with nested generic type" {
        getOApiSchemaBuilder().build(
            WrapperForClassWithGenerics::class.java,
            ComponentsContext.NOOP,
            SwaggerUIPluginConfig()
        ) shouldBeSchema {
            type = "object"
            properties = mapOf(
                "genericClass" to Schema<Any>().apply {
                    type = "object"
                    properties = mapOf(
                        "genericField" to Schema<Any>().apply {
                            type = "string"
                        },
                        "genericList" to Schema<Any>().apply {
                            type = "array"
                            items = Schema<Any>().apply {
                                type = "string"
                            }
                        }
                    )
                }
            )
        }
    }

    "generate schema for a class with generic types" {
        getOApiSchemaBuilder().build(
            getType<ClassWithGenerics<SimpleDataClass>>(),
            ComponentsContext.NOOP,
            SwaggerUIPluginConfig()
        ) shouldBeSchema {
            type = "object"
            properties = mapOf(
                "genericField" to Schema<Any>().apply {
                    type = "object"
                    properties = mapOf(
                        "text" to Schema<Any>().apply {
                            type = "string"
                        },
                        "value" to Schema<Any>().apply {
                            type = "number"
                            format = "float"
                        }
                    )
                },
                "genericList" to Schema<Any>().apply {
                    type = "array"
                    items = Schema<Any>().apply {
                        type = "object"
                        properties = mapOf(
                            "text" to Schema<Any>().apply {
                                type = "string"
                            },
                            "value" to Schema<Any>().apply {
                                type = "number"
                                format = "float"
                            }
                        )
                    }
                }
            )
        }
    }

}) {
    companion object {

        inline fun <reified T> getType() = object : TypeReference<T>() {}.type

        enum class SimpleEnum {
            RED, GREEN, BLUE
        }

        data class SimpleDataClass(
            val text: String,
            val value: Float
        )

        data class DataClassWithMaps(
            val mapStringValues: Map<String, String>,
            val mapLongValues: Map<String, Long>
        )

        data class AnotherDataClass(
            val primitiveValue: Int,
            val primitiveList: List<Int>,
            private val privateValue: String,
            val nestedClass: SimpleDataClass,
            val nestedClassList: List<SimpleDataClass>
        )

        @JsonTypeInfo(
            use = JsonTypeInfo.Id.CLASS,
            include = JsonTypeInfo.As.PROPERTY,
            property = "_type",
        )
        @JsonSubTypes(
            JsonSubTypes.Type(value = SubClassA::class),
            JsonSubTypes.Type(value = SubClassB::class),
        )
        abstract class Superclass(
            val superField: String,
        )

        class SubClassA(
            superField: String,
            val subFieldA: Int
        ) : Superclass(superField)

        class SubClassB(
            superField: String,
            val subFieldB: Boolean
        ) : Superclass(superField)


        data class ClassWithNestedAbstractClass(
            val nestedClass: Superclass,
            val someField: String
        )

        class ClassWithGenerics<T>(
            val genericField: T,
            val genericList: List<T>
        )

        class WrapperForClassWithGenerics(
            val genericClass: ClassWithGenerics<String>
        )

    }
}
