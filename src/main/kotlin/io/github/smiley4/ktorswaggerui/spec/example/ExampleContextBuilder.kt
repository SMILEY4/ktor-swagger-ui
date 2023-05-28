package io.github.smiley4.ktorswaggerui.spec.example

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.OpenApiBaseBody
import io.github.smiley4.ktorswaggerui.dsl.OpenApiExample
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRequestParameter
import io.github.smiley4.ktorswaggerui.dsl.OpenApiResponse
import io.github.smiley4.ktorswaggerui.dsl.OpenApiSimpleBody
import io.github.smiley4.ktorswaggerui.dsl.SchemaType
import io.github.smiley4.ktorswaggerui.dsl.getSchemaType
import io.github.smiley4.ktorswaggerui.spec.openapi.ExampleBuilder
import io.github.smiley4.ktorswaggerui.spec.route.RouteMeta
import io.swagger.v3.oas.models.examples.Example

class ExampleContextBuilder(
    private val config: SwaggerUIPluginConfig,
    private val exampleBuilder: ExampleBuilder
) {

    fun build(routes: Collection<RouteMeta>): ExampleContext {
        return ExampleContext()
            .also { ctx -> routes.forEach { handle(ctx, it) } }
    }


    private fun handle(ctx: ExampleContext, route: RouteMeta) {
        route.documentation.getRequest().getBody()?.also { handle(ctx, it) }
        route.documentation.getRequest().getParameters().forEach { handle(ctx, it) }
        route.documentation.getResponses().getResponses().forEach { handle(ctx, it) }
    }

    private fun handle(ctx: ExampleContext, response: OpenApiResponse) {
        response.getBody()?.also { handle(ctx, it) }
    }


    private fun handle(ctx: ExampleContext, body: OpenApiBaseBody) {
        return when (body) {
            is OpenApiSimpleBody -> handle(ctx, body)
            else -> Unit
        }
    }


    private fun handle(ctx: ExampleContext, body: OpenApiSimpleBody) {
        body.getExamples().forEach { (name, value) ->
            ctx.addExample(body, name, createExample(body.type ?: getSchemaType<String>(), value))
        }
    }

    private fun handle(ctx: ExampleContext, parameter: OpenApiRequestParameter) {
        parameter.example?.also { example ->
            ctx.addExample(parameter, createExample(parameter.type, example))
        }
    }

    private fun createExample(type: SchemaType?, value: Any): String {
        return exampleBuilder.buildExampleValue(type, value)
    }

    private fun createExample(type: SchemaType?, example: OpenApiExample): Example {
        return exampleBuilder.build(type, example)
    }

}