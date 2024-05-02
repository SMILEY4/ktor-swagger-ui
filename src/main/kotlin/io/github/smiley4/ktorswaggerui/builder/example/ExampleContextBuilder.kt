//package io.github.smiley4.ktorswaggerui.builder.example
//
//import io.github.smiley4.ktorswaggerui.dsl.routes.OpenApiBaseBody
//import io.github.smiley4.ktorswaggerui.dsl.routes.OpenApiExample
//import io.github.smiley4.ktorswaggerui.dsl.routes.OpenApiRequestParameter
//import io.github.smiley4.ktorswaggerui.dsl.routes.OpenApiResponse
//import io.github.smiley4.ktorswaggerui.dsl.routes.OpenApiSimpleBody
//import io.github.smiley4.ktorswaggerui.dsl.SchemaType
//import io.github.smiley4.ktorswaggerui.dsl.getSchemaType
//import io.github.smiley4.ktorswaggerui.builder.openapi.ExampleBuilder
//import io.github.smiley4.ktorswaggerui.builder.route.RouteMeta
//import io.github.smiley4.ktorswaggerui.dsl.BodyTypeDescriptor
//import io.github.smiley4.ktorswaggerui.dsl.CollectionBodyTypeDescriptor
//import io.github.smiley4.ktorswaggerui.dsl.CustomRefBodyTypeDescriptor
//import io.github.smiley4.ktorswaggerui.dsl.EmptyBodyTypeDescriptor
//import io.github.smiley4.ktorswaggerui.dsl.OneOfBodyTypeDescriptor
//import io.github.smiley4.ktorswaggerui.dsl.SchemaBodyTypeDescriptor
//import io.swagger.v3.oas.models.examples.Example
//
//class ExampleContextBuilder(
//    private val exampleBuilder: ExampleBuilder
//) {
//
//    fun build(routes: Collection<RouteMeta>): ExampleContext {
//        return ExampleContext()
//            .also { ctx -> routes.forEach { handle(ctx, it) } }
//    }
//
//
//    private fun handle(ctx: ExampleContext, route: RouteMeta) {
//        route.documentation.getRequest().getBody()?.also { handle(ctx, it) }
//        route.documentation.getRequest().getParameters().forEach { handle(ctx, it) }
//        route.documentation.getResponses().getResponses().forEach { handle(ctx, it) }
//    }
//
//    private fun handle(ctx: ExampleContext, response: OpenApiResponse) {
//        response.getBody()?.also { handle(ctx, it) }
//    }
//
//
//    private fun handle(ctx: ExampleContext, body: OpenApiBaseBody) {
//        return when (body) {
//            is OpenApiSimpleBody -> handle(ctx, body)
//            else -> Unit
//        }
//    }
//
//
//    private fun handle(ctx: ExampleContext, body: OpenApiSimpleBody) {
//        body.getExamples().forEach { (name, value) ->
//            val bodyType = getRelevantSchemaType(body.type, getSchemaType<String>())
//            ctx.addExample(body, name, createExample(bodyType, value))
//        }
//    }
//
//    private fun getRelevantSchemaType(typeDescriptor: BodyTypeDescriptor, fallback: SchemaType): SchemaType {
//        return when(typeDescriptor) {
//            is EmptyBodyTypeDescriptor -> fallback
//            is SchemaBodyTypeDescriptor -> typeDescriptor.schemaType
//            is CollectionBodyTypeDescriptor -> getRelevantSchemaType(typeDescriptor.schemaType, fallback)
//            is OneOfBodyTypeDescriptor -> fallback
//            is CustomRefBodyTypeDescriptor -> fallback
//        }
//    }
//
//    private fun handle(ctx: ExampleContext, parameter: OpenApiRequestParameter) {
//        parameter.example?.also { example ->
//            ctx.addExample(parameter, createExample(parameter.type, example))
//        }
//    }
//
//    private fun createExample(type: SchemaType?, value: Any): String {
//        return exampleBuilder.buildExampleValue(type, value)
//    }
//
//    private fun createExample(type: SchemaType?, example: OpenApiExample): Example {
//        return exampleBuilder.build(type, example)
//    }
//
//}
