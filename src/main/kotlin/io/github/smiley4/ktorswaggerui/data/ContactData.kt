package io.github.smiley4.ktorswaggerui.data

data class ContactData(
    val name: String?,
    val url: String?,
    val email: String?
) {
    companion object {
        val DEFAULT = ContactData(
            name = null,
            url = null,
            email = null
        )
    }
}
