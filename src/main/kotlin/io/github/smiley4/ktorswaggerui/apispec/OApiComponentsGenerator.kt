package io.github.smiley4.ktorswaggerui.apispec

import io.github.smiley4.ktorswaggerui.documentation.ExampleDocumentation
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.KClass

/**
 * Generator for the OpenAPI Components Object
 */
class OApiComponentsGenerator {

    /**
     * Generate the Components Object from the given config
     */
    fun generate(ctx: ComponentsContext): Components {
        return Components().apply {
            schemas = ctx.schemas
            examples = ctx.examples.mapValues {
                Example().apply {
                    value = it.value.value
                    summary = it.value.summary
                    description = it.value.description
                }
            }
        }
    }

}


data class ComponentsContext(
    val schemasInComponents: Boolean,
    val schemas: MutableMap<String, Schema<*>>,
    val examplesInComponents: Boolean,
    val examples: MutableMap<String, ExampleDocumentation>
) {

    companion object {
        val NOOP = ComponentsContext(false, mutableMapOf(), false, mutableMapOf())
    }

    fun addSchema(type: KClass<*>, schema: Schema<*>): String {
        val key = type.qualifiedName ?: "?"
        if (!schemas.containsKey(key)) {
            schemas[key] = schema
        }
        return key
    }

    fun addExample(name: String, example: ExampleDocumentation): String {
        if (examples[name] == null) {
            examples[name] = example
            return name
        } else {
            val existing = examples[name]!!
            if(existing.value == example.value && existing.description == example.description && existing.summary == example.summary) {
                return name
            } else {
                val key = name + "#" + example.hashCode().toString(16)
                examples[key] = example
                return key
            }
        }
    }

}