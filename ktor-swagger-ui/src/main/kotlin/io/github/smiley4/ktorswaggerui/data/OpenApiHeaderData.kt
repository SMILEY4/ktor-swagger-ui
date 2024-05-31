package io.github.smiley4.ktorswaggerui.data

data class OpenApiHeaderData(
    val description: String?,
    val type: TypeDescriptor?,
    val required: Boolean,
    val deprecated: Boolean,
    val explode: Boolean?,
)
