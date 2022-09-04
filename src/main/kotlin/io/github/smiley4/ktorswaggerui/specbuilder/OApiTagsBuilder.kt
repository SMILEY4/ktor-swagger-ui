package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.dsl.OpenApiTag
import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.tags.Tag

/**
 * Builder for the OpenAPI Tag-Objects
 */
class OApiTagsBuilder {

    fun build(tags: List<OpenApiTag>): List<Tag> {
        return tags.map {
            Tag().apply {
                name = it.name
                description = it.description
                if (it.externalDocDescription != null || it.externalDocUrl != null) {
                    externalDocs = ExternalDocumentation().apply {
                        description = it.externalDocDescription
                        url = it.externalDocUrl
                    }
                }
            }
        }
    }

}