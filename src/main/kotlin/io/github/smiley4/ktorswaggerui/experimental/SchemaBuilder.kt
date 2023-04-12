package io.github.smiley4.ktorswaggerui.experimental

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.github.victools.jsonschema.generator.Option
import com.github.victools.jsonschema.generator.OptionPreset
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder
import com.github.victools.jsonschema.generator.SchemaVersion
import com.github.victools.jsonschema.module.jackson.JacksonModule
import com.github.victools.jsonschema.module.swagger2.Swagger2Module
import io.swagger.v3.oas.models.media.Schema
import java.lang.reflect.Type


class SchemaBuilder {

    companion object {

        private data class JsonSchema(
            val rootSchema: String,
            val schemas: Map<String, JsonNode>
        )

    }

    private val generator = SchemaGenerator(
        SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON)
            .with(JacksonModule())
            .with(Swagger2Module())
            .without(Option.DEFINITIONS_FOR_ALL_OBJECTS)
            .with(Option.INLINE_ALL_SCHEMAS)
            .with(Option.EXTRA_OPEN_API_FORMAT_VALUES)
            .with(Option.ALLOF_CLEANUP_AT_THE_END)
            .with(Option.MAP_VALUES_AS_ADDITIONAL_PROPERTIES)

            .with(Option.DEFINITIONS_FOR_ALL_OBJECTS)
            .with(Option.DEFINITION_FOR_MAIN_SCHEMA)
            .without(Option.INLINE_ALL_SCHEMAS)

            .build()
    )

    fun build(type: Type): Schema<*> {
        return type
            .let { buildJsonSchema(it) }
            .let { processJsonSchema(it) }
            .let { buildOpenApiSchema(it) }
    }

    private fun buildJsonSchema(type: Type): JsonNode {
        return generator.generateSchema(type)
    }

    private fun processJsonSchema(json: JsonNode): JsonSchema {
        if (json is ObjectNode && json.get("\$defs") != null) {
            val mainDefinition = json.get("\$ref").asText().replace("#/\$defs/", "")
            val definitions = json.get("\$defs").fields().asSequence().map { it.key to it.value }.toList()
            definitions.forEach { cleanupRefPaths(it.second) }
            return JsonSchema(
                rootSchema = mainDefinition,
                schemas = definitions.associate { it }
            )
        } else {
            return JsonSchema(
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

    private fun buildOpenApiSchema(json: JsonSchema): Schema<*> {
        // TODO: handle multiple schema-definitions
        return ObjectMapper().readValue(json.schemas[json.rootSchema].toString(), Schema::class.java)
    }


}