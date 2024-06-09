package io.github.smiley4.ktorswaggerui.data

/**
 * See [OpenAPI Specification - License Object](https://swagger.io/specification/#license-object).
 */
data class LicenseData(
    val name: String?,
    val url: String?,
    val identifier: String?
) {
    companion object {
        val DEFAULT = LicenseData(
            name = null,
            url = null,
            identifier = null
        )
    }
}
