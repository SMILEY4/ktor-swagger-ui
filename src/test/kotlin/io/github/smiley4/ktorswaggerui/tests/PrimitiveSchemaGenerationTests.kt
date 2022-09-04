package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.specbuilder.ComponentsContext
import io.github.smiley4.ktorswaggerui.specbuilder.OApiSchemaGenerator
import io.kotest.core.spec.style.StringSpec
import java.math.BigDecimal

class PrimitiveSchemaGenerationTests : StringSpec({

    "generate schema for byte" {
        OApiSchemaGenerator().generate(Byte::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "integer"
            minimum = BigDecimal.valueOf(-128)
            maximum = BigDecimal.valueOf(127)
        }
    }

    "generate schema for unsigned byte" {
        OApiSchemaGenerator().generate(UByte::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "integer"
            minimum = BigDecimal.valueOf(0)
            maximum = BigDecimal.valueOf(255)
        }
    }

    "generate schema for short" {
        OApiSchemaGenerator().generate(Short::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "integer"
            minimum = BigDecimal.valueOf(-32768)
            maximum = BigDecimal.valueOf(32767)
        }
    }

    "generate schema for unsigned short" {
        OApiSchemaGenerator().generate(UShort::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "integer"
            minimum = BigDecimal.valueOf(0)
            maximum = BigDecimal.valueOf(65535)
        }
    }

    "generate schema for integer" {
        OApiSchemaGenerator().generate(Int::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "integer"
            format = "int32"
        }
    }

    "generate schema for unsigned integer" {
        OApiSchemaGenerator().generate(UInt::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "integer"
            minimum = BigDecimal.valueOf(0)
            maximum = BigDecimal.valueOf(4294967295)
        }
    }

    "generate schema for long" {
        OApiSchemaGenerator().generate(Long::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "integer"
            format = "int64"
        }
    }

    "generate schema for unsigned long" {
        OApiSchemaGenerator().generate(ULong::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "integer"
            minimum = BigDecimal.valueOf(0)
        }
    }

    "generate schema for float" {
        OApiSchemaGenerator().generate(Float::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "number"
            format = "float"
        }
    }

    "generate schema for double" {
        OApiSchemaGenerator().generate(Double::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "number"
            format = "double"
        }
    }

    "generate schema for character" {
        OApiSchemaGenerator().generate(Char::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "string"
            minLength = 1
            maxLength = 1
        }
    }

    "generate schema for string" {
        OApiSchemaGenerator().generate(String::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "string"
        }
    }

    "generate schema for boolean" {
        OApiSchemaGenerator().generate(Boolean::class, ComponentsContext.NOOP) shouldBeSchema {
            type = "boolean"
        }
    }

})