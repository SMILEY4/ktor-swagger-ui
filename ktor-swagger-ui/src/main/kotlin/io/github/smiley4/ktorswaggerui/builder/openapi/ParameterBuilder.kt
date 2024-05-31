package io.github.smiley4.ktorswaggerui.builder.openapi

import io.github.smiley4.ktorswaggerui.builder.example.ExampleContext
import io.github.smiley4.ktorswaggerui.builder.schema.SchemaContext
import io.github.smiley4.ktorswaggerui.data.OpenApiRequestParameterData
import io.github.smiley4.ktorswaggerui.data.ParameterLocation
import io.swagger.v3.oas.models.parameters.Parameter

class ParameterBuilder(
    private val schemaContext: SchemaContext,
    private val exampleContext: ExampleContext
) {

    fun build(parameter: OpenApiRequestParameterData): Parameter =
        Parameter().also {
            it.`in` = when (parameter.location) {
                ParameterLocation.QUERY -> "query"
                ParameterLocation.HEADER -> "header"
                ParameterLocation.PATH -> "path"
            }
            it.name = parameter.name
            it.description = parameter.description
            it.required = parameter.required
            it.deprecated = parameter.deprecated
            it.allowEmptyValue = parameter.allowEmptyValue
            it.explode = parameter.explode
            it.example = parameter.example?.let { e -> exampleContext.getExample(e).value } // todo: example"S" ?
            it.allowReserved = parameter.allowReserved
            it.schema = schemaContext.getSchema(parameter.type)
            it.style = parameter.style
        }

}
