package de.lruegner.ktorswaggerui.routing

object SchemaRef {

    fun ofClass(schema: Class<*>) = "schemas/" + schema.typeName.replace(".", "__")

}