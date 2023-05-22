package io.github.smiley4.ktorswaggerui.spec.schema

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig
import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.SchemaType
import io.github.smiley4.ktorswaggerui.dsl.getSimpleTypeName
import io.swagger.v3.core.util.Json
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.XML
import kotlin.reflect.javaType


class JsonSchemaBuilder(
    private val pluginConfig: SwaggerUIPluginConfig,
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

    fun build(type: SchemaType): OpenApiSchemaInfo {
        return type
            .let { buildJsonSchema(it) }
            .let { build(it, type.getSimpleTypeName()) }
    }

    fun build(schema: JsonNode, typeName: String): OpenApiSchemaInfo {
        println("hello")
        return schema
            .let { processJsonSchema(it, typeName) }
            .let { buildOpenApiSchema(it) }
    }


    @OptIn(ExperimentalStdlibApi::class)
    private fun buildJsonSchema(type: SchemaType): JsonNode {
        return pluginConfig.serializationConfig.getCustomSchemaSerializer()
            .let { customSerializer -> customSerializer(type) }
            ?.let { ObjectMapper().readTree(it) }
            ?: generator.generateSchema(type.javaType)
    }

    private fun processJsonSchema(json: JsonNode, typeName: String): JsonSchemaInfo {
        if (json is ObjectNode && hasDefinitions(json)) {
            val mainDefinition = getMainDefinition(json)
            val definitions = getDefinitions(json)
            definitions.forEach { cleanupRefPaths(it.second) }
            return JsonSchemaInfo(
                rootSchema = mainDefinition,
                schemas = definitions.associate { it }
            )
        } else {
            return JsonSchemaInfo(
                rootSchema = typeName,
                schemas = mapOf(typeName to json)
            )
        }
    }

    private fun hasDefinitions(json: JsonNode) = json.get("\$defs") != null || json.get("definitions") != null

    private fun getDefinitions(json: JsonNode): List<Pair<String, JsonNode>> {
        return (json.get("\$defs") ?: json["definitions"]).fields().asSequence().map { it.key to it.value }.toList()
    }

    private fun getMainDefinition(json: JsonNode) =
        json.get("\$ref").asText()
            .replace("#/definitions/", "")
            .replace("#/\$defs/", "")

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
        println(json)
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