package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.dsl.OpenApiContact
import io.swagger.v3.oas.models.info.Contact

class ContactBuilder {

    fun build(contact: OpenApiContact): Contact =
        Contact().also {
            it.name = contact.name
            it.email = contact.email
            it.url = contact.url
        }

}