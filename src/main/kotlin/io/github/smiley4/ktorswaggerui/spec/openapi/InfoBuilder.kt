package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.dsl.OpenApiInfo
import io.swagger.v3.oas.models.info.Info

class InfoBuilder(
    private val contactBuilder: ContactBuilder,
    private val licenseBuilder: LicenseBuilder
) {

    fun build(info: OpenApiInfo): Info =
        Info().also {
            it.title = info.title
            it.version = info.version
            it.description = info.description
            it.termsOfService = info.termsOfService
            info.getContact()?.also { contact ->
                it.contact = contactBuilder.build(contact)
            }
            info.getLicense()?.also { license ->
                it.license = licenseBuilder.build(license)
            }
        }

}