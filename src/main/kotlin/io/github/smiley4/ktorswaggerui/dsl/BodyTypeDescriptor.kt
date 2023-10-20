package io.github.smiley4.ktorswaggerui.dsl

import kotlin.reflect.KClass

/**
 * Describes the type/schema of a request or response body.
 * [BodyTypeDescriptor]s can be nested to build more specific bodies from simple types
 */
sealed interface BodyTypeDescriptor {

    companion object {

        /**
         * A [BodyTypeDescriptor] of the specific given type (or empty if type is null).
         */
        fun typeOf(type: KClass<*>?) = type?.let { SchemaBodyTypeDescriptor(it.asSchemaType()) } ?: EmptyBodyTypeDescriptor()


        /**
         * A [BodyTypeDescriptor] of the specific given type (or empty if type is null).
         */
        fun typeOf(type: SchemaType?) = type?.let { SchemaBodyTypeDescriptor(it) } ?: EmptyBodyTypeDescriptor()


        /**
         * A [BodyTypeDescriptor] of the specific given generic type.
         */
        inline fun <reified T> typeOf() = SchemaBodyTypeDescriptor(getSchemaType<T>())


        /**
         * Type can be any one of the given types.
         */
        fun oneOf(vararg type: KClass<*>) = OneOfBodyTypeDescriptor(type.toList().map { typeOf(it.asSchemaType()) })


        /**
         * Type can be any one of the given types.
         */
        @JvmName("oneOfClass")
        fun oneOf(types: Collection<KClass<*>>) = OneOfBodyTypeDescriptor(types.map { typeOf(it.asSchemaType()) })


        /**
         * Type can be any one of the given types.
         */
        fun oneOf(vararg type: SchemaType) = OneOfBodyTypeDescriptor(type.map { typeOf(it) })


        /**
         * Type can be any one of the given types.
         */
        @JvmName("oneOfType")
        fun oneOf(types: Collection<SchemaType>) = OneOfBodyTypeDescriptor(types.map { typeOf(it) })


        /**
         * Type can be any one of the given types.
         */
        fun oneOf(vararg type: BodyTypeDescriptor) = OneOfBodyTypeDescriptor(type.toList())


        /**
         * Type can be any one of the given types.
         */
        @JvmName("oneOfDescriptor")
        fun oneOf(types: Collection<BodyTypeDescriptor>) = OneOfBodyTypeDescriptor(types.toList())


        /**
         * Type is an array of the specific given type.
         */
        fun multipleOf(type: KClass<*>) = CollectionBodyTypeDescriptor(typeOf(type.asSchemaType()))


        /**
         * Type is an array of the specific given type.
         */
        fun multipleOf(type: SchemaType) = CollectionBodyTypeDescriptor(typeOf(type))


        /**
         * Type is an array of the given type.
         */
        fun multipleOf(type: BodyTypeDescriptor) = CollectionBodyTypeDescriptor(type)


        /**
         * A [BodyTypeDescriptor] of the specific given custom schema.
         */
        fun custom(customSchemaId: String) = CustomRefBodyTypeDescriptor(customSchemaId)


        /**
         * An empty type.
         */
        fun empty() = EmptyBodyTypeDescriptor()
    }

}


/**
 * Describes an empty type
 */
class EmptyBodyTypeDescriptor : BodyTypeDescriptor


/**
 * Describes a specific type/schema
 */
class SchemaBodyTypeDescriptor(val schemaType: SchemaType) : BodyTypeDescriptor


/**
 * Describes any one of the given types
 */
class OneOfBodyTypeDescriptor(val elements: List<BodyTypeDescriptor>) : BodyTypeDescriptor


/**
 * Describes an array of the given type
 */
class CollectionBodyTypeDescriptor(val schemaType: BodyTypeDescriptor) : BodyTypeDescriptor


/**
 * Describes the custom schema/type with the given id
 */
class CustomRefBodyTypeDescriptor(val customSchemaId: String) : BodyTypeDescriptor
