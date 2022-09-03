package io.github.smiley4.ktorswaggerui.tests

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.swagger.v3.oas.models.media.Schema


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
    this.additionalProperties shouldBe expected.additionalProperties
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
//    this.discriminator shouldBe expected.discriminator
//    this.example shouldBe expected.example
//    this.externalDocs shouldBe expected.externalDocs
//    this.xml shouldBe expected.xml
//    this.extensions shouldBe expected.extensions
}

private fun <T> assertNullSafe(actual: T?, expected: T?, assertion: () -> Unit) {
    if(expected == null) {
        actual.shouldBeNull()
    } else {
        actual.shouldNotBeNull()
        assertion()
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