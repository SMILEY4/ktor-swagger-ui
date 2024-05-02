package io.github.smiley4.ktorswaggerui.data

import kotlin.reflect.KType

sealed interface TypeDescriptor {
    companion object {
        fun todo(v: Any?) = EmptyTypeDescriptor
    }
}

class KTypeDescriptor(val type: KType) : TypeDescriptor

class ArrayTypeDescriptor(val type: TypeDescriptor) : TypeDescriptor

class OneOfTypeDescriptor(val types: List<TypeDescriptor>) : TypeDescriptor

object EmptyTypeDescriptor : TypeDescriptor