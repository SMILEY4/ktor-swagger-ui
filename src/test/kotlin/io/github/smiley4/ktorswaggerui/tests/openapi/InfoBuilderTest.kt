package io.github.smiley4.ktorswaggerui.tests.openapi

import io.github.smiley4.ktorswaggerui.dsl.OpenApiInfo
import io.github.smiley4.ktorswaggerui.spec.openapi.ContactBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.InfoBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.LicenseBuilder
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.swagger.v3.oas.models.info.Info


class InfoBuilderTest : StringSpec({

    "empty info object" {
        buildInfoObject {}.also { info ->
            info.title shouldBe "API"
            info.version shouldBe "latest"
            info.description shouldBe null
            info.termsOfService shouldBe null
            info.contact shouldBe null
            info.license shouldBe null
            info.extensions shouldBe null
            info.summary shouldBe null
        }
    }

    "full info object" {
        buildInfoObject {
            title = "Test Api"
            version = "1.0"
            description = "Api for testing"
            termsOfService = "test-tos"
            contact {
                name = "Test Person"
                url = "example.com"
                email = "test.mail"

            }
            license {
                name = "Test License"
                url = "example.com"
            }
        }.also { info ->
            info.title shouldBe "Test Api"
            info.version shouldBe "1.0"
            info.description shouldBe "Api for testing"
            info.termsOfService shouldBe "test-tos"
            info.contact
                .also { contact -> contact.shouldNotBeNull() }
                ?.also { contact ->
                    contact.name shouldBe "Test Person"
                    contact.url shouldBe "example.com"
                    contact.email shouldBe "test.mail"
                }
            info.license
                .also { license -> license.shouldNotBeNull() }
                ?.also { license ->
                    license.name shouldBe "Test License"
                    license.url shouldBe "example.com"
                }
            info.extensions shouldBe null
            info.summary shouldBe null
        }
    }

}) {

    companion object {

        private fun buildInfoObject(builder: OpenApiInfo.() -> Unit): Info {
            return InfoBuilder(
                contactBuilder = ContactBuilder(),
                licenseBuilder = LicenseBuilder()
            ).build(OpenApiInfo().apply(builder))
        }

    }

}