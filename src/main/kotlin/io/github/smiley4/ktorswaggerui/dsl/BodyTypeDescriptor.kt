package io.github.smiley4.ktorswaggerui.dsl

import kotlin.reflect.KClass

sealed interface BodyTypeDescriptor {

    companion object {

        fun typeOf(type: KClass<*>?) = type?.let { SchemaBodyTypeDescriptor(it.asSchemaType()) } ?: EmptyBodyTypeDescriptor()

        fun typeOf(type: SchemaType?) = type?.let { SchemaBodyTypeDescriptor(it) } ?: EmptyBodyTypeDescriptor()

        fun oneOf(vararg type: KClass<*>) = OneOfBodyTypeDescriptor(type.toList().map { typeOf(it.asSchemaType()) })

        @JvmName("oneOfClass")
        fun oneOf(types: Collection<KClass<*>>) = OneOfBodyTypeDescriptor(types.map { typeOf(it.asSchemaType()) })

        fun oneOf(vararg type: SchemaType) = OneOfBodyTypeDescriptor(type.map { typeOf(it) })

        @JvmName("oneOfType")
        fun oneOf(types: Collection<SchemaType>) = OneOfBodyTypeDescriptor(types.map { typeOf(it) })

        fun oneOf(vararg type: BodyTypeDescriptor) = OneOfBodyTypeDescriptor(type.toList())

        @JvmName("oneOfDescriptor")
        fun oneOf(types: Collection<BodyTypeDescriptor>) = OneOfBodyTypeDescriptor(types.toList())

        fun multipleOf(type: KClass<*>) = CollectionBodyTypeDescriptor(typeOf(type.asSchemaType()))

        fun multipleOf(type: SchemaType) = CollectionBodyTypeDescriptor(typeOf(type))

        fun multipleOf(type: BodyTypeDescriptor) = CollectionBodyTypeDescriptor(type)

        fun custom(customSchemaId: String) = CustomRefBodyTypeDescriptor(customSchemaId)

        fun empty() = EmptyBodyTypeDescriptor()
    }

}

class EmptyBodyTypeDescriptor : BodyTypeDescriptor

class SchemaBodyTypeDescriptor(val schemaType: SchemaType) : BodyTypeDescriptor

class OneOfBodyTypeDescriptor(val elements: List<BodyTypeDescriptor>) : BodyTypeDescriptor

class CollectionBodyTypeDescriptor(val schemaType: BodyTypeDescriptor) : BodyTypeDescriptor

class CustomRefBodyTypeDescriptor(val customSchemaId: String) : BodyTypeDescriptor
