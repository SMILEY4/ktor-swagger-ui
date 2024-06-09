package io.github.smiley4.ktorswaggerui.data

/**
 * See [OpenAPI Specification - External Documentation Object](https://swagger.io/specification/#external-documentation-object).
 */
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
