package io.github.smiley4.ktorswaggerui.data

import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.KType
import kotlin.reflect.typeOf

sealed interface TypeDescriptor
class SwaggerTypeDescriptor(val schema: Schema<*>) : TypeDescriptor
class KTypeDescriptor(val type: KType) : TypeDescriptor
class ArrayTypeDescriptor(val type: TypeDescriptor) : TypeDescriptor
class AnyOfTypeDescriptor(val types: List<TypeDescriptor>) : TypeDescriptor
class EmptyTypeDescriptor : TypeDescriptor
class RefTypeDescriptor(val schemaId: String) : TypeDescriptor

inline fun <reified T> type() = KTypeDescriptor(typeOf<T>())

fun empty() = EmptyTypeDescriptor()

fun ref(schemaId: String) = RefTypeDescriptor(schemaId)

fun array(type: TypeDescriptor) = ArrayTypeDescriptor(type)
fun array(type: Schema<*>) = ArrayTypeDescriptor(SwaggerTypeDescriptor(type))
fun array(type: KType) = ArrayTypeDescriptor(KTypeDescriptor(type))
inline fun <reified T> array() = ArrayTypeDescriptor(KTypeDescriptor(typeOf<T>()))

fun anyOf(vararg types: TypeDescriptor) = AnyOfTypeDescriptor(types.toList())
fun anyOf(types: Collection<TypeDescriptor>) = AnyOfTypeDescriptor(types.toList())

fun anyOf(vararg types: Schema<*>) = AnyOfTypeDescriptor(types.map { SwaggerTypeDescriptor(it) })
fun anyOf(types: Collection<Schema<*>>) = AnyOfTypeDescriptor(types.map { SwaggerTypeDescriptor(it) })

fun anyOf(vararg types: KType) = AnyOfTypeDescriptor(types.map { KTypeDescriptor(it) })
fun anyOf(types: Collection<KType>) = AnyOfTypeDescriptor(types.map { KTypeDescriptor(it) })
