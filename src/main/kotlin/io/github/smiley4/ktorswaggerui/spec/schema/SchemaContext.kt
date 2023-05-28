package io.github.smiley4.ktorswaggerui.spec.schema

import io.github.smiley4.ktorswaggerui.dsl.CustomSchemaRef
import io.github.smiley4.ktorswaggerui.dsl.SchemaType
import io.github.smiley4.ktorswaggerui.dsl.getSchemaType
import io.github.smiley4.ktorswaggerui.dsl.getSimpleTypeName
import io.swagger.v3.oas.models.media.Schema
import kotlin.collections.set


class SchemaContext {

    companion object {

        private data class SchemaKeyWrapper(
            val type: SchemaType,
            val schemaId: String,
            val isCustom: Boolean
        ) {

            companion object {
                val PLACEHOLDER_TYPE = getSchemaType<Any>()
                const val PLACEHOLDER_SCHEMAID = ""

                fun type(type: SchemaType) = SchemaKeyWrapper(
                    type = type,
                    schemaId = PLACEHOLDER_SCHEMAID,
                    isCustom = false
                )

                fun custom(schemaId: String) = SchemaKeyWrapper(
                    type = PLACEHOLDER_TYPE,
                    schemaId = schemaId,
                    isCustom = true
                )
            }
        }
    }

    private val schemas = mutableMapOf<SchemaType, SchemaDefinitions>()
    private val schemasCustom = mutableMapOf<String, SchemaDefinitions>()

    private val componentsSection = mutableMapOf<String, Schema<*>>()
    private val inlineSchemas = mutableMapOf<SchemaType, Schema<*>>()
    private val inlineSchemasCustom = mutableMapOf<String, Schema<*>>()


    fun addSchema(type: SchemaType, schema: SchemaDefinitions) {
        schemas[type] = schema
    }


    fun addSchema(ref: CustomSchemaRef, schema: SchemaDefinitions) {
        schemasCustom[ref.schemaId] = schema
    }


    fun getComponentsSection(): Map<String, Schema<*>> = componentsSection


    fun getSchema(type: SchemaType) = inlineSchemas[type] ?: throw Exception("No schema for type '$type'!")


    fun getSchema(ref: CustomSchemaRef) = inlineSchemasCustom[ref.schemaId] ?: throw Exception("No schema for ref '$ref'!")


    fun finalize() {
        schemas.forEach { (type, schemaDefinitions) ->
            finalize(SchemaKeyWrapper.type(type), schemaDefinitions)
        }
        schemasCustom.forEach { (schemaId, schemaDefinitions) ->
            finalize(SchemaKeyWrapper.custom(schemaId), schemaDefinitions)
        }
    }

    private fun finalize(key: SchemaKeyWrapper, schemaDefinitions: SchemaDefinitions) {
        if (schemaDefinitions.definitions.isEmpty()) {
            finalizeOnlyRootDefinition(key, schemaDefinitions)
        }
        if (schemaDefinitions.definitions.size == 1) {
            finalizeOneAdditionalDefinition(key, schemaDefinitions)
        }
        if (schemaDefinitions.definitions.size > 1) {
            finalizeMultipleAdditionalDefinitions(key, schemaDefinitions)
        }
    }

    private fun finalizeOnlyRootDefinition(key: SchemaKeyWrapper, schemaDefinitions: SchemaDefinitions) {
        val root = schemaDefinitions.root
        if (root.isPrimitive() || root.isPrimitiveArray()) {
            inlineRoot(key, schemaDefinitions)
        } else if (root.isObjectArray()) {
            unwrapRootArray(key, schemaDefinitions)
        } else {
            createInlineReference(key, schemaDefinitions)
        }
    }

    private fun finalizeOneAdditionalDefinition(key: SchemaKeyWrapper, schemaDefinitions: SchemaDefinitions) {
        val root = schemaDefinitions.root
        val definition = schemaDefinitions.definitions.entries.first().value
        if (root.isReference() && (definition.isPrimitive() || definition.isPrimitiveArray())) {
            inlineSingleDefinition(key, schemaDefinitions)
        } else if (root.isReference() || root.isReferenceArray()) {
            inlineRoot(key, schemaDefinitions)
        } else if (root.isObjectArray()) {
            unwrapRootArray(key, schemaDefinitions)
        } else {
            createInlineReference(key, schemaDefinitions)
        }
    }

    private fun finalizeMultipleAdditionalDefinitions(key: SchemaKeyWrapper, schemaDefinitions: SchemaDefinitions) {
        val root = schemaDefinitions.root
        if (root.isReference() || root.isReferenceArray()) {
            inlineRoot(key, schemaDefinitions)
        } else if (root.isObjectArray()) {
            unwrapRootArray(key, schemaDefinitions)
        } else {
            createInlineReference(key, schemaDefinitions)
        }
    }


    private fun inlineRoot(key: SchemaKeyWrapper, schemaDefinitions: SchemaDefinitions) {
        /*
        - root-schema: inline
        - definitions: in components section
         */
        addInline(key, schemaDefinitions.root)
        schemaDefinitions.definitions.forEach { (name, schema) ->
            addToComponentsSection(name, schema)
        }
    }

    private fun inlineSingleDefinition(key: SchemaKeyWrapper, schemaDefinitions: SchemaDefinitions) {
        /*
        - assumption: size(definitions) == 1
        - root-schema: discard
        - definition:  inline
         */
        if (schemaDefinitions.definitions.size != 1) {
            throw Exception("Unexpected amount of additional schema-definitions: ${schemaDefinitions.definitions.size}")
        }
        schemaDefinitions.definitions.entries.first()
            .also { addInline(key, it.value) }
    }

    private fun createInlineReference(key: SchemaKeyWrapper, schemaDefinitions: SchemaDefinitions) {
        /*
        - root-schema: in components section
        - definitions: in components section
        - create inline ref to root
         */
        schemaDefinitions.definitions.forEach { (name, schema) ->
            addToComponentsSection(name, schema)
        }
        val rootName = schemaName(key)
        addToComponentsSection(rootName, schemaDefinitions.root)
        addInline(key, Schema<Any>().also {
            it.`$ref` = "#/components/schemas/$rootName"
        })
    }

    private fun unwrapRootArray(key: SchemaKeyWrapper, schemaDefinitions: SchemaDefinitions) {
        /*
        - assumption: root schema.type == array
        - root-schema: unwrap
            - item -> component section
            - create inline array-ref to item
        - definitions: in components section
         */
        if (schemaDefinitions.root.items == null) {
            throw Exception("Expected items for array-schema but items were 'null'.")
        }
        schemaDefinitions.definitions.forEach { (name, schema) ->
            addToComponentsSection(name, schema)
        }
        val rootName = schemaName(key)
        addToComponentsSection(rootName, schemaDefinitions.root.items)
        addInline(key, Schema<Any>().also { array ->
            array.type = "array"
            array.items = Schema<Any>().also { item ->
                item.`$ref` = "#/components/schemas/$rootName"
            }
        })
    }

    private fun schemaName(key: SchemaKeyWrapper): String {
        return if (key.isCustom) {
            key.schemaId
        } else {
            key.type.getSimpleTypeName()
        }
    }

    private fun addToComponentsSection(name: String, schema: Schema<*>) {
        componentsSection[name] = schema
    }

    private fun addInline(key: SchemaKeyWrapper, schema: Schema<*>) {
        if (key.isCustom) {
            inlineSchemasCustom[key.schemaId] = schema
        } else {
            inlineSchemas[key.type] = schema
        }
    }

    private fun Schema<*>.isPrimitive(): Boolean {
        return type != "object" && type != "array" && type != null
    }

    private fun Schema<*>.isPrimitiveArray(): Boolean {
        return type == "array" && (items.isPrimitive() || items.isPrimitiveArray())
    }

    private fun Schema<*>.isObjectArray(): Boolean {
        return type == "array" && !items.isPrimitive() && !items.isPrimitiveArray()
    }

    private fun Schema<*>.isReference(): Boolean {
        return type == null && `$ref` != null
    }

    private fun Schema<*>.isReferenceArray(): Boolean {
        return type == "array" && items.isReference()
    }

}