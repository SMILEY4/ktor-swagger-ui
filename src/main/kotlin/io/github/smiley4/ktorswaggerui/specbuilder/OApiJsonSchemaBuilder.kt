package io.github.smiley4.ktorswaggerui.specbuilder

import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.victools.jsonschema.generator.Option
import com.github.victools.jsonschema.generator.OptionPreset
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder
import com.github.victools.jsonschema.generator.SchemaVersion
import com.github.victools.jsonschema.module.jackson.JacksonModule
import io.swagger.v3.oas.models.media.Schema
import java.lang.reflect.Type

/**
 * Builder for an OpenAPI Schema Object that describes a json-object (or array)
 */
class OApiJsonSchemaBuilder {

    private val jsonToSchemaConverter = JsonToOpenApiSchemaConverter()

    fun build(type: Type, components: ComponentsContext): Schema<Any> {
        if (components.schemasInComponents) {
            val schema = createSchema(type)
            if (schema.type == "array") {
                return components.addArraySchema(type, schema)
            } else {
                return components.addSchema(type, schema)
            }
        } else {
            return createSchema(type)
        }
    }


    private fun createSchema(type: Type): Schema<Any> {
        return if (type is Class<*> && type.isArray) {
            Schema<Any>().apply {
                this.type = "array"
                this.items = createObjectSchema(type.componentType)
            }
        } else if (type is Class<*> && type.isEnum) {
            Schema<Any>().apply {
                this.type = "string"
                this.enum = type.enumConstants.map { it.toString() }
            }
        } else {
            return createObjectSchema(type)
        }
    }


    private fun createObjectSchema(type: Type): Schema<Any> {
        return if (type is Class<*> && type.isEnum) {
            Schema<Any>().apply {
                this.type = "string"
                this.enum = type.enumConstants.map { it.toString() }
            }
        } else {
            return jsonToSchemaConverter.toSchema(generateJsonSchema(type))
        }
    }


    private fun generateJsonSchema(type: Type): ObjectNode {
        val config = SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON)
            .with(JacksonModule())
            .without(Option.DEFINITIONS_FOR_ALL_OBJECTS)
            .with(Option.INLINE_ALL_SCHEMAS)
            .with(Option.EXTRA_OPEN_API_FORMAT_VALUES)
            .with(Option.ALLOF_CLEANUP_AT_THE_END)
            .build()
        return SchemaGenerator(config).generateSchema(type)
    }

}
