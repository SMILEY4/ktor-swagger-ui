package de.lruegner.ktorswaggerui.apispec

import de.lruegner.ktorswaggerui.OpenApiTagConfig
import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.tags.Tag

/**
 * Generator for the OpenAPI Tag-Objects
 */
class OApiTagsGenerator {

    /**
     * Generate the OpenAPI Tag-Objects from the given configs
     */
    fun generate(configs: List<OpenApiTagConfig>): List<Tag> {
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