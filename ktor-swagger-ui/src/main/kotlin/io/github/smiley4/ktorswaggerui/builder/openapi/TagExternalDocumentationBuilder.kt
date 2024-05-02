package io.github.smiley4.ktorswaggerui.builder.openapi

import io.swagger.v3.oas.models.ExternalDocumentation

class TagExternalDocumentationBuilder {

    fun build(url: String, description: String): ExternalDocumentation =
        ExternalDocumentation().also {
            it.url = url
            it.description = description
        }
}
