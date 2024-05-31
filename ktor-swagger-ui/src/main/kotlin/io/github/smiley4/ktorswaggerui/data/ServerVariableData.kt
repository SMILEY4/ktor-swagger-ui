package io.github.smiley4.ktorswaggerui.data

data class ServerVariableData(
    val name: String,
    val enum: Set<String>,
    val default: String,
    val description: String?
)
