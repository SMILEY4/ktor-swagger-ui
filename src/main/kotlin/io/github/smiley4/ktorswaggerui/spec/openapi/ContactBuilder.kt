package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.data.ContactData
import io.swagger.v3.oas.models.info.Contact

class ContactBuilder {

    fun build(contact: ContactData): Contact =
        Contact().also {
            it.name = contact.name
            it.email = contact.email
            it.url = contact.url
        }

}
