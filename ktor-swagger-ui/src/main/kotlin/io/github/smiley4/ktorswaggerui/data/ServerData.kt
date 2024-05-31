package io.github.smiley4.ktorswaggerui.data

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
