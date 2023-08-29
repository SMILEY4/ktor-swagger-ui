package io.github.smiley4.ktorswaggerui.dsl

/**
 * An object representing external documentation.
 */
@OpenApiDslMarker
class OpenApiExternalDocs {
    /**
     * A short description of the external documentation
     */
    var description: String? = null

    /**
     * A URL to the external documentation
     */
    var url: String = "/"
}
