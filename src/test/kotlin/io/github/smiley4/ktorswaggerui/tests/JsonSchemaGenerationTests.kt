package io.github.smiley4.ktorswaggerui.tests

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.github.smiley4.ktorswaggerui.apispec.OApiSchemaGenerator
import io.kotest.core.spec.style.StringSpec
import io.swagger.v3.oas.models.media.Schema

class JsonSchemaGenerationTests : StringSpec({

    "generate schema for a simple enum" {
        OApiSchemaGenerator().generate(SimpleEnum::class) shouldBeSchema {
            type = "string"
            enum = SimpleEnum.values().map { it.name }
        }
    }

    "generate schema for a list of simple classes" {
        OApiSchemaGenerator().generate(Array<SimpleDataClass>::class) shouldBeSchema {
            type = "array"
            items = Schema<Any>().apply {
                type = "object"
                properties = mapOf(
                    "text" to Schema<Any>().apply {
                        type = "string"
                    },
                    "value" to Schema<Any>().apply {
                        type = "number"
                    }
                )
            }
        }
    }

    "generate schema for a simple class" {
        OApiSchemaGenerator().generate(SimpleDataClass::class) shouldBeSchema {
            type = "object"
            properties = mapOf(
                "text" to Schema<Any>().apply {
                    type = "string"
                },
                "value" to Schema<Any>().apply {
                    type = "number"
                }
            )
        }
    }

    "generate schema for a another class" {
        OApiSchemaGenerator().generate(AnotherDataClass::class) shouldBeSchema {
            type = "object"
            properties = mapOf(
                "primitiveValue" to Schema<Any>().apply {
                    type = "integer"
                },
                "primitiveList" to Schema<Any>().apply {
                    type = "array"
                    items = Schema<Any>().apply {
                        type = "integer"
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
                            }
                        )
                    }
                },
            )
        }
    }

    "generate schema for a class with inheritance" {
        OApiSchemaGenerator().generate(SubClassA::class) shouldBeSchema {
            allOf = listOf(
                Schema<Any>().apply {
                    type = "object"
                    properties = mapOf(
                        "superField" to Schema<Any>().apply {
                            type = "string"
                        },
                        "subFieldA" to Schema<Any>().apply {
                            type = "integer"
                        }
                    )
                },
                Schema<Any>().apply {
                    type = "object"
                    properties = mapOf(
                        "_type" to Schema<Any>().apply {
                            setConst("io.github.smiley4.ktorswaggerui.tests.JsonSchemaGenerationTests\$Companion\$SubClassA")
                        },
                    )
                    required = listOf("_type")
                },
            )
        }
    }

    "generate schema for a class with sub-classes" {
        OApiSchemaGenerator().generate(Superclass::class) shouldBeSchema {
            anyOf = listOf(
                Schema<Any>().apply {
                    allOf = listOf(
                        Schema<Any>().apply {
                            type = "object"
                            properties = mapOf(
                                "superField" to Schema<Any>().apply {
                                    type = "string"
                                },
                                "subFieldA" to Schema<Any>().apply {
                                    type = "integer"
                                }
                            )
                        },
                        Schema<Any>().apply {
                            type = "object"
                            properties = mapOf(
                                "_type" to Schema<Any>().apply {
                                    setConst("io.github.smiley4.ktorswaggerui.tests.JsonSchemaGenerationTests\$Companion\$SubClassA")
                                },
                            )
                            required = listOf("_type")
                        },
                    )
                },
                Schema<Any>().apply {
                    allOf = listOf(
                        Schema<Any>().apply {
                            type = "object"
                            properties = mapOf(
                                "superField" to Schema<Any>().apply {
                                    type = "string"
                                },
                                "subFieldB" to Schema<Any>().apply {
                                    type = "boolean"
                                }
                            )
                        },
                        Schema<Any>().apply {
                            type = "object"
                            properties = mapOf(
                                "_type" to Schema<Any>().apply {
                                    setConst("io.github.smiley4.ktorswaggerui.tests.JsonSchemaGenerationTests\$Companion\$SubClassB")
                                },
                            )
                            required = listOf("_type")
                        },
                    )
                },
            )
        }
    }

    "generate schema for a class with generic types" {
        OApiSchemaGenerator().generate(WrapperForClassWithGenerics::class) shouldBeSchema {
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

}) {
    companion object {


        enum class SimpleEnum {
            RED, GREEN, BLUE
        }

        data class SimpleDataClass(
            val text: String,
            val value: Float
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
