package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.dsl.OpenApiLicense
import io.swagger.v3.oas.models.info.License

class LicenseBuilder {

    fun build(license: OpenApiLicense): License =
        License().also {
            it.name = license.name
            it.url = license.url
        }

}