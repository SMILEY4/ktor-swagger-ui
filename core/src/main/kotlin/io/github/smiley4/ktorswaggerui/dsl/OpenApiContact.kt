package io.github.smiley4.ktorswaggerui.dsl

/**
 * Contact information for the exposed API.
 */
@OpenApiDslMarker
class OpenApiContact {

    /**
     * The identifying name of the contact person/organization.
     */
    var name: String? = null


    /**
     * The URL pointing to the contact information. MUST be in the format of a URL.
     */
    var url: String? = null


    /**
     * The email address of the contact person/organization. MUST be in the format of an email address.
     */
    var email: String? = null

}
