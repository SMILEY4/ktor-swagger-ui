package io.github.smiley4.ktorswaggerui.data

/**
 * See [OpenAPI Specification - Header Object](https://swagger.io/specification/#header-object).
 */
data class OpenApiHeaderData(
    val description: String?,
    val type: TypeDescriptor?,
    val required: Boolean,
    val deprecated: Boolean,
    val explode: Boolean?,
)
