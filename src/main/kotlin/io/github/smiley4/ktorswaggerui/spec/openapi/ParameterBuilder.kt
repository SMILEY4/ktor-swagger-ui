package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.dsl.OpenApiRequestParameter
import io.github.smiley4.ktorswaggerui.spec.example.ExampleContext
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaContext
import io.swagger.v3.oas.models.parameters.Parameter

class ParameterBuilder(
    private val schemaContext: SchemaContext,
    private val exampleContext: ExampleContext,
) {

    fun build(parameter: OpenApiRequestParameter): Parameter =
        Parameter().also {
            it.`in` = when (parameter.location) {
                OpenApiRequestParameter.Location.QUERY -> "query"
                OpenApiRequestParameter.Location.HEADER -> "header"
                OpenApiRequestParameter.Location.PATH -> "path"
            }
            it.name = parameter.name
            it.description = parameter.description
            it.required = parameter.required
            it.deprecated = parameter.deprecated
            it.allowEmptyValue = parameter.allowEmptyValue
            it.explode = parameter.explode
            it.example = exampleContext.getExample(parameter)
            it.allowReserved = parameter.allowReserved
            it.schema = schemaContext.getSchema(parameter.type)
        }

}