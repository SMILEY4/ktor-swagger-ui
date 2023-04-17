package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.specbuilder.ComponentsContext
import io.kotest.core.spec.style.StringSpec
import io.swagger.v3.oas.models.media.Schema
import java.math.BigDecimal

class PrimitiveArraysSchemaGenerationTests : StringSpec({

    "generate schema for byte-array" {
        getOApiSchemaBuilder().build(Array<Byte>::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                minimum = BigDecimal.valueOf(-128)
                maximum = BigDecimal.valueOf(127)
            }
        }
        getOApiSchemaBuilder().build(ByteArray::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                minimum = BigDecimal.valueOf(-128)
                maximum = BigDecimal.valueOf(127)
            }
        }
    }

    "generate schema for unsigned byte" {
        getOApiSchemaBuilder().build(Array<UByte>::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                minimum = BigDecimal.valueOf(0)
                maximum = BigDecimal.valueOf(255)
            }
        }
    }

    "generate schema for short-array" {
        getOApiSchemaBuilder().build(Array<Short>::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                minimum = BigDecimal.valueOf(-32768)
                maximum = BigDecimal.valueOf(32767)
            }
        }
        getOApiSchemaBuilder().build(ShortArray::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                minimum = BigDecimal.valueOf(-32768)
                maximum = BigDecimal.valueOf(32767)
            }
        }
    }

    "generate schema for unsigned short" {
        getOApiSchemaBuilder().build(Array<UShort>::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                minimum = BigDecimal.valueOf(0)
                maximum = BigDecimal.valueOf(65535)
            }
        }
    }

    "generate schema for integer-array" {
        getOApiSchemaBuilder().build(Array<Int>::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                format = "int32"
            }
        }
        getOApiSchemaBuilder().build(IntArray::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                format = "int32"
            }
        }
    }

    "generate schema for unsigned integer" {
        getOApiSchemaBuilder().build(Array<UInt>::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                minimum = BigDecimal.valueOf(0)
                maximum = BigDecimal.valueOf(4294967295)
            }
        }
    }

    "generate schema for long-array" {
        getOApiSchemaBuilder().build(Array<Long>::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                format = "int64"
            }
        }
        getOApiSchemaBuilder().build(LongArray::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                format = "int64"
            }
        }
    }

    "generate schema for unsigned long" {
        getOApiSchemaBuilder().build(Array<ULong>::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                minimum = BigDecimal.valueOf(0)
            }
        }
    }

    "generate schema for float-array" {
        getOApiSchemaBuilder().build(Array<Float>::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "number"
                format = "float"
            }
        }
        getOApiSchemaBuilder().build(FloatArray::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "number"
                format = "float"
            }
        }
    }

    "generate schema for double-array" {
        getOApiSchemaBuilder().build(Array<Double>::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "number"
                format = "double"
            }
        }
        getOApiSchemaBuilder().build(DoubleArray::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "number"
                format = "double"
            }
        }
    }

    "generate schema for character-array" {
        getOApiSchemaBuilder().build(Array<Char>::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "string"
                minLength = 1
                maxLength = 1
            }
        }
    }

    "generate schema for string-array" {
        getOApiSchemaBuilder().build(Array<String>::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "string"
            }
        }
    }

    "generate schema for boolean-array" {
        getOApiSchemaBuilder().build(Array<Boolean>::class.java, ComponentsContext.NOOP, SwaggerUIPluginConfig()) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "boolean"
            }
        }
    }

})