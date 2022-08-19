package io.github.smiley4.ktorswaggerui.routing

object SchemaRef {

    fun refOfClass(schema: Class<*>) = "schemas/" + schema.typeName.replace(".", "__")

    fun refToClassName(ref: String) = ref.replace("__", ".")

}