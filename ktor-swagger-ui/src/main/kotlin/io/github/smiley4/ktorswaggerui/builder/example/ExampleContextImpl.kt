package io.github.smiley4.ktorswaggerui.builder.example

import io.github.smiley4.ktorswaggerui.builder.route.RouteMeta
import io.github.smiley4.ktorswaggerui.data.*
import io.swagger.v3.oas.models.examples.Example

/**
 * Implementation of an [ExampleContext].
 */
class ExampleContextImpl(private val encoder: ExampleEncoder?) : ExampleContext {

    private val rootExamples = mutableMapOf<ExampleDescriptor, Example>()
    private val componentExamples = mutableMapOf<String, Example>()


    /**
     * Add all global/shared examples from the config that are placed in the components-section of the openapi-spec
     */
    fun addShared(config: ExampleConfigData) {
        config.sharedExamples.forEach { (_, exampleDescriptor) ->
            val example = generateExample(exampleDescriptor, null)
            componentExamples[exampleDescriptor.name] = example
        }
        config.securityExamples.forEach { exampleDescriptor ->
            val example = generateExample(exampleDescriptor, null)
            rootExamples[exampleDescriptor] = example
        }
    }


    /**
     * Collect and add all examples for the given routes
     */
    fun add(routes: Collection<RouteMeta>) {
        collectExampleDescriptors(routes).forEach { (exampleDescriptor, typeDescriptor) ->
            val example = generateExample(exampleDescriptor, typeDescriptor)
            rootExamples[exampleDescriptor] = example
        }
    }


    /**
     * Collect all [ExampleDescriptor]s from the given routes
     */
    private fun collectExampleDescriptors(routes: Collection<RouteMeta>): List<Pair<ExampleDescriptor, TypeDescriptor>> =
        buildList {
            routes
                .filter { !it.documentation.hidden }
                .forEach { route ->
                    route.documentation.request.also { request ->
                        request.parameters.forEach { parameter ->
                            parameter.example?.also { add(it to parameter.type) }
                        }
                        request.body?.also { body ->
                            if (body is OpenApiSimpleBodyData) {
                                addAll(body.examples.map { it to body.type })
                            }
                        }
                    }
                    route.documentation.responses.forEach { response ->
                        response.body?.also { body ->
                            if (body is OpenApiSimpleBodyData) {
                                addAll(body.examples.map { it to body.type })
                            }
                        }
                    }
                }
        }


    /**
     * Generate a swagger [Example] from the given [ExampleDescriptor]
     */
    private fun generateExample(exampleDescriptor: ExampleDescriptor, type: TypeDescriptor?): Example {
        return when (exampleDescriptor) {
            is ValueExampleDescriptor -> Example().also {
                it.value =
                    if (encoder != null) encoder.invoke(type, exampleDescriptor.value)
                    else exampleDescriptor.value
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
