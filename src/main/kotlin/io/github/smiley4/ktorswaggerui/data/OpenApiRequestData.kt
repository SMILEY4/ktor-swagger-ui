package io.github.smiley4.ktorswaggerui.data

data class OpenApiRequestData(
    val parameters: List<OpenApiRequestParameterData>,
    val body: OpenApiBaseBodyData?,
)