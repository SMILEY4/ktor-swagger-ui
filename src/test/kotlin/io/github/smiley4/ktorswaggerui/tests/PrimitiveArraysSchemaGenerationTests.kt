package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.specbuilder.ComponentsContext
import io.github.smiley4.ktorswaggerui.specbuilder.OApiSchemaGenerator
import io.kotest.core.spec.style.StringSpec
import io.swagger.v3.oas.models.media.Schema
import java.math.BigDecimal

class PrimitiveArraysSchemaGenerationTests : StringSpec({

    "generate schema for byte-array" {
        OApiSchemaGenerator().generate(Array<Byte>::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                minimum = BigDecimal.valueOf(-128)
                maximum = BigDecimal.valueOf(127)
            }
        }
        OApiSchemaGenerator().generate(ByteArray::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                minimum = BigDecimal.valueOf(-128)
                maximum = BigDecimal.valueOf(127)
            }
        }
    }

    "generate schema for unsigned byte" {
        OApiSchemaGenerator().generate(Array<UByte>::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                minimum = BigDecimal.valueOf(0)
                maximum = BigDecimal.valueOf(255)
            }
        }
    }

    "generate schema for short-array" {
        OApiSchemaGenerator().generate(Array<Short>::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                minimum = BigDecimal.valueOf(-32768)
                maximum = BigDecimal.valueOf(32767)
            }
        }
        OApiSchemaGenerator().generate(ShortArray::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                minimum = BigDecimal.valueOf(-32768)
                maximum = BigDecimal.valueOf(32767)
            }
        }
    }

    "generate schema for unsigned short" {
        OApiSchemaGenerator().generate(Array<UShort>::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                minimum = BigDecimal.valueOf(0)
                maximum = BigDecimal.valueOf(65535)
            }
        }
    }

    "generate schema for integer-array" {
        OApiSchemaGenerator().generate(Array<Int>::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                format = "int32"
            }
        }
        OApiSchemaGenerator().generate(IntArray::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                format = "int32"
            }
        }
    }

    "generate schema for unsigned integer" {
        OApiSchemaGenerator().generate(Array<UInt>::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                minimum = BigDecimal.valueOf(0)
                maximum = BigDecimal.valueOf(4294967295)
            }
        }
    }

    "generate schema for long-array" {
        OApiSchemaGenerator().generate(Array<Long>::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                format = "int64"
            }
        }
        OApiSchemaGenerator().generate(LongArray::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                format = "int64"
            }
        }
    }

    "generate schema for unsigned long" {
        OApiSchemaGenerator().generate(Array<ULong>::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "integer"
                minimum = BigDecimal.valueOf(0)
            }
        }
    }

    "generate schema for float-array" {
        OApiSchemaGenerator().generate(Array<Float>::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "number"
                format = "float"
            }
        }
        OApiSchemaGenerator().generate(FloatArray::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "number"
                format = "float"
            }
        }
    }

    "generate schema for double-array" {
        OApiSchemaGenerator().generate(Array<Double>::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "number"
                format = "double"
            }
        }
        OApiSchemaGenerator().generate(DoubleArray::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "number"
                format = "double"
            }
        }
    }

    "generate schema for character-array" {
        OApiSchemaGenerator().generate(Array<Char>::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "string"
                minLength = 1
                maxLength = 1
            }
        }
    }

    "generate schema for string-array" {
        OApiSchemaGenerator().generate(Array<String>::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "string"
            }
        }
    }

    "generate schema for boolean-array" {
        OApiSchemaGenerator().generate(Array<Boolean>::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "array"
            items = Schema<String>().apply {
                type = "boolean"
            }
        }
    }

})