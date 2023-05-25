package io.github.smiley4.ktorswaggerui.dsl

@OpenApiDslMarker
class OpenApiInfo {

    /**
     * The title of the api
     */
    var title: String = "API"


    /**
     * The version of the OpenAPI document
     */
    var version: String? = "latest"


    /**
     * A short description of the API
     */
    var description: String? = null


    /**
     * A URL to the Terms of Service for the API. MUST be in the format of a URL.
     */
    var termsOfService: String? = null


    private var contact: OpenApiContact? = null

    /**
     * The contact information for the exposed API.
     */
    fun contact(block: OpenApiContact.() -> Unit) {
        contact = OpenApiContact().apply(block)
    }

    fun getContact() = contact


    private var license: OpenApiLicense? = null

    /**
     * The license information for the exposed API.
     */
    fun license(block: OpenApiLicense.() -> Unit) {
        license = OpenApiLicense().apply(block)
    }

    fun getLicense() = license

}
