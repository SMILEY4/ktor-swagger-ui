package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.dsl.OpenApiInfo
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License

/**
 * Generator for the OpenAPI Info-Object
 */
class OApiInfoGenerator {

    /**
     * Generate the OpenAPI Info-Object from the given config
     */
    fun generate(config: OpenApiInfo): Info {
        return Info().apply {
            title = config.title
            version = config.version
            description = config.description
            termsOfService = config.termsOfService
            config.getContact()?.let { cfgContact ->
                contact = Contact().apply {
                    name = cfgContact.name
                    url = cfgContact.url
                    email = cfgContact.email
                }
            }
            config.getLicense()?.let { cfgLicense ->
                license = License().apply {
                    name = cfgLicense.name
                    url = cfgLicense.url
                }
            }
        }
    }

}