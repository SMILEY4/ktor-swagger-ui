package io.github.smiley4.ktorswaggerui.data

data class OpenApiRouteData(
    val specId: String?,
    val tags: List<String>,
    val summary: String?,
    val description: String?,
    val operationId: String?,
    val deprecated: Boolean,
    val hidden: Boolean,
    val securitySchemeNames: List<String>,
    val protected: Boolean?,
    val request: OpenApiRequestData,
    val responses: List<OpenApiResponseData>,
)
