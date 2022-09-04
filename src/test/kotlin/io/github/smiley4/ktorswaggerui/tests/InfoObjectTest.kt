package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.OpenApiInfoConfig
import io.github.smiley4.ktorswaggerui.apispec.OApiInfoGenerator
import io.kotest.core.spec.style.StringSpec
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License

class InfoObjectTest : StringSpec({

    "test default info object" {
        val info = generateInfoObject {}
        info shouldBeInfo {
            title = "API"
            version = "latest"
        }
    }

    "test complete info object" {
        val info = generateInfoObject {
            title = "Test Title"
            version = "test"
            description = "Test Description"
            termsOfService = "Test Terms of Service"
            contact {
                name = "Test Contact Name"
                url = "Test Contact URL"
                email = "Test Contact Email"
            }
            license {
                name = "Test License Name"
                url = "Test License URL"
            }
        }
        info shouldBeInfo {
            title = "Test Title"
            version = "test"
            description = "Test Description"
            termsOfService = "Test Terms of Service"
            contact = Contact().apply {
                name = "Test Contact Name"
                url = "Test Contact URL"
                email = "Test Contact Email"
            }
            license = License().apply {
                name = "Test License Name"
                url = "Test License URL"
            }
        }
    }

}) {

    companion object {

        private fun generateInfoObject(builder: OpenApiInfoConfig.() -> Unit): Info {
            return OApiInfoGenerator().generate(OpenApiInfoConfig().apply(builder))
        }

    }

}