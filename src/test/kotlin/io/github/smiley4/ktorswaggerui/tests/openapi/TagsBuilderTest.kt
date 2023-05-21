package io.github.smiley4.ktorswaggerui.tests.openapi

import io.github.smiley4.ktorswaggerui.dsl.OpenApiTag
import io.github.smiley4.ktorswaggerui.spec.openapi.ExternalDocumentationBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.TagBuilder
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.swagger.v3.oas.models.tags.Tag


class TagsBuilderTest : StringSpec({

    "empty tag object" {
        buildTagObject("test-tag") {}.also { tag ->
            tag.name shouldBe "test-tag"
            tag.description shouldBe null
            tag.externalDocs shouldBe null
            tag.extensions shouldBe null
        }
    }

    "full tag object" {
        buildTagObject("test-tag") {
            description = "Description of tag"
            externalDocDescription = "Description of external docs"
            externalDocUrl = "example.com"
        }.also { tag ->
            tag.name shouldBe "test-tag"
            tag.description shouldBe "Description of tag"
            tag.externalDocs
                .also { docs -> docs.shouldNotBeNull() }
                ?.also { docs ->
                    docs.description shouldBe "Description of external docs"
                    docs.url shouldBe "example.com"
                    docs.extensions shouldBe null
                }
            tag.extensions shouldBe null
        }
    }

}) {

    companion object {

        private fun buildTagObject(name: String, builder: OpenApiTag.() -> Unit): Tag {
            return TagBuilder(
                externalDocumentationBuilder = ExternalDocumentationBuilder()
            ).build(OpenApiTag(name).apply(builder))
        }

    }

}