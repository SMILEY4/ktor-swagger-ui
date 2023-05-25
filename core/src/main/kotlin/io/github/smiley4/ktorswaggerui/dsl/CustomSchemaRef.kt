package io.github.smiley4.ktorswaggerui.dsl

sealed class CustomSchemaRef(
	val schemaId: String
)

class CustomObjectSchemaRef(schemaId: String) : CustomSchemaRef(schemaId)

class CustomArraySchemaRef(schemaId: String) : CustomSchemaRef(schemaId)

fun obj(schemaId: String) = CustomObjectSchemaRef(schemaId)

fun array(schemaId: String) = CustomArraySchemaRef(schemaId)