package io.github.smiley4.ktorswaggerui.dsl

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.typeOf

typealias SchemaType = KType


inline fun <reified T> getSchemaType(): SchemaType {
    return typeOf<T>()
}

fun SchemaType.getTypeName() = this.toString()

fun SchemaType.getSimpleTypeName(): String {
    val rawName = getTypeName()
    return if (rawName.contains("<") || rawName.contains(">")) {
        rawName
    } else {
        (this.classifier as KClass<*>).simpleName ?: rawName
    }
}

fun SchemaType.getSimpleArrayElementTypeName(): String {
    if (this.arguments.size != 1) {
        throw IllegalArgumentException("Could not determine type of array-elements")
    } else {
        return this.arguments.first().let { arg ->
            arg.type?.getSimpleTypeName() ?: arg.toString()
        }
    }
}

fun KClass<*>.asSchemaType() = this.starProjectedType
