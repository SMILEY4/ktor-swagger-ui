package io.github.smiley4.ktorswaggerui.spec.schema

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig
import io.swagger.v3.core.util.Json
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.XML
import java.lang.reflect.Type


class JsonSchemaBuilder(
    schemaGeneratorConfig: SchemaGeneratorConfig
) {

    companion object {

        data class JsonSchemaInfo(
            val rootSchema: String,
            val schemas: Map<String, JsonNode>
        )

        data class OpenApiSchemaInfo(
            val rootSchema: String,
            val schemas: Map<String, Schema<*>>
        )

    }

    private val generator = SchemaGenerator(schemaGeneratorConfig)

    fun build(type: Type): OpenApiSchemaInfo {
        return type
            .let { buildJsonSchema(it) }
            .let { build(it) }
    }

    fun build(schema: JsonNode): OpenApiSchemaInfo {
        return schema
            .let { processJsonSchema(it) }
            .let { buildOpenApiSchema(it) }
    }

    private fun buildJsonSchema(type: Type): JsonNode {
        return generator.generateSchema(type)
    }

    private fun processJsonSchema(json: JsonNode): JsonSchemaInfo {
        if (json is ObjectNode && json.get("\$defs") != null) {
            val mainDefinition = json.get("\$ref").asText().replace("#/\$defs/", "")
            val definitions = json.get("\$defs").fields().asSequence().map { it.key to it.value }.toList()
            definitions.forEach { cleanupRefPaths(it.second) }
            return JsonSchemaInfo(
                rootSchema = mainDefinition,
                schemas = definitions.associate { it }
            )
        } else {
            return JsonSchemaInfo(
                rootSchema = "root",
                schemas = mapOf("root" to json)
            )
        }
    }

    private fun cleanupRefPaths(node: JsonNode) {
        when (node) {
            is ObjectNode -> {
                node.get("\$ref")?.also {
                    node.set<ObjectNode>("\$ref", TextNode(it.asText().replace("#/\$defs/", "")))
                }
                node.elements().asSequence().forEach { cleanupRefPaths(it) }
            }

            is ArrayNode -> {
                node.elements().asSequence().forEach { cleanupRefPaths(it) }
            }
        }
    }

    private fun buildOpenApiSchema(json: JsonSchemaInfo): OpenApiSchemaInfo {
        return OpenApiSchemaInfo(
            rootSchema = json.rootSchema,
            schemas = json.schemas.mapValues { (name, schema) -> buildOpenApiSchema(schema, name) }
        )
    }

    private fun buildOpenApiSchema(json: JsonNode, name: String): Schema<*> {
        return Json.mapper().readValue(json.toString(), Schema::class.java).also { schema ->
            schema.xml = XML().also {
                it.name = name
            }
        }
    }

}