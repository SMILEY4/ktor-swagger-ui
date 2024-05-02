package io.github.smiley4.ktorswaggerui.dsl.routes

import io.github.smiley4.ktorswaggerui.data.OpenApiExampleData
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker

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


    fun build() = OpenApiExampleData(
        value = value,
        summary = summary,
        description = description
    )
}
