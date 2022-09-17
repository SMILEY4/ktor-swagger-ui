package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRequestParameter
import io.swagger.v3.oas.models.parameters.Parameter

/**
 * Builder for OpenAPI Parameters
 */
class OApiParametersBuilder {

    private val schemaBuilder = OApiSchemaBuilder()


    fun build(parameters: List<OpenApiRequestParameter>, config: SwaggerUIPluginConfig): List<Parameter> {
        return parameters.map { parameter ->
            Parameter().apply {
                `in` = when (parameter.location) {
                    OpenApiRequestParameter.Location.QUERY -> "query"
                    OpenApiRequestParameter.Location.HEADER -> "header"
                    OpenApiRequestParameter.Location.PATH -> "path"
                }
                name = parameter.name
                schema = schemaBuilder.build(parameter.type, ComponentsContext.NOOP, config)
                description = parameter.description
                required = parameter.required
                deprecated = parameter.deprecated
                allowEmptyValue = parameter.allowEmptyValue
                example = parameter.explode
                allowReserved = parameter.allowReserved
            }
        }
    }

}
