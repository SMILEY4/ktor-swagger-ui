package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.OpenApiHeader
import io.swagger.v3.oas.models.headers.Header

/**
 * Builder for the OpenAPI Header Object
 */
class OApiHeaderBuilder {

    private val schemaBuilder = OApiSchemaBuilder()

    fun build(header: OpenApiHeader, config: SwaggerUIPluginConfig): Header {
        return Header().apply {
            description = header.description
            required = header.required
            deprecated = header.deprecated
            schema = header.type?.let { t -> schemaBuilder.build(t, ComponentsContext.NOOP, config) }
        }
    }

}