package io.github.smiley4.ktorswaggerui.dsl.config

import io.github.smiley4.ktorswaggerui.data.DataUtils.merge
import io.github.smiley4.ktorswaggerui.data.TagData
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker

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
     * The URL for additional external documentation for this tag.
     */
    var externalDocUrl: String? = null


    fun build(base: TagData) = TagData(
        name = name,
        description = merge(base.description, description),
        externalDocDescription = merge(base.externalDocDescription, externalDocDescription),
        externalDocUrl = merge(base.externalDocUrl, externalDocUrl)
    )

}
