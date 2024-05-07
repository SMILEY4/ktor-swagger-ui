package io.github.smiley4.ktorswaggerui.data


data class SchemaConfigData(
    val schemas: Map<String, TypeDescriptor>
) {
    companion object {
        val DEFAULT = SchemaConfigData(
            schemas = emptyMap()
        )
    }
}