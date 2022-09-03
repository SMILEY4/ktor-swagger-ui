package io.github.smiley4.ktorswaggerui.apispec

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
 * Generator for an OpenAPI Schema Object that describes a json-object (or array)
 */
class OApiJsonSchemaGenerator {

    /**
     * Generate the Schema Object from the given class
     */
    fun generate(schema: KClass<*>, componentCtx: ComponentsContext): Schema<Any> {
        if (componentCtx.schemasInComponents) {
            val schemaObj = createSchema(schema)
            if (schemaObj.type == "array") {
                return arrayRefSchema(componentCtx.addSchema(schema.java.componentType.kotlin, schemaObj.items))
            } else {
                return refSchema(componentCtx.addSchema(schema, schemaObj))
            }
        } else {
            return createSchema(schema)
        }
    }

    private fun createSchema(schema: KClass<*>): Schema<Any> {
        return if (schema.java.isArray) {
            Schema<Any>().apply {
                type = "array"
                items = createObjectSchema(schema.java.componentType.kotlin)
            }
        } else if (schema.java.isEnum) {
            Schema<Any>().apply {
                type = "string"
                enum = schema.java.enumConstants.map { it.toString() }
            }
        } else {
            createObjectSchema(schema)
        }
    }

    private fun createObjectSchema(schema: KClass<*>): Schema<Any> {
        return if (schema.java.isEnum) {
            Schema<Any>().apply {
                type = "string"
                enum = schema.java.enumConstants.map { it.toString() }
            }
        } else {
            toSchema(generateJsonSchema(schema.java))
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
            `$ref` = "#/components/schemas/$key"
        }
    }

    private fun arrayRefSchema(key: String): Schema<Any> {
        return Schema<Any>().apply {
            type = "array"
            items = Schema<Any>().apply {
                `$ref` = "#/components/schemas/$key"
            }
        }
    }


}