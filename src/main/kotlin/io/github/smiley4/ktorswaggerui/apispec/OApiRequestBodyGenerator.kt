package io.github.smiley4.ktorswaggerui.apispec

import io.github.smiley4.ktorswaggerui.documentation.RouteBody
import io.swagger.v3.oas.models.parameters.RequestBody

/**
 * Generator for the OpenAPI Request Body
 */
class OApiRequestBodyGenerator {

    /**
     * Generate the Request Body from the given config
     */
    fun generate(config: RouteBody): RequestBody {
        return RequestBody().apply {
            description = config.description
            required = config.required
            content = OApiContentGenerator().generate(config)
        }
    }

}