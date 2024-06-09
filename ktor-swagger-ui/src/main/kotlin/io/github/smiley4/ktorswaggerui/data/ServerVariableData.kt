package io.github.smiley4.ktorswaggerui.data


/**
 * See [OpenAPI Specification - Server Variable Object](https://swagger.io/specification/#server-variable-object).
 */
data class ServerVariableData(
    val name: String,
    val enum: Set<String>,
    val default: String,
    val description: String?
)
