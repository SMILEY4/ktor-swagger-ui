package io.github.smiley4.ktorswaggerui.data

data class ExternalDocsData(
    val url: String,
    val description: String?,
) {

    companion object {
        val DEFAULT = ExternalDocsData(
            url = "/",
            description = null
        )
    }

}
