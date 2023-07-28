package io.github.smiley4.ktorswaggerui.tests.schema

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ricky12awesome.jss.encodeToSchema
import com.github.victools.jsonschema.generator.Option
import com.github.victools.jsonschema.generator.OptionPreset
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder
import com.github.victools.jsonschema.generator.SchemaVersion
import com.github.victools.jsonschema.module.jackson.JacksonModule
import com.github.victools.jsonschema.module.swagger2.Swagger2Module
import io.github.smiley4.ktorswaggerui.dsl.Example
import io.github.smiley4.ktorswaggerui.dsl.SchemaEncoder
import io.github.smiley4.ktorswaggerui.dsl.getSchemaType
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaBuilder
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaDefinitions
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaTypeAttributeOverride
import io.github.smiley4.ktorswaggerui.spec.schema.TypeOverwrites
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.io.File
import kotlin.reflect.jvm.javaType

class SchemaBuilderTest : StringSpec({

    "primitive (victools, all definitions)" {
        createSchemaVictools<Int>(true).also { defs ->
            defs.definitions.keys shouldContainExactly setOf("int")
            defs.root.also { schema ->
                schema.`$ref` shouldBe "#/components/schemas/int"
                schema.type shouldBe null
            }
            defs.definitions["int"]!!.also { schema ->
                schema.`$ref` shouldBe null
                schema.type shouldBe "integer"
            }
        }
    }

    "primitive (victools, no definitions)" {
        createSchemaVictools<Int>(false).also { defs ->
            defs.definitions shouldHaveSize 0
            defs.root.also { schema ->
                schema.`$ref` shouldBe null
                schema.type shouldBe "integer"
                schema.properties shouldBe null
            }
        }
    }

    "primitive (kotlinx, definitions)" {
        createSchemaKotlinX<Int>(true).also { defs ->
            defs.definitions.keys shouldContainExactly setOf("x1xjd3yo2dbzzz")
            defs.root.also { schema ->
                schema.`$ref` shouldBe "#/components/schemas/x1xjd3yo2dbzzz"
                schema.type shouldBe null
            }
            defs.definitions["x1xjd3yo2dbzzz"]!!.also { schema ->
                schema.`$ref` shouldBe null
                schema.type shouldBe "number"
                schema.properties shouldBe null
            }
        }
    }

    "primitive (kotlinx, no definitions)" {
        createSchemaKotlinX<Int>(false).also { defs ->
            defs.definitions shouldHaveSize 0
            defs.root.also { schema ->
                schema.`$ref` shouldBe null
                schema.type shouldBe "number"
                schema.properties shouldBe null
            }
        }
    }

    "simple object (victools, all definitions)" {
        createSchemaVictools<Pet>(true).also { defs ->
            defs.definitions.keys shouldContainExactly setOf("Pet")
            defs.root.also { schema ->
                schema.`$ref` shouldBe "#/components/schemas/Pet"
                schema.type shouldBe null
            }
            defs.definitions["Pet"]!!.also { schema ->
                schema.`$ref` shouldBe null
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactly setOf("id", "name", "tag")
            }
        }
    }

    "simple object (victools, no definitions)" {
        createSchemaVictools<Pet>(false).also { defs ->
            println(defs)
            defs.definitions shouldHaveSize 0
            defs.root.also { schema ->
                schema.`$ref` shouldBe null
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactly setOf("id", "name", "tag")
                schema.properties["id"]!!.also { prop ->
                    prop.`$ref` shouldBe null
                    prop.type shouldBe "integer"
                }
                schema.properties["name"]!!.also { prop ->
                    prop.`$ref` shouldBe null
                    prop.type shouldBe "string"
                }
                schema.properties["tag"]!!.also { prop ->
                    prop.`$ref` shouldBe null
                    prop.type shouldBe "string"
                }
            }
        }
    }

    "simple object (kotlinx, definitions)" {
        createSchemaKotlinX<Pet>(true).also { defs ->
            defs.definitions.keys shouldContainExactly setOf("1d8t6cs0dbcap", "x1xjd3yo2dbzzz", "xq0zwcprkn9j3")
            defs.root.also { schema ->
                schema.`$ref` shouldBe "#/components/schemas/1d8t6cs0dbcap"
                schema.type shouldBe null
            }
            defs.definitions["1d8t6cs0dbcap"]!!.also { schema ->
                schema.`$ref` shouldBe null
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactly setOf("id", "name", "tag")
                schema.properties["id"]!!.also { prop ->
                    prop.`$ref` shouldBe "#/components/schemas/x1xjd3yo2dbzzz"
                    prop.type shouldBe null
                }
                schema.properties["name"]!!.also { prop ->
                    prop.`$ref` shouldBe "#/components/schemas/xq0zwcprkn9j3"
                    prop.type shouldBe null
                }
                schema.properties["tag"]!!.also { prop ->
                    prop.`$ref` shouldBe "#/components/schemas/xq0zwcprkn9j3"
                    prop.type shouldBe null
                }
            }
            defs.definitions["x1xjd3yo2dbzzz"]!!.also { schema ->
                schema.`$ref` shouldBe null
                schema.type shouldBe "number"
                schema.properties shouldBe null
            }
            defs.definitions["xq0zwcprkn9j3"]!!.also { schema ->
                schema.`$ref` shouldBe null
                schema.type shouldBe "string"
                schema.properties shouldBe null
            }
        }
    }

    "simple object (kotlinx, no definitions)" {
        createSchemaKotlinX<Pet>(false).also { defs ->
            defs.definitions shouldHaveSize 0
            defs.root.also { schema ->
                schema.`$ref` shouldBe null
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactly setOf("id", "name", "tag")
                schema.properties["id"]!!.also { prop ->
                    prop.`$ref` shouldBe null
                    prop.type shouldBe "number"
                }
                schema.properties["name"]!!.also { prop ->
                    prop.`$ref` shouldBe null
                    prop.type shouldBe "string"
                }
                schema.properties["tag"]!!.also { prop ->
                    prop.`$ref` shouldBe null
                    prop.type shouldBe "string"
                }
            }
        }
    }

    //==== SIMPLE LIST =====================================================

    "simple list (victools, all definitions)" {
        createSchemaVictools<List<Pet>>(true).also { defs ->
            defs.definitions.keys shouldContainExactly setOf("List(Pet)", "Pet")
            defs.root.also { schema ->
                schema.`$ref` shouldBe "#/components/schemas/List(Pet)"
                schema.type shouldBe null
            }
            defs.definitions["List(Pet)"]!!.also { schema ->
                schema.`$ref` shouldBe null
                schema.type shouldBe "array"
                schema.items
                    .also { it shouldNotBe null }
                    ?.also { item ->
                        item.type shouldBe null
                        item.`$ref` shouldBe "#/components/schemas/Pet"
                    }
            }
            defs.definitions["Pet"]!!.also { schema ->
                schema.`$ref` shouldBe null
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactly setOf("id", "name", "tag")
            }
        }
    }

    "simple list (victools, no definitions)" {
        createSchemaVictools<List<Pet>>(false).also { defs ->
            defs.definitions shouldHaveSize 0
            defs.root.also { schema ->
                schema.`$ref` shouldBe null
                schema.type shouldBe "array"
                schema.properties shouldBe null
                schema.items
                    .also { it shouldNotBe null }
                    ?.also { item ->
                        item.type shouldBe "object"
                        item.`$ref` shouldBe null
                        item.properties.keys shouldContainExactly setOf("id", "name", "tag")
                        item.properties["id"]!!.also { prop ->
                            prop.`$ref` shouldBe null
                            prop.type shouldBe "integer"
                        }
                        item.properties["name"]!!.also { prop ->
                            prop.`$ref` shouldBe null
                            prop.type shouldBe "string"
                        }
                        item.properties["tag"]!!.also { prop ->
                            prop.`$ref` shouldBe null
                            prop.type shouldBe "string"
                        }
                    }
            }
        }
    }


    "simple list (kotlinx, definitions)" {
        createSchemaKotlinX<List<Pet>>(true).also { defs ->
            defs.definitions.keys shouldContainExactly setOf(
                "1tonzv7il5q0x",
                "1d8t6cs0dbcap",
                "x1xjd3yo2dbzzz",
                "xq0zwcprkn9j3"
            )
            defs.root.also { schema ->
                schema.`$ref` shouldBe "#/components/schemas/1tonzv7il5q0x"
                schema.type shouldBe null
            }
            defs.definitions["1tonzv7il5q0x"]!!.also { schema ->
                schema.`$ref` shouldBe null
                schema.type shouldBe "array"
                schema.properties shouldBe null
                schema.items
                    .also { it shouldNotBe null }
                    ?.also { item ->
                        item.type shouldBe null
                        item.`$ref` shouldBe "#/components/schemas/1d8t6cs0dbcap"
                    }
            }
            defs.definitions["1d8t6cs0dbcap"]!!.also { schema ->
                schema.`$ref` shouldBe null
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactly setOf("id", "name", "tag")
                schema.properties["id"]!!.also { prop ->
                    prop.`$ref` shouldBe "#/components/schemas/x1xjd3yo2dbzzz"
                    prop.type shouldBe null
                }
                schema.properties["name"]!!.also { prop ->
                    prop.`$ref` shouldBe "#/components/schemas/xq0zwcprkn9j3"
                    prop.type shouldBe null
                }
                schema.properties["tag"]!!.also { prop ->
                    prop.`$ref` shouldBe "#/components/schemas/xq0zwcprkn9j3"
                    prop.type shouldBe null
                }
            }
            defs.definitions["x1xjd3yo2dbzzz"]!!.also { schema ->
                schema.`$ref` shouldBe null
                schema.type shouldBe "number"
                schema.properties shouldBe null
            }
            defs.definitions["xq0zwcprkn9j3"]!!.also { schema ->
                schema.`$ref` shouldBe null
                schema.type shouldBe "string"
                schema.properties shouldBe null
            }
        }
    }

    "simple list (kotlinx, no definitions)" {
        createSchemaKotlinX<List<Pet>>(false).also { defs ->
            defs.definitions shouldHaveSize 0
            defs.root.also { schema ->
                schema.`$ref` shouldBe null
                schema.type shouldBe "array"
                schema.properties shouldBe null
                schema.items
                    .also { it shouldNotBe null }
                    ?.also { item ->
                        item.type shouldBe "object"
                        item.`$ref` shouldBe null
                        item.properties.keys shouldContainExactly setOf("id", "name", "tag")
                        item.properties["id"]!!.also { prop ->
                            prop.`$ref` shouldBe null
                            prop.type shouldBe "number"
                        }
                        item.properties["name"]!!.also { prop ->
                            prop.`$ref` shouldBe null
                            prop.type shouldBe "string"
                        }
                        item.properties["tag"]!!.also { prop ->
                            prop.`$ref` shouldBe null
                            prop.type shouldBe "string"
                        }
                    }
            }
        }
    }

    "schema with Schema-Annotations" {
        createSchemaVictools<Person>(false).also { defs ->
            defs.definitions shouldHaveSize 0
            defs.root.also { schema ->
                schema.title = "The Schema for a person"
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactly setOf("age", "city_code", "name")
                schema.properties["age"]!!.also { age ->
                    age.type shouldBe "integer"
                    age.description shouldBe "the age of the person in years"
                    age.format shouldBe "int32"
                    age.nullable shouldBe true
                    age.minimum.toInt() shouldBe 1
                    age.maximum.toInt() shouldBe 99
                    age.example shouldBe 42
                }
                schema.properties["name"]!!.also { name ->
                    name.type shouldBe "string"
                    name.description shouldBe "the name of the person"
                    name.minLength shouldBe 1
                    name.maxLength shouldBe 32
                    name.example shouldBe "Mr. Example"
                }
                schema.properties["city_code"]!!.also { cityCode ->
                    cityCode.type shouldBe "integer"
                    cityCode.format shouldBe "int32"
                    cityCode.example shouldBe 12345
                }
            }
        }
    }

    "file type-overwrite" {
        createSchemaVictools<File>(false).also { defs ->
            defs.definitions shouldHaveSize 0
            defs.root.also { schema ->
                schema.type shouldBe "string"
                schema.format shouldBe "binary"
            }
        }
    }

}) {

    companion object {

        @Serializable
        private data class Pet(
            val id: Int,
            val name: String,
            val tag: String
        )


        @Schema(title = "The Schema for a person")
        data class Person(

            @field:Schema(
                description = "the name of the person",
                minLength = 1,
                maxLength = 32
            )
            @field:Example("Mr. Example")
            val name: String,

            @field:Schema(
                description = "the age of the person in years",
                nullable = true,
                maximum = "99",
                minimum = "1",
            )
            @field:Example("42")
            val age: Int,

            @field:Schema(
                name = "city_code",
                example = "12345"
            )
            val cityCode: Int

        )

        inline fun <reified T> createSchemaVictools(definitions: Boolean) =
            createSchema<T>("\$defs", serializerVictools(definitions))

        inline fun <reified T> createSchemaKotlinX(generateDefinitions: Boolean) =
            createSchema<T>("definitions", serializerKotlinX(generateDefinitions))

        inline fun <reified T> createSchema(
            defs: String,
            noinline serializer: SchemaEncoder
        ): SchemaDefinitions {
            return SchemaBuilder(defs, serializer, ObjectMapper(), TypeOverwrites.get()).create(getSchemaType<T>())
        }

        fun serializerVictools(definitions: Boolean): SchemaEncoder {
            return { type ->
                SchemaGenerator(
                    SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON)
                        .with(JacksonModule())
                        .with(Swagger2Module())
                        .with(Option.EXTRA_OPEN_API_FORMAT_VALUES)
                        .with(Option.ALLOF_CLEANUP_AT_THE_END)
                        .with(Option.MAP_VALUES_AS_ADDITIONAL_PROPERTIES).also {
                            if (definitions) {
                                it
                                    .with(Option.DEFINITIONS_FOR_ALL_OBJECTS)
                                    .with(Option.DEFINITION_FOR_MAIN_SCHEMA)
                                    .without(Option.INLINE_ALL_SCHEMAS)
                            } else {
                                it.with(Option.INLINE_ALL_SCHEMAS)
                            }
                        }
                        .also {
                            it.forTypesInGeneral()
                                .withTypeAttributeOverride(SchemaTypeAttributeOverride())
                        }
                        .build()
                ).generateSchema(type.javaType).toPrettyString()
            }
        }

        fun serializerKotlinX(generateDefinitions: Boolean): SchemaEncoder {
            val kotlinxJson = Json {
                prettyPrint = true
                encodeDefaults = true
            }
            return { type ->
                kotlinxJson.encodeToSchema(
                    serializer(type),
                    generateDefinitions = generateDefinitions,
                    exposeClassDiscriminator = false
                )
            }
        }

    }

}