package io.github.smiley4.ktorswaggerui.spec.schema

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.BooleanNode
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

    private fun normalizeRefs(jsonSchema: JsonNode, normalizer: (ref: String) -> String) {
        iterateTree(jsonSchema) { node ->
            if (node is ObjectNode) {
                node.get("\$ref")?.also {
                    node.set<ObjectNode>("\$ref", TextNode(normalizer(it.asText())))
                }
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
        iterateTree(jsonSchema) { node ->
            node.get("type")?.also { typeNode ->
                if (typeNode is ArrayNode && node is ObjectNode) {
                    val types = typeNode.asSequence().filterIsInstance<TextNode>().map { it.asText() }.toSet()
                    node.set<ObjectNode>("type", TextNode(types.first { it != "null" }))
                    if(types.contains("null")){
                        node.set<ObjectNode>("nullable", BooleanNode.TRUE)
                    }
                }
            }
        }
        return Json.mapper().readValue(jsonSchema.toString(), Schema::class.java)
    }


    private fun iterateTree(node: JsonNode, consumer: (node: JsonNode) -> Unit) {
        consumer(node)
        when (node) {
            is ObjectNode -> {
                node.elements().asSequence().forEach { iterateTree(it, consumer) }
            }
            is ArrayNode -> {
                node.elements().asSequence().forEach { iterateTree(it, consumer) }
            }
        }
    }

}