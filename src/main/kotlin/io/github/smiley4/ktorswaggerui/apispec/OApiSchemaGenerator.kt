package io.github.smiley4.ktorswaggerui.apispec

import io.github.smiley4.ktorswaggerui.routing.SchemaRef
import io.swagger.v3.oas.models.media.Schema
import java.math.BigDecimal

/**
 * Generator for an OpenAPI Schema Object
 */
class OApiSchemaGenerator {

    /**
     * Generate the Content Object from the given config
     */
    fun generate(schema: Class<*>): Schema<Any> {
        return Schema<Any>().apply {
            when (schema) {
                Byte::class.java -> {
                    type = "integer"
                    minimum = BigDecimal.valueOf(Byte.MIN_VALUE.toLong())
                    maximum = BigDecimal.valueOf(Byte.MAX_VALUE.toLong())
                }
                Short::class.java -> {
                    type = "integer"
                    minimum = BigDecimal.valueOf(Short.MIN_VALUE.toLong())
                    maximum = BigDecimal.valueOf(Short.MAX_VALUE.toLong())
                }
                Int::class.java -> {
                    type = "integer"
                    format = "int32"
                }
                Long::class.java -> {
                    type = "integer"
                    format = "int64"
                }
                UByte::class.java -> {
                    type = "integer"
                    minimum = BigDecimal.valueOf(UByte.MIN_VALUE.toLong())
                    maximum = BigDecimal.valueOf(UByte.MAX_VALUE.toLong())
                }
                UShort::class.java -> {
                    type = "integer"
                    minimum = BigDecimal.valueOf(UShort.MIN_VALUE.toLong())
                    maximum = BigDecimal.valueOf(UShort.MAX_VALUE.toLong())
                }
                UInt::class.java -> {
                    type = "integer"
                    minimum = BigDecimal.valueOf(UInt.MIN_VALUE.toLong())
                    maximum = BigDecimal.valueOf(UInt.MAX_VALUE.toLong())
                }
                ULong::class.java -> {
                    type = "integer"
                    minimum = BigDecimal.valueOf(ULong.MIN_VALUE.toLong())
                    maximum = BigDecimal.valueOf(ULong.MAX_VALUE.toLong())
                }
                Float::class.java -> {
                    type = "number"
                    format = "float"
                }
                Double::class.java -> {
                    type = "number"
                    format = "double"
                }
                Boolean::class.java -> {
                    type = "boolean"
                }
                Char::class.java -> {
                    type = "string"
                    minLength = 1
                    maxLength = 1
                }
                String::class.java -> {
                    type = "string"
                }

                Array<Byte>::class.java, ByteArray::class.java -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "integer"
                        minimum = BigDecimal.valueOf(Byte.MIN_VALUE.toLong())
                        maximum = BigDecimal.valueOf(Byte.MAX_VALUE.toLong())
                    }
                }
                Array<Short>::class.java, ShortArray::class.java -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "integer"
                        minimum = BigDecimal.valueOf(Short.MIN_VALUE.toLong())
                        maximum = BigDecimal.valueOf(Short.MAX_VALUE.toLong())
                    }
                }
                Array<Int>::class.java, IntArray::class.java -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "integer"
                        format = "int32"
                    }
                }
                Array<Long>::class.java, LongArray::class.java -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "integer"
                        format = "int64"
                    }
                }
                Array<UByte>::class.java -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "integer"
                        minimum = BigDecimal.valueOf(UByte.MIN_VALUE.toLong())
                        maximum = BigDecimal.valueOf(UByte.MAX_VALUE.toLong())
                    }
                }
                Array<UShort>::class.java -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "integer"
                        minimum = BigDecimal.valueOf(UShort.MIN_VALUE.toLong())
                        maximum = BigDecimal.valueOf(UShort.MAX_VALUE.toLong())
                    }
                }
                Array<UInt>::class.java -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "integer"
                        minimum = BigDecimal.valueOf(UInt.MIN_VALUE.toLong())
                        maximum = BigDecimal.valueOf(UInt.MAX_VALUE.toLong())
                    }
                }
                Array<ULong>::class.java -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "integer"
                        minimum = BigDecimal.valueOf(ULong.MIN_VALUE.toLong())
                        maximum = BigDecimal.valueOf(ULong.MAX_VALUE.toLong())
                    }
                }
                Array<Float>::class.java, FloatArray::class.java -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "number"
                        format = "float"
                    }
                }
                Array<Double>::class.java, DoubleArray::class.java -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "number"
                        format = "double"
                    }
                }

                Array<Boolean>::class.java, BooleanArray::class.java -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "boolean"
                    }
                }
                Array<Char>::class.java -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "string"
                        minLength = 1
                        maxLength = 1
                    }
                }
                Array<String>::class.java -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "string"
                    }
                }
                else -> {
                    if (schema.isArray) {
                        type = "array"
                        items = Schema<String>().apply {
                            type = "object"
                            `$ref` = SchemaRef.refOfClass(schema.componentType)
                        }
                    } else if (schema.isEnum) {
                        type = "string"
                        enum = schema.enumConstants.map { it.toString() }
                    } else {
                        type = "object"
                        `$ref` = SchemaRef.refOfClass(schema)
                    }
                }
            }
        }
    }

}