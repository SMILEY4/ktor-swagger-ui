package io.github.smiley4.ktorswaggerui.dsl.config

import io.github.smiley4.ktorswaggerui.data.DataUtils
import io.github.smiley4.ktorswaggerui.data.LicenseData
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker

/**
 * License information for the exposed API.
 */
@OpenApiDslMarker
class OpenApiLicense {

    /**
     * The license name used for the API
     */
    var name: String? = LicenseData.DEFAULT.name


    /**
     * A URL to the license used for the API. MUST be in the format of a URL.
     */
    var url: String? = LicenseData.DEFAULT.url


    /**
     * An SPDX (https://spdx.org/licenses/) license expression for the API. The identifier field is mutually exclusive of the url field.
     */
    var identifier: String? = LicenseData.DEFAULT.identifier

    /**
     * Build the data object for this config.
     * @param base the base config to "inherit" from. Values from the base should be copied, replaced or merged together.
     */
    fun build(base: LicenseData) = LicenseData(
        name = DataUtils.merge(base.name, name),
        url = DataUtils.merge(base.url, url),
        identifier = DataUtils.merge(base.identifier, identifier)
    )
}
