package io.github.smiley4.ktorswaggerui.apispec

import io.github.smiley4.ktorswaggerui.documentation.RequestParameterDocumentation
import io.swagger.v3.oas.models.parameters.Parameter

/**
 * Generator for OpenAPI Parameters
 */
class OApiParametersGenerator {

    /**
     * Generate the OpenAPI Parameters from the given configs
     */
    fun generate(configs: List<RequestParameterDocumentation>): List<Parameter> {
        return configs.map { paramCfg ->
            Parameter().apply {
                `in` = when (paramCfg.location) {
                    RequestParameterDocumentation.Location.QUERY -> "query"
                    RequestParameterDocumentation.Location.HEADER -> "header"
                    RequestParameterDocumentation.Location.PATH -> "path"
                }
                name = paramCfg.name
                schema = OApiSchemaGenerator().generate(paramCfg.schema)
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
