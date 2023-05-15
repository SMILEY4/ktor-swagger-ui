package io.github.smiley4.ktorswaggerui.tests

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import io.swagger.v3.oas.models.tags.Tag




infix fun SecurityScheme.shouldBeSecurityScheme(expectedBuilder: SecurityScheme.() -> Unit) {
    this shouldBeSecurityScheme SecurityScheme().apply(expectedBuilder)
}

infix fun SecurityScheme.shouldBeSecurityScheme(expected: SecurityScheme?) {
    if (expected == null) {
        this.shouldBeNull()
        return
    } else {
        this.shouldNotBeNull()
    }
    this.type shouldBe expected.type
    this.description shouldBe expected.description
    this.name shouldBe expected.name
    this.`$ref` shouldBe expected.`$ref`
    this.`in` shouldBe expected.`in`
    this.scheme shouldBe expected.scheme
    this.openIdConnectUrl shouldBe expected.openIdConnectUrl
    assertNullSafe(this.flows, expected.flows) {
        this.flows.implicit shouldBeOAuthFlow expected.flows.implicit
        this.flows.password shouldBeOAuthFlow expected.flows.password
        this.flows.clientCredentials shouldBeOAuthFlow expected.flows.clientCredentials
        this.flows.authorizationCode shouldBeOAuthFlow expected.flows.authorizationCode
    }
}

infix fun OAuthFlow.shouldBeOAuthFlow(expected: OAuthFlow) {
    this.authorizationUrl shouldBe expected.authorizationUrl
    this.tokenUrl shouldBe expected.tokenUrl
    this.refreshUrl shouldBe expected.refreshUrl
    this.scopes shouldContainExactly expected.scopes
}


infix fun Server.shouldBeServer(expectedBuilder: Server.() -> Unit) {
    this shouldBeServer Server().apply(expectedBuilder)
}

infix fun Server.shouldBeServer(expected: Server?) {
    if (expected == null) {
        this.shouldBeNull()
        return
    } else {
        this.shouldNotBeNull()
    }
    this.url shouldBe expected.url
    this.description shouldBe expected.description
}


infix fun Info.shouldBeInfo(expectedBuilder: Info.() -> Unit) {
    this shouldBeInfo Info().apply(expectedBuilder)
}

infix fun Info.shouldBeInfo(expected: Info?) {
    if (expected == null) {
        this.shouldBeNull()
        return
    } else {
        this.shouldNotBeNull()
    }
    this.title shouldBe expected.title
    this.version shouldBe expected.version
    this.description shouldBe expected.description
    this.termsOfService shouldBe expected.termsOfService
    assertNullSafe(this.contact, expected.contact) {
        this.contact.name shouldBe expected.contact.name
        this.contact.url shouldBe expected.contact.url
        this.contact.email shouldBe expected.contact.email
    }
    assertNullSafe(this.license, expected.license) {
        this.license.name shouldBe expected.license.name
        this.license.url shouldBe expected.license.url
    }
}


infix fun Schema<*>.shouldBeSchema(expectedBuilder: Schema<*>.() -> Unit) {
    this shouldBeSchema Schema<Any>().apply(expectedBuilder)
}

infix fun Schema<*>.shouldBeSchema(expected: Schema<*>?) {
    if (expected == null) {
        this.shouldBeNull()
        return
    } else {
        this.shouldNotBeNull()
    }
    this.default shouldBe expected.default
    this.const shouldBe expected.const
    this.title shouldBe expected.title
    this.format shouldBe expected.format
    this.multipleOf shouldBe expected.multipleOf
    this.maximum shouldBe expected.maximum
    this.exclusiveMaximum shouldBe expected.exclusiveMaximum
    this.minimum shouldBe expected.minimum
    this.exclusiveMinimum shouldBe expected.exclusiveMinimum
    this.maxLength shouldBe expected.maxLength
    this.minLength shouldBe expected.minLength
    this.pattern shouldBe expected.pattern
    this.maxItems shouldBe expected.maxItems
    this.minItems shouldBe expected.minItems
    this.uniqueItems shouldBe expected.uniqueItems
    this.maxProperties shouldBe expected.maxProperties
    this.minProperties shouldBe expected.minProperties
    assertNullSafe(this.required, expected.required) {
        this.required shouldContainExactlyInAnyOrder expected.required
    }
    this.type shouldBe expected.type
    assertNullSafe(this.not, expected.not) {
        this.not shouldBeSchema expected.not
    }
    assertNullSafe(this.properties, expected.properties) {
        this.properties.keys shouldContainExactlyInAnyOrder expected.properties.keys
        expected.properties.keys.forEach { key ->
            this.properties[key]!! shouldBeSchema expected.properties[key]
        }
    }
    assertNullSafe(this.additionalProperties, expected.additionalProperties) {
        (this.additionalProperties as Schema<Any>) shouldBeSchema  (expected.additionalProperties as Schema<Any>)
    }
    this.description shouldBe expected.description
    this.`$ref` shouldBe expected.`$ref`
    this.nullable shouldBe expected.nullable
    this.readOnly shouldBe expected.readOnly
    this.writeOnly shouldBe expected.writeOnly
    this.deprecated shouldBe expected.deprecated
    assertNullSafe(this.enum, expected.enum) {
        this.enum shouldContainExactlyInAnyOrder expected.enum
    }
    assertSchemaList(this.prefixItems, expected.prefixItems)
    assertSchemaList(this.allOf, expected.allOf)
    assertSchemaList(this.anyOf, expected.anyOf)
    assertSchemaList(this.oneOf, expected.oneOf)
    assertSchemaList(this.prefixItems, expected.prefixItems)
    assertNullSafe(this.items, expected.items) {
        this.items shouldBeSchema expected.items
    }
    assertNullSafe(this.xml, expected.xml) {
        this.xml.name shouldBe expected.xml.name
        this.xml.namespace shouldBe expected.xml.namespace
        this.xml.prefix shouldBe expected.xml.prefix
        this.xml.attribute shouldBe expected.xml.attribute
        this.xml.wrapped shouldBe expected.xml.wrapped

    }
}

private fun assertSchemaList(actual: List<Schema<*>>?, expected: List<Schema<*>>?) {
    if (expected == null) {
        actual.shouldBeNull()
    } else {
        actual.shouldNotBeNull()
        actual shouldHaveSize expected.size
        expected.forEachIndexed { index, expectedItem ->
            actual[index] shouldBeSchema expectedItem
        }
    }
}


private fun <T> assertNullSafe(actual: T?, expected: T?, assertion: () -> Unit) {
    if (expected == null) {
        actual.shouldBeNull()
    } else {
        actual.shouldNotBeNull()
        assertion()
    }
}