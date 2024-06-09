package io.github.smiley4.ktorswaggerui.builder.example

import io.github.smiley4.ktorswaggerui.builder.route.RouteMeta
import io.github.smiley4.ktorswaggerui.data.ExampleConfigData
import io.github.smiley4.ktorswaggerui.data.ExampleDescriptor
import io.github.smiley4.ktorswaggerui.data.OpenApiSimpleBodyData
import io.github.smiley4.ktorswaggerui.data.RefExampleDescriptor
import io.github.smiley4.ktorswaggerui.data.SwaggerExampleDescriptor
import io.github.smiley4.ktorswaggerui.data.ValueExampleDescriptor
import io.swagger.v3.oas.models.examples.Example

/**
 * Implementation of an [ExampleContext].
 */
class ExampleContextImpl : ExampleContext {

    private val rootExamples = mutableMapOf<ExampleDescriptor, Example>()
    private val componentExamples = mutableMapOf<String, Example>()


    /**
     * Add all global/shared examples from the config that are placed in the components-section of the openapi-spec
     */
    fun addShared(config: ExampleConfigData) {
        config.sharedExamples.forEach { (_, exampleDescriptor) ->
            val example = generateExample(exampleDescriptor)
            componentExamples[exampleDescriptor.name] = example
        }
    }


    /**
     * Collect and add all examples for the given routes
     */
    fun add(routes: Collection<RouteMeta>) {
        collectExampleDescriptors(routes).forEach { exampleDescriptor ->
            rootExamples[exampleDescriptor] = generateExample(exampleDescriptor)
        }
    }


    /**
     * Collect all [ExampleDescriptor]s from the given routes
     */
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


    /**
     * Generate a swagger [Example] from the given [ExampleDescriptor]
     */
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
            is SwaggerExampleDescriptor -> exampleDescriptor.example
        }
    }

    override fun getExample(descriptor: ExampleDescriptor): Example {
        return rootExamples[descriptor] ?: throw NoSuchElementException("no root-example for given example-descriptor")
    }

    override fun getComponentSection(): Map<String, Example> {
        return componentExamples
    }

}
