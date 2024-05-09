package io.github.smiley4.ktorswaggerui.builder.schema

import io.github.smiley4.ktorswaggerui.data.TypeDescriptor
import io.swagger.v3.oas.models.media.Schema

interface SchemaContext {
    fun getSchema(typeDescriptor: TypeDescriptor): Schema<*>
    fun getComponentSection(): Map<String, Schema<*>>
}
