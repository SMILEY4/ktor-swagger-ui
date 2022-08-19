package io.github.smiley4.ktorswaggerui.apispec

import io.github.smiley4.ktorswaggerui.documentation.RouteParameter
import io.github.smiley4.ktorswaggerui.routing.SchemaRef
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.Parameter

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
                description = paramCfg.description
                required = paramCfg.required
                deprecated = paramCfg.deprecated
                allowEmptyValue = paramCfg.allowEmptyValue
                example = paramCfg.explode
                allowReserved = paramCfg.allowReserved
                schema = Schema<String>().apply {
                    when (val paramSchema = paramCfg.getSchema()) {
                        is RouteParameter.ArraySchema -> {
                            type = "array"
                            items = Schema<String>().apply {
                                when (paramSchema.type) {
                                    is RouteParameter.ObjectSchema -> {
                                        type = "object"
                                        additionalProperties = Schema<String>().apply {
                                            `$ref` = SchemaRef.refOfClass(paramSchema.type.type)
                                        }
                                    }
                                    is RouteParameter.PrimitiveSchema -> {
                                        type = when (paramSchema.type.type) {
                                            RouteParameter.Type.STRING -> "string"
                                            RouteParameter.Type.INTEGER -> "integer"
                                            RouteParameter.Type.NUMBER -> "number"
                                            RouteParameter.Type.BOOLEAN -> "boolean"
                                        }
                                    }

                                }
                            }
                        }
                        is RouteParameter.ObjectSchema -> {
                            type = "object"
                            additionalProperties = Schema<String>().apply {
                                `$ref` = SchemaRef.refOfClass(paramSchema.type)
                            }
                        }
                        is RouteParameter.PrimitiveSchema -> {
                            type = when (paramSchema.type) {
                                RouteParameter.Type.STRING -> "string"
                                RouteParameter.Type.INTEGER -> "integer"
                                RouteParameter.Type.NUMBER -> "number"
                                RouteParameter.Type.BOOLEAN -> "boolean"
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

}