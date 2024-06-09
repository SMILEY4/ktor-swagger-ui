package io.github.smiley4.ktorswaggerui.builder.schema

import io.github.smiley4.ktorswaggerui.data.TypeDescriptor
import io.swagger.v3.oas.models.media.Schema

/**
 * Provides schemas for an openapi-spec
 */
interface SchemaContext {

    /**
     * Get a [Schema] (or a ref to a schema) by its [TypeDescriptor]
     */
    fun getSchema(typeDescriptor: TypeDescriptor): Schema<*>

    /**
     * Get all schemas placed in the components-section of the spec.
     */
    fun getComponentSection(): Map<String, Schema<*>>
}
