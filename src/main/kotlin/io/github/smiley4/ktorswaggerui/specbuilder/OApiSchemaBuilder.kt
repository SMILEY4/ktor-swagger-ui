package io.github.smiley4.ktorswaggerui.specbuilder

import com.fasterxml.jackson.core.type.TypeReference
import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.swagger.v3.oas.models.media.Schema
import java.io.File
import java.lang.reflect.Type
import java.math.BigDecimal

/**
 * Builder for an OpenAPI Schema Object
 */
class OApiSchemaBuilder {

    private companion object {

        private val BYTE: Type = (object : TypeReference<Byte>() {}.type)
        private val SHORT: Type = (object : TypeReference<Short>() {}.type)
        private val INT: Type = (object : TypeReference<Int>() {}.type)
        private val LONG: Type = (object : TypeReference<Long>() {}.type)
        private val UBYTE: Type = (object : TypeReference<UByte>() {}.type)
        private val USHORT: Type = (object : TypeReference<UShort>() {}.type)
        private val UINT: Type = (object : TypeReference<UInt>() {}.type)
        private val ULONG: Type = (object : TypeReference<ULong>() {}.type)
        private val FLOAT: Type = (object : TypeReference<Float>() {}.type)
        private val DOUBLE: Type = (object : TypeReference<Double>() {}.type)
        private val BOOLEAN: Type = (object : TypeReference<Boolean>() {}.type)
        private val CHAR: Type = (object : TypeReference<Char>() {}.type)
        private val STRING: Type = (object : TypeReference<String>() {}.type)
        private val ARRAY_BYTE: Type = (object : TypeReference<Array<Byte>>() {}.type)
        private val ARRAY_SHORT: Type = (object : TypeReference<Array<Short>>() {}.type)
        private val ARRAY_INT: Type = (object : TypeReference<Array<Int>>() {}.type)
        private val ARRAY_LONG: Type = (object : TypeReference<Array<Long>>() {}.type)
        private val ARRAY_UBYTE: Type = (object : TypeReference<Array<UByte>>() {}.type)
        private val ARRAY_USHORT: Type = (object : TypeReference<Array<UShort>>() {}.type)
        private val ARRAY_UINT: Type = (object : TypeReference<Array<UInt>>() {}.type)
        private val ARRAY_ULONG: Type = (object : TypeReference<Array<ULong>>() {}.type)
        private val ARRAY_FLOAT: Type = (object : TypeReference<Array<Float>>() {}.type)
        private val ARRAY_DOUBLE: Type = (object : TypeReference<Array<Double>>() {}.type)
        private val ARRAY_CHAR: Type = (object : TypeReference<Array<Char>>() {}.type)
        private val ARRAY_STRING: Type = (object : TypeReference<Array<String>>() {}.type)
        private val FILE: Type = (object : TypeReference<File>() {}.type)

        fun typeRefToJavaType(type: Type): Type = when (type) {
            BYTE -> Byte::class.java
            SHORT -> Short::class.java
            INT -> Int::class.java
            LONG -> Long::class.java
            UBYTE -> UByte::class.java
            USHORT -> UShort::class.java
            UINT -> UInt::class.java
            ULONG -> ULong::class.java
            FLOAT -> Float::class.java
            DOUBLE -> Double::class.java
            BOOLEAN -> Boolean::class.java
            CHAR -> Char::class.java
            STRING -> String::class.java
            ARRAY_BYTE -> Array<Byte>::class.java
            ARRAY_SHORT -> Array<Short>::class.java
            ARRAY_INT -> Array<Int>::class.java
            ARRAY_LONG -> Array<Long>::class.java
            ARRAY_UBYTE -> Array<UByte>::class.java
            ARRAY_USHORT -> Array<UShort>::class.java
            ARRAY_UINT -> Array<UInt>::class.java
            ARRAY_ULONG -> Array<ULong>::class.java
            ARRAY_FLOAT -> Array<Float>::class.java
            ARRAY_DOUBLE -> Array<Double>::class.java
            ARRAY_CHAR -> Array<Char>::class.java
            ARRAY_STRING -> Array<String>::class.java
            FILE -> File::class.java
            else -> type
        }

    }

    private val jsonSchemaBuilder = OApiJsonSchemaBuilder()

    fun build(type: Type, components: ComponentsContext, config: SwaggerUIPluginConfig): Schema<Any> {
        return Schema<Any>().apply {
            when (typeRefToJavaType(type)) {
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
                File::class.java -> {
                    this.type = "string"
                    format = "binary"
                }
                Array<File>::class.java -> {
                    this.type = "array"
                    items = Schema<String>().apply {
                        this.type = "string"
                        format = "binary"
                    }
                }
                else -> {
                    return jsonSchemaBuilder.build(type, components, config)
                }
            }
        }
    }

}
