package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.swagger.v3.oas.models.media.Schema
import java.lang.reflect.Type
import java.math.BigDecimal
import kotlin.reflect.KClass

/**
 * Builder for an OpenAPI Schema Object
 */
class OApiSchemaBuilder {

    private val jsonSchemaBuilder = OApiJsonSchemaBuilder()


    fun build(type: Type, components: ComponentsContext, config: SwaggerUIPluginConfig): Schema<Any> {
        return Schema<Any>().apply {
            when (type) {
                Byte::class.java -> {
                    this.type = "integer"
                    minimum = BigDecimal.valueOf(Byte.MIN_VALUE.toLong())
                    maximum = BigDecimal.valueOf(Byte.MAX_VALUE.toLong())
                }
                Short::class.java -> {
                    this.type = "integer"
                    minimum = BigDecimal.valueOf(Short.MIN_VALUE.toLong())
                    maximum = BigDecimal.valueOf(Short.MAX_VALUE.toLong())
                }
                Int::class.java -> {
                    this.type = "integer"
                    format = "int32"
                }
                Long::class.java -> {
                    this.type = "integer"
                    format = "int64"
                }
                UByte::class.java -> {
                    this.type = "integer"
                    minimum = BigDecimal.valueOf(UByte.MIN_VALUE.toLong())
                    maximum = BigDecimal.valueOf(UByte.MAX_VALUE.toLong())
                }
                UShort::class.java -> {
                    this.type = "integer"
                    minimum = BigDecimal.valueOf(UShort.MIN_VALUE.toLong())
                    maximum = BigDecimal.valueOf(UShort.MAX_VALUE.toLong())
                }
                UInt::class.java -> {
                    this.type = "integer"
                    minimum = BigDecimal.valueOf(UInt.MIN_VALUE.toLong())
                    maximum = BigDecimal.valueOf(UInt.MAX_VALUE.toLong())
                }
                ULong::class.java -> {
                    this.type = "integer"
                    minimum = BigDecimal.valueOf(ULong.MIN_VALUE.toLong())
                }
                Float::class.java -> {
                    this.type = "number"
                    format = "float"
                }
                Double::class.java -> {
                    this.type = "number"
                    format = "double"
                }
                Boolean::class.java -> {
                    this.type = "boolean"
                }
                Char::class.java -> {
                    this.type = "string"
                    minLength = 1
                    maxLength = 1
                }
                String::class.java -> {
                    this.type = "string"
                }
                Array<Byte>::class.java, ByteArray::class.java -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "integer"
                        minimum = BigDecimal.valueOf(Byte.MIN_VALUE.toLong())
                        maximum = BigDecimal.valueOf(Byte.MAX_VALUE.toLong())
                    }
                }
                Array<Short>::class.java, ShortArray::class.java -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "integer"
                        minimum = BigDecimal.valueOf(Short.MIN_VALUE.toLong())
                        maximum = BigDecimal.valueOf(Short.MAX_VALUE.toLong())
                    }
                }
                Array<Int>::class.java, IntArray::class.java -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "integer"
                        format = "int32"
                    }
                }
                Array<Long>::class.java, LongArray::class.java -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "integer"
                        format = "int64"
                    }
                }
                Array<UByte>::class.java -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "integer"
                        minimum = BigDecimal.valueOf(UByte.MIN_VALUE.toLong())
                        maximum = BigDecimal.valueOf(UByte.MAX_VALUE.toLong())
                    }
                }
                Array<UShort>::class.java -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "integer"
                        minimum = BigDecimal.valueOf(UShort.MIN_VALUE.toLong())
                        maximum = BigDecimal.valueOf(UShort.MAX_VALUE.toLong())
                    }
                }
                Array<UInt>::class.java -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "integer"
                        minimum = BigDecimal.valueOf(UInt.MIN_VALUE.toLong())
                        maximum = BigDecimal.valueOf(UInt.MAX_VALUE.toLong())
                    }
                }
                Array<ULong>::class.java -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "integer"
                        minimum = BigDecimal.valueOf(ULong.MIN_VALUE.toLong())
                    }
                }
                Array<Float>::class.java, FloatArray::class.java -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "number"
                        format = "float"
                    }
                }
                Array<Double>::class.java, DoubleArray::class.java -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "number"
                        format = "double"
                    }
                }

                Array<Boolean>::class.java, BooleanArray::class.java -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "boolean"
                    }
                }
                Array<Char>::class.java -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "string"
                        minLength = 1
                        maxLength = 1
                    }
                }
                Array<String>::class.java -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "string"
                    }
                }
                else -> {
                    return jsonSchemaBuilder.build(type, components, config)
                }
            }
        }
    }

}
