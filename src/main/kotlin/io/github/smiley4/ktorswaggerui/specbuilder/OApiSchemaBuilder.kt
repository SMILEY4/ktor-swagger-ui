package io.github.smiley4.ktorswaggerui.specbuilder

import io.swagger.v3.oas.models.media.Schema
import java.math.BigDecimal
import kotlin.reflect.KClass

/**
 * Builder for an OpenAPI Schema Object
 */
class OApiSchemaBuilder(
    private val jsonSchemaBuilder: OApiJsonSchemaBuilder
) {

    fun build(type: KClass<*>, components: ComponentsContext): Schema<Any> {
        return Schema<Any>().apply {
            when (type) {
                Byte::class -> {
                    this.type = "integer"
                    minimum = BigDecimal.valueOf(Byte.MIN_VALUE.toLong())
                    maximum = BigDecimal.valueOf(Byte.MAX_VALUE.toLong())
                }
                Short::class -> {
                    this.type = "integer"
                    minimum = BigDecimal.valueOf(Short.MIN_VALUE.toLong())
                    maximum = BigDecimal.valueOf(Short.MAX_VALUE.toLong())
                }
                Int::class -> {
                    this.type = "integer"
                    format = "int32"
                }
                Long::class -> {
                    this.type = "integer"
                    format = "int64"
                }
                UByte::class -> {
                    this.type = "integer"
                    minimum = BigDecimal.valueOf(UByte.MIN_VALUE.toLong())
                    maximum = BigDecimal.valueOf(UByte.MAX_VALUE.toLong())
                }
                UShort::class -> {
                    this.type = "integer"
                    minimum = BigDecimal.valueOf(UShort.MIN_VALUE.toLong())
                    maximum = BigDecimal.valueOf(UShort.MAX_VALUE.toLong())
                }
                UInt::class -> {
                    this.type = "integer"
                    minimum = BigDecimal.valueOf(UInt.MIN_VALUE.toLong())
                    maximum = BigDecimal.valueOf(UInt.MAX_VALUE.toLong())
                }
                ULong::class -> {
                    this.type = "integer"
                    minimum = BigDecimal.valueOf(ULong.MIN_VALUE.toLong())
                }
                Float::class -> {
                    this.type = "number"
                    format = "float"
                }
                Double::class -> {
                    this.type = "number"
                    format = "double"
                }
                Boolean::class -> {
                    this.type = "boolean"
                }
                Char::class -> {
                    this.type = "string"
                    minLength = 1
                    maxLength = 1
                }
                String::class -> {
                    this.type = "string"
                }

                Array<Byte>::class, ByteArray::class -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "integer"
                        minimum = BigDecimal.valueOf(Byte.MIN_VALUE.toLong())
                        maximum = BigDecimal.valueOf(Byte.MAX_VALUE.toLong())
                    }
                }
                Array<Short>::class, ShortArray::class -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "integer"
                        minimum = BigDecimal.valueOf(Short.MIN_VALUE.toLong())
                        maximum = BigDecimal.valueOf(Short.MAX_VALUE.toLong())
                    }
                }
                Array<Int>::class, IntArray::class -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "integer"
                        format = "int32"
                    }
                }
                Array<Long>::class, LongArray::class -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "integer"
                        format = "int64"
                    }
                }
                Array<UByte>::class -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "integer"
                        minimum = BigDecimal.valueOf(UByte.MIN_VALUE.toLong())
                        maximum = BigDecimal.valueOf(UByte.MAX_VALUE.toLong())
                    }
                }
                Array<UShort>::class -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "integer"
                        minimum = BigDecimal.valueOf(UShort.MIN_VALUE.toLong())
                        maximum = BigDecimal.valueOf(UShort.MAX_VALUE.toLong())
                    }
                }
                Array<UInt>::class -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "integer"
                        minimum = BigDecimal.valueOf(UInt.MIN_VALUE.toLong())
                        maximum = BigDecimal.valueOf(UInt.MAX_VALUE.toLong())
                    }
                }
                Array<ULong>::class -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "integer"
                        minimum = BigDecimal.valueOf(ULong.MIN_VALUE.toLong())
                    }
                }
                Array<Float>::class, FloatArray::class -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "number"
                        format = "float"
                    }
                }
                Array<Double>::class, DoubleArray::class -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "number"
                        format = "double"
                    }
                }

                Array<Boolean>::class, BooleanArray::class -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "boolean"
                    }
                }
                Array<Char>::class -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "string"
                        minLength = 1
                        maxLength = 1
                    }
                }
                Array<String>::class -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "string"
                    }
                }
                else -> {
                    return jsonSchemaBuilder.build(type, components)
                }
            }
        }
    }

}
