package io.github.smiley4.ktorswaggerui.specbuilder

import io.swagger.v3.oas.models.media.Schema
import java.math.BigDecimal
import kotlin.reflect.KClass

/**
 * Generator for an OpenAPI Schema Object
 */
class OApiSchemaGenerator {

    /**
     * Generate the Content Object from the given class/type
     */
    fun generate(schema: KClass<*>, componentCtx: ComponentsContext): Schema<Any> {
        return Schema<Any>().apply {
            when (schema) {
                Byte::class -> {
                    type = "integer"
                    minimum = BigDecimal.valueOf(Byte.MIN_VALUE.toLong())
                    maximum = BigDecimal.valueOf(Byte.MAX_VALUE.toLong())
                }
                Short::class -> {
                    type = "integer"
                    minimum = BigDecimal.valueOf(Short.MIN_VALUE.toLong())
                    maximum = BigDecimal.valueOf(Short.MAX_VALUE.toLong())
                }
                Int::class -> {
                    type = "integer"
                    format = "int32"
                }
                Long::class -> {
                    type = "integer"
                    format = "int64"
                }
                UByte::class -> {
                    type = "integer"
                    minimum = BigDecimal.valueOf(UByte.MIN_VALUE.toLong())
                    maximum = BigDecimal.valueOf(UByte.MAX_VALUE.toLong())
                }
                UShort::class -> {
                    type = "integer"
                    minimum = BigDecimal.valueOf(UShort.MIN_VALUE.toLong())
                    maximum = BigDecimal.valueOf(UShort.MAX_VALUE.toLong())
                }
                UInt::class -> {
                    type = "integer"
                    minimum = BigDecimal.valueOf(UInt.MIN_VALUE.toLong())
                    maximum = BigDecimal.valueOf(UInt.MAX_VALUE.toLong())
                }
                ULong::class -> {
                    type = "integer"
                    minimum = BigDecimal.valueOf(ULong.MIN_VALUE.toLong())
                }
                Float::class -> {
                    type = "number"
                    format = "float"
                }
                Double::class -> {
                    type = "number"
                    format = "double"
                }
                Boolean::class -> {
                    type = "boolean"
                }
                Char::class -> {
                    type = "string"
                    minLength = 1
                    maxLength = 1
                }
                String::class -> {
                    type = "string"
                }

                Array<Byte>::class, ByteArray::class -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "integer"
                        minimum = BigDecimal.valueOf(Byte.MIN_VALUE.toLong())
                        maximum = BigDecimal.valueOf(Byte.MAX_VALUE.toLong())
                    }
                }
                Array<Short>::class, ShortArray::class -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "integer"
                        minimum = BigDecimal.valueOf(Short.MIN_VALUE.toLong())
                        maximum = BigDecimal.valueOf(Short.MAX_VALUE.toLong())
                    }
                }
                Array<Int>::class, IntArray::class -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "integer"
                        format = "int32"
                    }
                }
                Array<Long>::class, LongArray::class -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "integer"
                        format = "int64"
                    }
                }
                Array<UByte>::class -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "integer"
                        minimum = BigDecimal.valueOf(UByte.MIN_VALUE.toLong())
                        maximum = BigDecimal.valueOf(UByte.MAX_VALUE.toLong())
                    }
                }
                Array<UShort>::class -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "integer"
                        minimum = BigDecimal.valueOf(UShort.MIN_VALUE.toLong())
                        maximum = BigDecimal.valueOf(UShort.MAX_VALUE.toLong())
                    }
                }
                Array<UInt>::class -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "integer"
                        minimum = BigDecimal.valueOf(UInt.MIN_VALUE.toLong())
                        maximum = BigDecimal.valueOf(UInt.MAX_VALUE.toLong())
                    }
                }
                Array<ULong>::class -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "integer"
                        minimum = BigDecimal.valueOf(ULong.MIN_VALUE.toLong())
                    }
                }
                Array<Float>::class, FloatArray::class -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "number"
                        format = "float"
                    }
                }
                Array<Double>::class, DoubleArray::class -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "number"
                        format = "double"
                    }
                }

                Array<Boolean>::class, BooleanArray::class -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "boolean"
                    }
                }
                Array<Char>::class -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "string"
                        minLength = 1
                        maxLength = 1
                    }
                }
                Array<String>::class -> {
                    type = "array"
                    items = Schema<String>().apply {
                        type = "string"
                    }
                }
                else -> {
                    return OApiJsonSchemaGenerator().generate(schema, componentCtx)
                }
            }
        }
    }


}