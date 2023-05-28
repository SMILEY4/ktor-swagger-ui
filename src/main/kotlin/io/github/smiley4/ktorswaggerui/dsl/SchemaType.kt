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
    if(rawName.contains("<") || rawName.contains(">")) {
        return rawName
    } else {
        return (this.classifier as KClass<*>).simpleName ?: rawName
    }
}

fun KClass<*>.asSchemaType() = this.starProjectedType

