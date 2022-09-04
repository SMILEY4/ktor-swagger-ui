package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.dsl.OpenApiInfo
import io.kotest.core.spec.style.StringSpec
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License

class InfoObjectTest : StringSpec({

    "test default info object" {
        val info = buildInfoObject {}
        info shouldBeInfo {
            title = "API"
            version = "latest"
        }
    }

    "test complete info object" {
        val info = buildInfoObject {
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

        private fun buildInfoObject(builder: OpenApiInfo.() -> Unit): Info {
            return getOApiInfoBuilder().build(OpenApiInfo().apply(builder))
        }

    }

}