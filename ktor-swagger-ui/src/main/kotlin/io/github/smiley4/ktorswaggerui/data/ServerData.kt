package io.github.smiley4.ktorswaggerui.data

/**
 * See [OpenAPI Specification - Server Object](https://swagger.io/specification/#server-object).
 */
data class ServerData(
    val url: String,
    val description: String?,
    val variables: List<ServerVariableData>
) {

    companion object {
        val DEFAULT = ServerData(
            url = "/",
            description = null,
            variables = emptyList()
        )
    }

}
