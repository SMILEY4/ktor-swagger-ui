package io.github.smiley4.ktorswaggerui.dsl

/**
 * Documentation for an example object
 */
@OpenApiDslMarker
class OpenApiExample(
    /**
     * The actual example object/value
     */
    val value: Any
) {

    /**
     * A short description of the example
     */
    var summary: String? = null


    /**
     * A long description of the example
     */
    var description: String? = null

}