package io.github.smiley4.ktorswaggerui.data

data class ServerData(
    val url: String,
    val description: String?,
) {

    companion object {
        val DEFAULT = ServerData(
            url = "/",
            description = null
        )
    }

}
