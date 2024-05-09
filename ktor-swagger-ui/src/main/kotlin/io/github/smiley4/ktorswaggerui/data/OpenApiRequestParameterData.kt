package io.github.smiley4.ktorswaggerui.data

data class OpenApiRequestParameterData(
    val name: String,
    val type: TypeDescriptor,
    val location: ParameterLocation,
    val description: String?,
    val example: ExampleDescriptor?,
    val required: Boolean,
    val deprecated: Boolean,
    val allowEmptyValue: Boolean,
    val explode: Boolean,
    val allowReserved: Boolean
)
