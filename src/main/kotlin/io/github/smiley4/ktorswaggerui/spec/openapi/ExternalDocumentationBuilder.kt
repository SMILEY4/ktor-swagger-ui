package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.dsl.OpenApiExternalDocs
import io.swagger.v3.oas.models.ExternalDocumentation

class ExternalDocumentationBuilder {

    fun build(externalDocs: OpenApiExternalDocs): ExternalDocumentation =
        ExternalDocumentation().also {
            it.url = externalDocs.url
            it.description = externalDocs.description
        }

}

