package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.dsl.OpenApiRequestParameter
import io.swagger.v3.oas.models.parameters.Parameter

/**
 * Generator for OpenAPI Parameters
 */
class OApiParametersGenerator {

    /**
     * Generate the OpenAPI Parameters from the given configs
     */
    fun generate(configs: List<OpenApiRequestParameter>): List<Parameter> {
        return configs.map { paramCfg ->
            Parameter().apply {
                `in` = when (paramCfg.location) {
                    OpenApiRequestParameter.Location.QUERY -> "query"
                    OpenApiRequestParameter.Location.HEADER -> "header"
                    OpenApiRequestParameter.Location.PATH -> "path"
                }
                name = paramCfg.name
                schema = OApiSchemaGenerator().generate(paramCfg.schema, ComponentsContext.NOOP)
                description = paramCfg.description
                required = paramCfg.required
                deprecated = paramCfg.deprecated
                allowEmptyValue = paramCfg.allowEmptyValue
                example = paramCfg.explode
                allowReserved = paramCfg.allowReserved
            }
        }
    }

}
