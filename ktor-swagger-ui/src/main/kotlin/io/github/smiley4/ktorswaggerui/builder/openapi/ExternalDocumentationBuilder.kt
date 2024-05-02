package io.github.smiley4.ktorswaggerui.builder.openapi

import io.github.smiley4.ktorswaggerui.data.ExternalDocsData
import io.swagger.v3.oas.models.ExternalDocumentation

class ExternalDocumentationBuilder {

    fun build(externalDocs: ExternalDocsData): ExternalDocumentation =
        ExternalDocumentation().also {
            it.url = externalDocs.url
            it.description = externalDocs.description
        }

}

