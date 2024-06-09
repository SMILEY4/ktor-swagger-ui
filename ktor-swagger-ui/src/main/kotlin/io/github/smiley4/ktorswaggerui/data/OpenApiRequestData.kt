package io.github.smiley4.ktorswaggerui.data

/**
 * Information about a request
 */
data class OpenApiRequestData(
    val parameters: List<OpenApiRequestParameterData>,
    val body: OpenApiBaseBodyData?,
)
