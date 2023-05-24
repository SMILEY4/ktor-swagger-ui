package io.github.smiley4.ktorswaggerui.spec.schemaV2

import io.github.smiley4.ktorswaggerui.dsl.CustomSchemaRef
import io.github.smiley4.ktorswaggerui.dsl.SchemaType
import io.swagger.v3.oas.models.media.Schema

class SchemaContext {

    private val schemas = mutableMapOf<SchemaType, SchemaDefinitions>()
    private val schemasCustom = mutableMapOf<CustomSchemaRef, SchemaDefinitions>()

    private val componentsSection = mutableMapOf<String, Schema<*>>()
    private val inlineSchemas = mutableMapOf<SchemaType, Schema<*>>()
    private val inlineSchemasCustom = mutableMapOf<CustomSchemaRef, Schema<*>>()


    fun addSchema(type: SchemaType, schema: SchemaDefinitions) {
        schemas[type] = schema
    }


    fun addSchema(ref: CustomSchemaRef, schema: SchemaDefinitions) {
        schemasCustom[ref] = schema
    }


    fun getComponentsSection(): Map<String, Schema<*>> = componentsSection


    fun getSchema(type: SchemaType) = inlineSchemas[type] ?: throw Exception("No schema for type '$type'!")


    fun getSchema(ref: CustomSchemaRef) = inlineSchemasCustom[ref] ?: throw Exception("No schema for ref '$ref'!")


    fun finalize() {
        schemas.forEach { (type, schemaDefinitions) ->
            // only root definition
            if (schemaDefinitions.definitions.isEmpty()) {
                val root = schemaDefinitions.root
                if (root.isPrimitive() || root.isPrimitiveArray()) {
                    inlineRoot(schemaDefinitions)
                } else if (root.isObjectArray()) {
                    unwrapRootArray(schemaDefinitions)
                } else {
                    createInlineReference(schemaDefinitions)
                }
            }
            // only one additional definition
            if (schemaDefinitions.definitions.size == 1) {
                val root = schemaDefinitions.root
                val definition = schemaDefinitions.definitions.entries.first().value
                if (root.isReference() && (definition.isPrimitive() || definition.isPrimitiveArray())) {
                    inlineDefinition(schemaDefinitions)
                } else if (root.isReference() || root.isReferenceArray()) {
                    inlineRoot(schemaDefinitions)
                } else if (root.isObjectArray()) {
                    unwrapRootArray(schemaDefinitions)
                } else {
                    createInlineReference(schemaDefinitions)
                }
            }
            // multiple additional definitions
            if (schemaDefinitions.definitions.size > 1) {
                val root = schemaDefinitions.root
                if (root.isReference() || root.isReferenceArray()) {
                    inlineRoot(schemaDefinitions)
                } else if (root.isObjectArray()) {
                    unwrapRootArray(schemaDefinitions)
                } else {
                    createInlineReference(schemaDefinitions)
                }
            }
        }
    }

    private fun inlineRoot(schemaDefinitions: SchemaDefinitions) {
        /*
        - root-schema: inline
        - definitions: in components section
         */
        addInline(TODO(), schemaDefinitions.root)
        schemaDefinitions.definitions.forEach { (name, schema) ->
            addToComponentsSection(name, schema)
        }
    }

    private fun inlineDefinition(schemaDefinitions: SchemaDefinitions) {
        /*
        - assumption: size(definitions) == 1
        - root-schema: discard
        - definition:  inline
         */
        if(schemaDefinitions.definitions.size != 1) {
            throw Exception("Unexpected amount of additional schema-definitions: ${schemaDefinitions.definitions.size}")
        }
        schemaDefinitions.definitions.entries.first()
            .also { addInline(TODO(), it.value) }
    }

    private fun createInlineReference(schemaDefinitions: SchemaDefinitions) {
        /*
        - root-schema: in components section
        - definitions: in components section
        - create inline ref to root
         */
        schemaDefinitions.definitions.forEach { (name, schema) ->
            addToComponentsSection(name, schema)
        }
        addToComponentsSection(TODO(), schemaDefinitions.root)
        TODO("create inline ref to root")
    }

    private fun unwrapRootArray(schemaDefinitions: SchemaDefinitions) {
        /*
        - assumption: root schema.type == array
        - root-schema: unwrap
            - item -> component section
            - create inline array-ref to item
        - definitions: in components section
         */
        if(schemaDefinitions.root.items == null) {
            throw Exception("Expected items for array-schema but items were 'null'.")
        }
        schemaDefinitions.definitions.forEach { (name, schema) ->
            addToComponentsSection(name, schema)
        }
        addToComponentsSection(TODO(), schemaDefinitions.root.items)
        addInline(TODO(), TODO("array-ref to item"))
    }


    private fun addToComponentsSection(name: String, schema: Schema<*>) {
        componentsSection[name] = schema
    }

    private fun addInline(type: SchemaType, schema: Schema<*>) {
        inlineSchemas[type] = schema
    }

    private fun addInline(ref: CustomSchemaRef, schema: Schema<*>) {
        inlineSchemasCustom[ref] = schema
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