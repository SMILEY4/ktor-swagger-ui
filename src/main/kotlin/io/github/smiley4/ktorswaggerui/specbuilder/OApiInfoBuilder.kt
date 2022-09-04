package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.dsl.OpenApiInfo
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License

/**
 * Builder for the OpenAPI Info-Object
 */
class OApiInfoBuilder {

    fun build(info: OpenApiInfo): Info {
        return Info().apply {
            title = info.title
            version = info.version
            description = info.description
            termsOfService = info.termsOfService
            info.getContact()?.let { cfgContact ->
                contact = Contact().apply {
                    name = cfgContact.name
                    url = cfgContact.url
                    email = cfgContact.email
                }
            }
            info.getLicense()?.let { cfgLicense ->
                license = License().apply {
                    name = cfgLicense.name
                    url = cfgLicense.url
                }
            }
        }
    }

}