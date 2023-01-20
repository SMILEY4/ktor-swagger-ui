package io.github.smiley4.ktorswaggerui.specbuilder

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.models.media.Schema

class JsonToOpenApiSchemaConverter {

    fun toSchema(json: String) = toSchema(ObjectMapper().readTree(json))


    fun toSchema(node: JsonNode): Schema<Any> {
        return Schema<Any>().apply {
            node["\$schema"]?.let { this.`$schema` = it.asText() }
            node["type"]?.let { this.type = it.asText() }
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
