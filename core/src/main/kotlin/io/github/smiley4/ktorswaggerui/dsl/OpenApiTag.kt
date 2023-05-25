package io.github.smiley4.ktorswaggerui.dsl

/**
 * Adds metadata to a single tag.
 */
@OpenApiDslMarker
class OpenApiTag(
    /**
     * The name of the tag.
     */
    var name: String
) {

    /**
     * A short description for the tag.
     */
    var description: String? = null


    /**
     * A short description of additional external documentation for this tag.
     */
    var externalDocDescription: String? = null


    /**
     *The URL for additional external documentation for this tag.
     */
    var externalDocUrl: String? = null

}
