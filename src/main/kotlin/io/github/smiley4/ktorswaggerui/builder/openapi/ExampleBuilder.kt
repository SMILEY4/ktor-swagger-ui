//package io.github.smiley4.ktorswaggerui.builder.openapi
//
//import io.github.smiley4.ktorswaggerui.data.PluginConfigData
//import io.github.smiley4.ktorswaggerui.dsl.routes.OpenApiExample
//import io.github.smiley4.ktorswaggerui.dsl.SchemaType
//import io.swagger.v3.oas.models.examples.Example
//
//class ExampleBuilder(
//    private val config: PluginConfigData
//) {
//
//    fun build(type: SchemaType?, example: OpenApiExample): Example =
//        Example().also {
//            it.value = buildExampleValue(type, example.value)
//            it.summary = example.summary
//            it.description = example.description
//        }
//
//    fun buildExampleValue(type: SchemaType?, value: Any): String {
//        return config.encoding.exampleEncoder(type, value) ?: value.toString()
//    }
//
//}
