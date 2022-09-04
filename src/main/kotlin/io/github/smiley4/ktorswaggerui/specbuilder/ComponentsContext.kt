package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.dsl.OpenApiExample
import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.KClass

/**
 * Container holding and collecting information about the OpenApi "Components"-Object
 */
data class ComponentsContext(
    val schemasInComponents: Boolean,
    val schemas: MutableMap<String, Schema<*>>,
    val examplesInComponents: Boolean,
    val examples: MutableMap<String, OpenApiExample>
) {

    companion object {
        val NOOP = ComponentsContext(false, mutableMapOf(), false, mutableMapOf())
    }


    /**
     * Add the given schema for the given type to the components-section
     * @return the ref-string for the schema
     */
    fun addSchema(type: KClass<*>, schema: Schema<*>): String {
        val key = type.qualifiedName ?: "?"
        if (!schemas.containsKey(key)) {
            schemas[key] = schema
        }
        return asSchemaRef(key)
    }


    /**
     * Add the given example with the given name to the components-section
     * @return the ref-string for the example
     */
    fun addExample(name: String, example: OpenApiExample): String {
        if (!examples.containsKey(name)) {
            examples[name] = example
            return asExampleRef(name)
        } else {
            if (isSameExample(examples[name]!!, example)) {
                return asExampleRef(name)
            } else {
                val key = asUniqueName(name, example)
                examples[key] = example
                return asExampleRef(key)
            }
        }
    }


    private fun isSameExample(a: OpenApiExample, b: OpenApiExample): Boolean {
        return a.value == b.value
                && a.description == b.description
                && a.summary == b.summary
    }


    private fun asUniqueName(name: String, example: OpenApiExample): String {
        return name + "#" + example.hashCode().toString(16)
    }


    private fun asSchemaRef(key: String) = "#/components/schemas/$key"


    private fun asExampleRef(key: String) = "#/components/examples/$key"

}