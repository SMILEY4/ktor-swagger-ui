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


    fun build(base: LicenseData) = LicenseData(
        name = DataUtils.merge(base.name, name),
        url = DataUtils.merge(base.url, url),
    )
}
