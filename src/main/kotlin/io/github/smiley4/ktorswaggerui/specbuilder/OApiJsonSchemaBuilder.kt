package io.github.smiley4.ktorswaggerui.specbuilder

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.victools.jsonschema.generator.SchemaGenerator
import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.swagger.v3.oas.models.media.Schema
import java.lang.reflect.Type

/**
 * Builder for an OpenAPI Schema Object that describes a json-object (or array)
 */
class OApiJsonSchemaBuilder {

    private val jsonToSchemaConverter = JsonToOpenApiSchemaConverter()

    fun build(type: Type, components: ComponentsContext, config: SwaggerUIPluginConfig): Schema<Any> {
        if (components.schemasInComponents) {
            val schema = createSchema(type, config)
            if (schema.type == "array") {
                return components.addArraySchema(type, schema)
            } else {
                return components.addSchema(type, schema)
            }
        } else {
            return createSchema(type, config)
        }
    }


    private fun createSchema(type: Type, config: SwaggerUIPluginConfig): Schema<Any> {
        return if (type is Class<*> && type.isArray) {
            Schema<Any>().apply {
                this.type = "array"
                this.items = createObjectSchema(type.componentType, config)
            }
        } else if (type is Class<*> && type.isEnum) {
            Schema<Any>().apply {
                this.type = "string"
                this.enum = type.enumConstants.map { it.toString() }
            }
        } else {
            return createObjectSchema(type, config)
        }
    }


    private fun createObjectSchema(type: Type, config: SwaggerUIPluginConfig): Schema<Any> {
        return if (type is Class<*> && type.isEnum) {
            Schema<Any>().apply {
                this.type = "string"
                this.enum = type.enumConstants.map { it.toString() }
            }
        } else {
            val jsonSchema = createObjectJsonSchema(type, config)
            return jsonToSchemaConverter.toSchema(jsonSchema)
        }
    }

    private fun createObjectJsonSchema(type: Type, config: SwaggerUIPluginConfig): ObjectNode {
        if (config.getCustomSchemas().getJsonSchemaBuilder() != null) {
            val jsonSchema = config.getCustomSchemas().getJsonSchemaBuilder()?.let { it(type) }
            if (jsonSchema != null) {
                return ObjectMapper().readTree(jsonSchema) as ObjectNode
            }
        }
        return generateJsonSchema(type, config)
    }

    private fun generateJsonSchema(type: Type, config: SwaggerUIPluginConfig): ObjectNode {
        val generatorConfig = config.schemaGeneratorConfigBuilder.build()
        return SchemaGenerator(generatorConfig).generateSchema(type)
    }

}
