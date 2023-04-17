package io.github.smiley4.ktorswaggerui.specbuilder

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import io.swagger.v3.oas.models.media.Schema
import java.math.BigDecimal

class JsonToOpenApiSchemaConverter {

    fun toSchema(json: String) = toSchema(ObjectMapper().readTree(json))

    fun toSchema(node: JsonNode): Schema<Any> {
        return Schema<Any>().apply {
            node["\$schema"]?.let { this.`$schema` = it.asText() }
            node["description"]?.let { this.description = it.asText() }
            node["title"]?.let { this.title = it.asText() }
            node["example"]?.let { this.example(it.asText()) }
            node["type"]?.let {
                val types = if (it is ArrayNode) it.collectElements().map { e -> e.asText() } else listOf(it.asText())
                this.type = types.firstOrNull { e -> e != "null" }
                if (types.contains("null")) {
                    this.nullable = true
                }
            }
            node["deprecated"]?.let { this.deprecated = it.asBoolean() }
            node["minLength"]?.let { this.minLength = it.asInt() }
            node["maxLength"]?.let { this.maxLength = it.asInt() }
            node["minimum"]?.let { this.minimum = BigDecimal(it.asText()) }
            node["maximum"]?.let { this.maximum = BigDecimal(it.asText()) }
            node["maxItems"]?.let { this.maxItems = it.asInt() }
            node["minItems"]?.let { this.minItems = it.asInt() }
            node["uniqueItems"]?.let { this.uniqueItems = it.asBoolean() }
            node["format"]?.let { this.format = it.asText() }
            node["items"]?.let { this.items = toSchema(it) }
            node["properties"]?.let { this.properties = it.collectFields().associate { prop -> prop.key to toSchema(prop.value) } }
            node["additionalProperties"]?.let { this.additionalProperties = toSchema(it) }
            node["allOf"]?.let { this.allOf = it.collectElements().map { prop -> toSchema(prop) } }
            node["anyOf"]?.let { this.anyOf = it.collectElements().map { prop -> toSchema(prop) } }
            node["required"]?.let { this.required = it.collectElements().map { prop -> prop.asText() } }
            node["enum"]?.let { this.enum = it.collectElements().map { prop -> prop.asText() } }
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

}
