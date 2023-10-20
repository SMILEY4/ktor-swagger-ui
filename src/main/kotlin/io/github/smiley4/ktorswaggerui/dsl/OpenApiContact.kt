package io.github.smiley4.ktorswaggerui.dsl

import io.github.smiley4.ktorswaggerui.data.ContactData
import io.github.smiley4.ktorswaggerui.data.DataUtils.merge

/**
 * Contact information for the exposed API.
 */
@OpenApiDslMarker
class OpenApiContact {

    /**
     * The identifying name of the contact person/organization.
     */
    var name: String? = ContactData.DEFAULT.name


    /**
     * The URL pointing to the contact information. MUST be in the format of a URL.
     */
    var url: String? = ContactData.DEFAULT.url


    /**
     * The email address of the contact person/organization. MUST be in the format of an email address.
     */
    var email: String? = ContactData.DEFAULT.email


    fun build(base: ContactData) = ContactData(
        name = merge(base.name, name),
        url = merge(base.url, url),
        email = merge(base.email, email)
    )

}
