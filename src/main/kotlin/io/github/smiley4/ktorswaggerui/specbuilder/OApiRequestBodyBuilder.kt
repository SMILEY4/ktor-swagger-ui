package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.OpenApiBody
import io.swagger.v3.oas.models.parameters.RequestBody

/**
 * Builder for the OpenAPI Request Body
 */
class OApiRequestBodyBuilder {

    private val contentBuilder = OApiContentBuilder()

    fun build(body: OpenApiBody, components: ComponentsContext, config: SwaggerUIPluginConfig): RequestBody {
        return RequestBody().apply {
            description = body.description
            required = body.required
            content = contentBuilder.build(body, components, config)
        }
    }

}
