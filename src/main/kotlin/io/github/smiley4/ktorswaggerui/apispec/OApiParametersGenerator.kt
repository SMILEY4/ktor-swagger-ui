package io.github.smiley4.ktorswaggerui.apispec

import io.github.smiley4.ktorswaggerui.documentation.RouteParameter
import io.github.smiley4.ktorswaggerui.routing.SchemaRef
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.Parameter
import java.math.BigDecimal

/**
 * Generator for OpenAPI Parameters
 */
class OApiParametersGenerator {

    /**
     * Generate the OpenAPI Parameters from the given configs
     */
    fun generate(configs: List<RouteParameter>): List<Parameter> {
        return configs.map { paramCfg ->
            Parameter().apply {
                `in` = when (paramCfg.location) {
                    RouteParameter.Location.QUERY -> "query"
                    RouteParameter.Location.HEADER -> "header"
                    RouteParameter.Location.PATH -> "path"
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
