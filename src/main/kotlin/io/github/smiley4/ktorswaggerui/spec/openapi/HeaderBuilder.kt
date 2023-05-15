package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.dsl.OpenApiHeader
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaContext
import io.swagger.v3.oas.models.headers.Header

class HeaderBuilder(
    private val schemaContext: SchemaContext
) {

    fun build(header: OpenApiHeader): Header =
        Header().also {
            it.description = header.description
            it.required = header.required
            it.deprecated = header.deprecated
            it.schema = header.type?.let { t -> schemaContext.getSchema(t) }
        }

}