package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.dsl.OpenApiTag
import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.tags.Tag

/**
 * Generator for the OpenAPI Tag-Objects
 */
class OApiTagsGenerator {

    /**
     * Generate the OpenAPI Tag-Objects from the given configs
     */
    fun generate(configs: List<OpenApiTag>): List<Tag> {
        return configs.map {
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