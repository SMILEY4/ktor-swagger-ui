package io.github.smiley4.ktorswaggerui.spec.schema

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.github.victools.jsonschema.generator.*
import com.github.victools.jsonschema.module.jackson.JacksonModule
import com.github.victools.jsonschema.module.swagger2.Swagger2Module
import io.swagger.v3.oas.models.media.Schema
import java.lang.reflect.Type


class JsonSchemaBuilder {

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

    private val generator = SchemaGenerator(
        SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON)
            .with(JacksonModule())
            .with(Swagger2Module())
            .with(Option.EXTRA_OPEN_API_FORMAT_VALUES)
            .with(Option.ALLOF_CLEANUP_AT_THE_END)
            .with(Option.MAP_VALUES_AS_ADDITIONAL_PROPERTIES)
            .with(Option.DEFINITIONS_FOR_ALL_OBJECTS)
            .with(Option.DEFINITION_FOR_MAIN_SCHEMA)
            .without(Option.INLINE_ALL_SCHEMAS)
            .build()
    )

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
                    println(it)
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
            schemas = json.schemas.mapValues { (_, schema) -> buildOpenApiSchema(schema) }
        )
    }

    private fun buildOpenApiSchema(json: JsonNode): Schema<*> {
        return ObjectMapper().readValue(json.toString(), Schema::class.java)
    }

}