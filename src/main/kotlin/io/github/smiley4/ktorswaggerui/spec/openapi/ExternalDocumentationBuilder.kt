package io.github.smiley4.ktorswaggerui.spec.openapi

import io.swagger.v3.oas.models.ExternalDocumentation


class ExternalDocumentationBuilder {

    fun build(url: String, description: String): ExternalDocumentation =
        ExternalDocumentation().also {
            it.url = url
            it.description = description
        }

}