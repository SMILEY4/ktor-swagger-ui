package io.github.smiley4.ktorswaggerui.spec.schema

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import io.github.smiley4.ktorswaggerui.dsl.SchemaEncoder
import io.github.smiley4.ktorswaggerui.dsl.SchemaType
import io.swagger.v3.core.util.Json
import io.swagger.v3.oas.models.media.Schema


data class SchemaDefinitions(
    val root: Schema<*>,
    val definitions: Map<String, Schema<*>>
)

class SchemaBuilder(
    private val definitionsField: String? = null,
    private val schemaEncoder: SchemaEncoder
) {


    fun create(type: SchemaType): SchemaDefinitions {
        return create(createJsonSchema(type))
    }

    fun create(jsonSchema: String): SchemaDefinitions {
        return create(ObjectMapper().readTree(jsonSchema))
    }

    fun create(jsonSchema: JsonNode): SchemaDefinitions {
        normalizeRefs(jsonSchema) { normalizeRef(it) }
        val additionalDefinitions = extractAdditionalDefinitions(jsonSchema)
        val rootSchema = toOpenApiSchema(jsonSchema)
        val additionalSchemas = additionalDefinitions.mapValues { toOpenApiSchema(it.value) }
        return SchemaDefinitions(
            root = rootSchema,
            definitions = additionalSchemas
        )
    }

    private fun createJsonSchema(type: SchemaType): JsonNode {
        val str = schemaEncoder(type)
        return ObjectMapper().readTree(str)
    }

    private fun normalizeRefs(node: JsonNode, normalizer: (ref: String) -> String) {
        when (node) {
            is ObjectNode -> {
                node.get("\$ref")?.also {
                    node.set<ObjectNode>("\$ref", TextNode(normalizer(it.asText())))
                }
                node.elements().asSequence().forEach { normalizeRefs(it, normalizer) }
            }

            is ArrayNode -> {
                node.elements().asSequence().forEach { normalizeRefs(it, normalizer) }
            }
        }
    }

    private fun normalizeRef(ref: String): String {
        val prefix = "#/$definitionsField"
        return if (ref.startsWith(prefix)) {
            ref.replace(prefix, "#/components/schemas")
        } else {
            ref
        }
    }

    private fun extractAdditionalDefinitions(schema: JsonNode): Map<String, JsonNode> {
        val definitionsPath = definitionsField?.let { "/$definitionsField" }
        return if (definitionsPath == null || schema.at(definitionsPath).isMissingNode) {
            emptyMap()
        } else {
            (schema.at(definitionsPath) as ObjectNode)
                .fields().asSequence().toList()
                .associate { (key, node) -> key to node }
        }
    }

    private fun toOpenApiSchema(jsonSchema: JsonNode): Schema<*> {
        return Json.mapper().readValue(jsonSchema.toString(), Schema::class.java)
    }

}