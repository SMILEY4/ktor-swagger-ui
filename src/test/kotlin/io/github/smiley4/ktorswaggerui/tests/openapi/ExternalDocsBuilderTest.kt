package io.github.smiley4.ktorswaggerui.tests.openapi

import io.github.smiley4.ktorswaggerui.dsl.OpenApiExternalDocs
import io.github.smiley4.ktorswaggerui.spec.openapi.ExternalDocumentationBuilder
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.swagger.v3.oas.models.ExternalDocumentation

class ExternalDocsBuilderTest : StringSpec({

    "default external docs object" {
        buildExternalDocsObject {}.also { docs ->
            docs.url shouldBe "/"
            docs.description shouldBe null
        }
    }

    "complete server object" {
        buildExternalDocsObject {
            url = "Test URL"
            description = "Test Description"
        }.also { docs ->
            docs.url shouldBe "Test URL"
            docs.description shouldBe "Test Description"
        }
    }

}) {

    companion object {

        private fun buildExternalDocsObject(builder: OpenApiExternalDocs.() -> Unit): ExternalDocumentation {
            return ExternalDocumentationBuilder().build(OpenApiExternalDocs().apply(builder))
        }

    }

}
