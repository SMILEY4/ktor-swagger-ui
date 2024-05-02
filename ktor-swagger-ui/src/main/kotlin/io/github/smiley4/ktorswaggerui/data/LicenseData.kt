package io.github.smiley4.ktorswaggerui.data

data class LicenseData(
    val name: String?,
    val url: String?,
) {
    companion object {
        val DEFAULT = LicenseData(
            name = null,
            url = null,
        )
    }
}
