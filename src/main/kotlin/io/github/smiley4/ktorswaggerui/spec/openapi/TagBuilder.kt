package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.dsl.OpenApiTag
import io.swagger.v3.oas.models.tags.Tag

class TagBuilder(
    private val tagExternalDocumentationBuilder: TagExternalDocumentationBuilder
) {

    fun build(tag: OpenApiTag): Tag =
        Tag().also {
            it.name = tag.name
            it.description = tag.description
            if(tag.externalDocUrl != null && tag.externalDocDescription != null) {
                it.externalDocs = tagExternalDocumentationBuilder.build(tag.externalDocUrl!!, tag.externalDocDescription!!)
            }
        }

}
