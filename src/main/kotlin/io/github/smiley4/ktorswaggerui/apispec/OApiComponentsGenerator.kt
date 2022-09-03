package io.github.smiley4.ktorswaggerui.apispec

import io.swagger.v3.oas.models.Components
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
        }
    }

}


data class ComponentsContext(
    val schemasInComponents: Boolean,
    val schemas: MutableMap<String, Schema<*>>
) {

    companion object {
        val NOOP = ComponentsContext(false, mutableMapOf())
    }

    fun addSchema(type: KClass<*>, schema: Schema<*>): String {
        val key = type.qualifiedName ?: "?"
        if (!schemas.containsKey(key)) {
            schemas[key] = schema
        }
        return key;
    }

}