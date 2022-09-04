package io.github.smiley4.ktorswaggerui.specbuilder

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.victools.jsonschema.generator.Option
import com.github.victools.jsonschema.generator.OptionPreset
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder
import com.github.victools.jsonschema.generator.SchemaVersion
import com.github.victools.jsonschema.module.jackson.JacksonModule
import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.KClass

/**
 * Builder for an OpenAPI Schema Object that describes a json-object (or array)
 */
class OApiJsonSchemaBuilder {

    fun build(type: KClass<*>, components: ComponentsContext): Schema<Any> {
        if (components.schemasInComponents) {
            val schema = createSchema(type)
            if (schema.type == "array") {
                return arrayRefSchema(components.addSchema(type.java.componentType.kotlin, schema.items))
            } else {
                return refSchema(components.addSchema(type, schema))
            }
        } else {
            return createSchema(type)
        }
    }


    private fun createSchema(type: KClass<*>): Schema<Any> {
        return if (type.java.isArray) {
            Schema<Any>().apply {
                this.type = "array"
                this.items = createObjectSchema(type.java.componentType.kotlin)
            }
        } else if (type.java.isEnum) {
            Schema<Any>().apply {
                this.type = "string"
                this.enum = type.java.enumConstants.map { it.toString() }
            }
        } else {
            createObjectSchema(type)
        }
    }


    private fun createObjectSchema(type: KClass<*>): Schema<Any> {
        return if (type.java.isEnum) {
            Schema<Any>().apply {
                this.type = "string"
                this.enum = type.java.enumConstants.map { it.toString() }
            }
        } else {
            toSchema(generateJsonSchema(type.java))
        }
    }


    private fun toSchema(node: JsonNode): Schema<Any> {
        return Schema<Any>().apply {
            node["\$schema"]?.let { this.`$schema` = it.asText() }
            node["type"]?.let { this.type = it.asText() }
            node["items"]?.let { this.items = toSchema(it) }
            node["properties"]?.let { this.properties = it.collectFields().associate { prop -> prop.key to toSchema(prop.value) } }
            node["allOf"]?.let { this.allOf = it.collectElements().map { prop -> toSchema(prop) } }
            node["anyOf"]?.let { this.anyOf = it.collectElements().map { prop -> toSchema(prop) } }
            node["required"]?.let { this.required = it.collectElements().map { prop -> prop.asText() } }
            node["const"]?.let { this.setConst(it.asText()) }
            node["\$defs"]?.let { throw UnsupportedOperationException("'\"defs' in json-schema are not supported") }
            node["\$ref"]?.let { throw UnsupportedOperationException("'\"refs' in json-schema are not supported") }
        }
    }


    private fun JsonNode.collectFields(): List<MutableMap.MutableEntry<String, JsonNode>> {
        return this.fields().asSequence().toList()
    }


    private fun JsonNode.collectElements(): List<JsonNode> {
        return this.elements().asSequence().toList()
    }


    private fun <T> generateJsonSchema(type: Class<T>): ObjectNode {
        val config = SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON)
            .with(JacksonModule())
            .without(Option.DEFINITIONS_FOR_ALL_OBJECTS)
            .with(Option.INLINE_ALL_SCHEMAS)
            .with(Option.ALLOF_CLEANUP_AT_THE_END)
            .build()
        return SchemaGenerator(config).generateSchema(type)
    }


    private fun refSchema(key: String): Schema<Any> {
        return Schema<Any>().apply {
            `$ref` = key
        }
    }


    private fun arrayRefSchema(key: String): Schema<Any> {
        return Schema<Any>().apply {
            type = "array"
            items = Schema<Any>().apply {
                `$ref` = key
            }
        }
    }

}
