package io.github.smiley4.ktorswaggerui.data

import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.KType

sealed interface TypeDescriptor
class SwaggerTypeDescriptor(val schema: Schema<*>) : TypeDescriptor
class KTypeDescriptor(val type: KType) : TypeDescriptor
class ArrayTypeDescriptor(val type: TypeDescriptor) : TypeDescriptor
class OneOfTypeDescriptor(val types: List<TypeDescriptor>) : TypeDescriptor
class EmptyTypeDescriptor : TypeDescriptor