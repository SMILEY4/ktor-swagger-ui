package io.github.smiley4.ktorswaggerui.dsl

/**
 * License information for the exposed API.
 */
@OpenApiDslMarker
class OpenApiLicense {

    /**
     * The license name used for the API
     */
    var name: String = "?"


    /**
     * A URL to the license used for the API. MUST be in the format of a URL.
     */
    var url: String? = null

}
