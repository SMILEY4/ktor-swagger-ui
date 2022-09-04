package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.dsl.OpenApiTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.tags.Tag

class TagsObjectTest : StringSpec({

    "test default tag object" {
        val tag = buildTagsObject("TestTag") {}
        tag shouldBeTag {
            name = "TestTag"
        }
    }

    "test complete tag object" {
        val tag = buildTagsObject("TestTag") {
            description = "Test Description"
            externalDocDescription = "External Doc Description"
            externalDocUrl = "External Doc URL"
        }
        tag shouldBeTag {
            name = "TestTag"
            description = "Test Description"
            externalDocs = ExternalDocumentation().apply {
                description = "External Doc Description"
                url = "External Doc URL"
            }
        }
    }

    "test multiple tag objects" {
        val tags = buildTagsObjects(mapOf(
            "TestTag 1" to {
                description = "Test Description 1"
                externalDocDescription = "External Doc Description 1"
                externalDocUrl = "External Doc URL 1"
            },
            "TestTag 2" to {
                description = "Test Description 2"
                externalDocDescription = "External Doc Description 2"
                externalDocUrl = "External Doc URL 2"
            }
        ))
        tags[0] shouldBeTag {
            name = "TestTag 1"
            description = "Test Description 1"
            externalDocs = ExternalDocumentation().apply {
                description = "External Doc Description 1"
                url = "External Doc URL 1"
            }
        }
        tags[1] shouldBeTag {
            name = "TestTag 2"
            description = "Test Description 2"
            externalDocs = ExternalDocumentation().apply {
                description = "External Doc Description 2"
                url = "External Doc URL 2"
            }
        }
    }

}) {

    companion object {

        private fun buildTagsObject(name: String, builder: OpenApiTag.() -> Unit): Tag {
            return getOApiTagsBuilder().build(listOf(OpenApiTag(name).apply(builder))).let {
                it shouldHaveSize 1
                it.first()
            }
        }

        private fun buildTagsObjects(builders: Map<String, OpenApiTag.() -> Unit>): List<Tag> {
            val tags = mutableListOf<Tag>()
            builders.forEach { (name, builder) ->
                tags.addAll(getOApiTagsBuilder().build(listOf(OpenApiTag(name).apply(builder))))
            }
            tags shouldHaveSize builders.size
            return tags
        }

    }

}