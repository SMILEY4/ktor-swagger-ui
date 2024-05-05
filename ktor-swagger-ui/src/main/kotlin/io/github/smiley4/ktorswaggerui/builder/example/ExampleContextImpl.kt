package io.github.smiley4.ktorswaggerui.builder.example

import io.github.smiley4.ktorswaggerui.builder.route.RouteMeta
import io.github.smiley4.ktorswaggerui.data.ExampleDescriptor
import io.github.smiley4.ktorswaggerui.data.OpenApiSimpleBodyData
import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.github.smiley4.ktorswaggerui.data.RefExampleDescriptor
import io.github.smiley4.ktorswaggerui.data.ValueExampleDescriptor
import io.swagger.v3.oas.models.examples.Example

class ExampleContextImpl : ExampleContext {

    private val rootExamples = mutableMapOf<ExampleDescriptor, Example>()
    private val componentExamples = mutableMapOf<String, Example>()

    fun addGlobal(config: PluginConfigData) {
        TODO("add global examples from config to components")
    }

    fun add(routes: Collection<RouteMeta>) {
        collectExampleDescriptors(routes).forEach { exampleDescriptor ->
            val example = generateExample(exampleDescriptor)
            if (exampleDescriptor is ValueExampleDescriptor && exampleDescriptor.inComponents == true) {
                rootExamples[exampleDescriptor] = Example().also {
                    it.`$ref` = "#/components/examples/${exampleDescriptor.name}"
                }
                componentExamples[exampleDescriptor.name] = example
            } else {
                rootExamples[exampleDescriptor] = example
            }
        }
    }

    private fun collectExampleDescriptors(routes: Collection<RouteMeta>): List<ExampleDescriptor> {
        val descriptors = mutableListOf<ExampleDescriptor>()

        routes
            .filter { !it.documentation.hidden }
            .forEach { route ->
                route.documentation.request.also { request ->
                    request.parameters.forEach { parameter ->
                        parameter.example?.also { descriptors.add(it) }
                    }
                    request.body?.also { body ->
                        if (body is OpenApiSimpleBodyData) {
                            descriptors.addAll(body.examples)
                        }
                    }
                }
                route.documentation.responses.forEach { response ->
                    response.body?.also { body ->
                        if (body is OpenApiSimpleBodyData) {
                            descriptors.addAll(body.examples)
                        }
                    }
                }
            }

        return descriptors
    }

    private fun generateExample(exampleDescriptor: ExampleDescriptor): Example {
        return when (exampleDescriptor) {
            is ValueExampleDescriptor -> Example().also {
                it.value = exampleDescriptor.value
                it.summary = exampleDescriptor.summary
                it.description = exampleDescriptor.description
            }
            is RefExampleDescriptor -> Example().also {
                it.`$ref` = "#/components/examples/${exampleDescriptor.refName}"
            }
        }
    }

    override fun getExample(descriptor: ExampleDescriptor): Example {
        return rootExamples[descriptor] ?: throw Exception("no root-example for given example-descriptor")
    }

    override fun getComponentSection(): Map<String, Example> {
        return componentExamples
    }

}