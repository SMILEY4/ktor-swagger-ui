package io.github.smiley4.ktorswaggerui.data

/**
 *  See [OpenAPI Specification - Contact Object](https://swagger.io/specification/#contact-object).
 */
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
