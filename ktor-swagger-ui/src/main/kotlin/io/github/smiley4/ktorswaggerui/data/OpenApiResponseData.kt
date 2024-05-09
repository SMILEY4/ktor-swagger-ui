package io.github.smiley4.ktorswaggerui.data

data class OpenApiResponseData(
    val statusCode: String,
    val description: String?,
    val headers: Map<String, OpenApiHeaderData>,
    val body: OpenApiBaseBodyData?,
)
