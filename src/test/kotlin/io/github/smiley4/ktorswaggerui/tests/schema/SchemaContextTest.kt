package io.github.smiley4.ktorswaggerui.tests.schema

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.victools.jsonschema.generator.Option
import com.github.victools.jsonschema.generator.OptionPreset
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder
import com.github.victools.jsonschema.generator.SchemaVersion
import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.github.smiley4.ktorswaggerui.dsl.PluginConfigDsl
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.github.smiley4.ktorswaggerui.dsl.asSchemaType
import io.github.smiley4.ktorswaggerui.dsl.getSchemaType
import io.github.smiley4.ktorswaggerui.builder.route.RouteMeta
import io.github.smiley4.ktorswaggerui.builder.schema.SchemaBuilder
import io.github.smiley4.ktorswaggerui.builder.schema.SchemaContext
import io.github.smiley4.ktorswaggerui.builder.schema.SchemaContextBuilder
import io.github.smiley4.ktorswaggerui.builder.schema.TypeOverwrites
import io.github.smiley4.ktorswaggerui.dsl.BodyTypeDescriptor.Companion.custom
import io.github.smiley4.ktorswaggerui.dsl.BodyTypeDescriptor.Companion.multipleOf
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.http.HttpMethod
import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.jvm.javaType

class SchemaContextTest : StringSpec({

    "route with all schemas" {
        val routes = listOf(
            route {
                request {
                    queryParameter<QueryParamType>("queryParam")
                    pathParameter<PathParamType>("pathParam")
                    headerParameter<HeaderParamType>("headerParam")
                    body<RequestBodyType>()
                }
                response {
                    default {
                        header<ResponseHeaderType>("header")
                        body<ResponseBodyType>()
                    }
                }
            }
        )
        val schemaContext = schemaContext(routes)
        schemaContext.getSchema(QueryParamType::class.asSchemaType()).also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/QueryParamType"
        }
        schemaContext.getSchema(PathParamType::class.asSchemaType()).also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/PathParamType"
        }
        schemaContext.getSchema(HeaderParamType::class.asSchemaType()).also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/HeaderParamType"
        }
        schemaContext.getSchema(RequestBodyType::class.asSchemaType()).also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/RequestBodyType"
        }
        schemaContext.getSchema(ResponseHeaderType::class.asSchemaType()).also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/ResponseHeaderType"
        }
        schemaContext.getSchema(ResponseBodyType::class.asSchemaType()).also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/ResponseBodyType"
        }
        schemaContext.getComponentsSection().also { components ->
            components.keys shouldContainExactlyInAnyOrder listOf(
                "QueryParamType",
                "PathParamType",
                "HeaderParamType",
                "RequestBodyType",
                "ResponseHeaderType",
                "ResponseBodyType",
            )
            components.forEach { (_, schema) ->
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactlyInAnyOrder listOf("value")
            }
        }
    }

    "primitive type" {
        val routes = listOf(
            route {
                request {
                    body<Int>()
                }
            }
        )
        val schemaContext = schemaContext(routes)
        schemaContext.getSchema(Integer::class.asSchemaType()).also { schema ->
            schema.type shouldBe "integer"
            schema.format shouldBe "int32"
            schema.`$ref` shouldBe null
        }
        schemaContext.getComponentsSection().also { components ->
            components.shouldBeEmpty()
        }
    }

    "primitive array" {
        val routes = listOf(
            route {
                request {
                    body<List<String>>()
                }
            }
        )
        val schemaContext = schemaContext(routes)
        schemaContext.getSchema(getType<List<String>>()).also { schema ->
            schema.type shouldBe "array"
            schema.`$ref` shouldBe null
            schema.items.also { item ->
                item.type shouldBe "string"
            }
        }
        schemaContext.getComponentsSection().also { components ->
            components.shouldBeEmpty()
        }
    }

    "primitive deep array" {
        val routes = listOf(
            route {
                request {
                    body<List<List<List<Boolean>>>>()
                }
            }
        )
        val schemaContext = schemaContext(routes)
        schemaContext.getSchema(getType<List<List<List<Boolean>>>>()).also { schema ->
            schema.type shouldBe "array"
            schema.`$ref` shouldBe null
            schema.items.also { item0 ->
                item0.type shouldBe "array"
                item0.`$ref` shouldBe null
                item0.items.also { item1 ->
                    item1.type shouldBe "array"
                    item1.`$ref` shouldBe null
                    item1.items.also { item2 ->
                        item2.type shouldBe "boolean"
                    }
                }
            }
        }
        schemaContext.getComponentsSection().also { components ->
            components.shouldBeEmpty()
        }
    }

    "object" {
        val routes = listOf(
            route {
                request {
                    body<SimpleDataClass>()
                }
            }
        )
        val schemaContext = schemaContext(routes)
        schemaContext.getSchema(SimpleDataClass::class.asSchemaType()).also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/SimpleDataClass"
        }
        schemaContext.getComponentsSection().also { components ->
            components.keys shouldContainExactlyInAnyOrder listOf("SimpleDataClass")
            components["SimpleDataClass"]?.also { schema ->
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactlyInAnyOrder listOf("text", "number")
            }
        }
    }

    "object array" {
        val routes = listOf(
            route {
                request {
                    body<List<SimpleDataClass>>()
                }
            }
        )
        val schemaContext = schemaContext(routes)
        schemaContext.getSchema(getType<List<SimpleDataClass>>()).also { schema ->
            schema.type shouldBe "array"
            schema.`$ref` shouldBe null
            schema.items.also { item ->
                item.type shouldBe null
                item.`$ref` shouldBe "#/components/schemas/SimpleDataClass"
            }
        }
        schemaContext.getComponentsSection().also { components ->
            components.keys shouldContainExactlyInAnyOrder listOf("SimpleDataClass")
            components["SimpleDataClass"]?.also { schema ->
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactlyInAnyOrder listOf("text", "number")
            }
        }
    }

    "nested objects" {
        val routes = listOf(
            route {
                request {
                    body<DataWrapper>()
                }
            }
        )
        val schemaContext = schemaContext(routes)
        schemaContext.getSchema(DataWrapper::class.asSchemaType()).also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/DataWrapper"
        }
        schemaContext.getComponentsSection().also { components ->
            components.keys shouldContainExactlyInAnyOrder listOf("SimpleDataClass", "DataWrapper")
            components["SimpleDataClass"]?.also { schema ->
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactlyInAnyOrder listOf("text", "number")
            }
            components["DataWrapper"]?.also { schema ->
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactlyInAnyOrder listOf("data", "enabled")
                schema.properties["data"]?.also { nestedSchema ->
                    nestedSchema.type shouldBe null
                    nestedSchema.`$ref` shouldBe "#/components/schemas/SimpleDataClass"
                }
            }
        }
    }

    "simple enum" {
        val routes = listOf(
            route {
                request {
                    body<SimpleEnum>()
                }
            }
        )
        val schemaContext = schemaContext(routes)
        schemaContext.getSchema(SimpleEnum::class.asSchemaType()).also { schema ->
            schema.type shouldBe "string"
            schema.enum shouldContainExactlyInAnyOrder SimpleEnum.values().map { it.name }
            schema.`$ref` shouldBe null
        }
        schemaContext.getComponentsSection().also { components ->
            components.keys.shouldBeEmpty()
        }
    }

    "maps" {
        val routes = listOf(
            route {
                request {
                    body<DataClassWithMaps>()
                }
            }
        )
        val schemaContext = schemaContext(routes)
        schemaContext.getSchema(DataClassWithMaps::class.asSchemaType()).also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/DataClassWithMaps"
        }
        schemaContext.getComponentsSection().also { components ->
            components.keys shouldContainExactlyInAnyOrder listOf(
                "DataClassWithMaps",
                "Map(String,Long)",
                "Map(String,String)"
            )
            components["DataClassWithMaps"]?.also { schema ->
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactlyInAnyOrder listOf("mapStringValues", "mapLongValues")
                schema.properties["mapStringValues"]?.also { nestedSchema ->
                    nestedSchema.type shouldBe null
                    nestedSchema.`$ref` shouldBe "#/components/schemas/Map(String,String)"
                }
                schema.properties["mapLongValues"]?.also { nestedSchema ->
                    nestedSchema.type shouldBe null
                    nestedSchema.`$ref` shouldBe "#/components/schemas/Map(String,Long)"
                }
            }
        }
    }

    "custom schema object" {
        val config = PluginConfigDsl().also {
            it.customSchemas {
                openApi("myCustomSchema") {
                    Schema<Any>().also { schema ->
                        schema.type = "object"
                        schema.properties = mapOf(
                            "custom" to Schema<Any>().also { prop ->
                                prop.type = "string"
                            }
                        )
                    }
                }
            }
        }
        val routes = listOf(
            route {
                request {
                    body(custom("myCustomSchema"))
                }
            }
        )
        val schemaContext = schemaContext(routes, config)
        schemaContext.getSchema("myCustomSchema").also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/myCustomSchema"
        }
        schemaContext.getComponentsSection().also { components ->
            components.keys shouldContainExactlyInAnyOrder listOf(
                "myCustomSchema",
            )
            components["myCustomSchema"]?.also { schema ->
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactlyInAnyOrder listOf("custom")
            }
        }
    }

    "custom schema array" {
        val config = PluginConfigDsl().also {
            it.customSchemas {
                openApi("myCustomSchema") {
                    Schema<Any>().also { schema ->
                        schema.type = "object"
                        schema.properties = mapOf(
                            "custom" to Schema<Any>().also { prop ->
                                prop.type = "string"
                            }
                        )
                    }
                }
            }
        }
        val routes = listOf(
            route {
                request {
                    body(multipleOf(custom("myCustomSchema")))
                }
            }
        )
        val schemaContext = schemaContext(routes, config)
        schemaContext.getSchema("myCustomSchema").also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/myCustomSchema"
        }
        schemaContext.getComponentsSection().also { components ->
            components.keys shouldContainExactlyInAnyOrder listOf(
                "myCustomSchema",
            )
            components["myCustomSchema"]?.also { schema ->
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactlyInAnyOrder listOf("custom")
            }
        }
    }

    "unwrap inlined array schema" {
        val generator = SchemaGenerator(
            SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON)
                .without(Option.DEFINITIONS_FOR_ALL_OBJECTS)
                .with(Option.INLINE_ALL_SCHEMAS)
                .with(Option.EXTRA_OPEN_API_FORMAT_VALUES)
                .with(Option.ALLOF_CLEANUP_AT_THE_END)
                .build()
        )
        val config = PluginConfigDsl().also {
            it.encoding {
                schemaEncoder { type ->
                    generator.generateSchema(type.javaType).toString()
                }
            }
        }
        val routes = listOf(
            route {
                request {
                    body<List<SimpleDataClass>>()
                }
            }
        )
        val schemaContext = schemaContext(routes, config)
        schemaContext.getSchema(getSchemaType<List<SimpleDataClass>>()).also { schema ->
            schema.type shouldBe "array"
            schema.`$ref` shouldBe null
            schema.items
                .also { it shouldNotBe null }
                ?.also { items ->
                    items.`$ref` shouldBe "#/components/schemas/SimpleDataClass"
                }
        }
        schemaContext.getComponentsSection().also { components ->
            components.keys shouldContainExactlyInAnyOrder listOf(
                "SimpleDataClass",
            )
            components["SimpleDataClass"]?.also { schema ->
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactlyInAnyOrder listOf("number", "text")
            }
        }
    }

    "don't include unused custom schema" {
        val config = PluginConfigDsl().also {
            it.customSchemas {
                includeAll = false
                openApi("myCustomSchema") {
                    Schema<Any>().also { schema ->
                        schema.type = "object"
                        schema.properties = mapOf(
                            "custom" to Schema<Any>().also { prop ->
                                prop.type = "string"
                            }
                        )
                    }
                }
            }
        }
        val schemaContext = schemaContext(emptyList(), config)
        schemaContext.getSchemaOrNull("myCustomSchema") shouldBe null
        schemaContext.getComponentsSection().also { components ->
            components.keys  shouldHaveSize 0
        }
    }

    "include unused custom schema" {
        val config = PluginConfigDsl().also {
            it.customSchemas {
                includeAll = true
                openApi("myCustomSchema") {
                    Schema<Any>().also { schema ->
                        schema.type = "object"
                        schema.properties = mapOf(
                            "custom" to Schema<Any>().also { prop ->
                                prop.type = "string"
                            }
                        )
                    }
                }
            }
        }
        val schemaContext = schemaContext(emptyList(), config)
        schemaContext.getSchema("myCustomSchema").also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/myCustomSchema"
        }
        schemaContext.getComponentsSection().also { components ->
            components.keys shouldContainExactlyInAnyOrder listOf(
                "myCustomSchema",
            )
            components["myCustomSchema"]?.also { schema ->
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactlyInAnyOrder listOf("custom")
            }
        }
    }

    "array with wildcard-generic" {
        val routes = listOf(
            route {
                request {
                    body<Array<*>>()
                }
            }
        )
        val schemaContext = schemaContext(routes)
        schemaContext.getSchema(getType<Array<*>>()).also { schema ->
            schema.type shouldBe "array"
            schema.`$ref` shouldBe null
            schema.items.also { item ->
                item.`$ref` shouldBe "#/components/schemas/*"
            }
        }
        schemaContext.getComponentsSection().also { components ->
            components.keys shouldContainExactlyInAnyOrder listOf("*")
            components["*"]?.also { schema ->
                schema.type shouldBe "object"
            }
        }
    }

}) {

    companion object {

        inline fun <reified T> getType() = getSchemaType<T>()

        private val defaultPluginConfig = PluginConfigDsl()

        private fun schemaContext(
            routes: Collection<RouteMeta>,
            pluginConfig: PluginConfigDsl = defaultPluginConfig
        ): SchemaContext {
            val pluginConfigData = pluginConfig.build(PluginConfigData.DEFAULT)
            return SchemaContextBuilder(
                config = pluginConfigData,
                schemaBuilder = SchemaBuilder(
                    definitionsField = pluginConfigData.encoding.schemaDefsField,
                    schemaEncoder = pluginConfigData.encoding.schemaEncoder,
                    ObjectMapper(),
                    TypeOverwrites.get()
                )
            ).build(routes)
        }

        fun route(block: OpenApiRoute.() -> Unit): RouteMeta {
            return RouteMeta(
                path = "/test",
                method = HttpMethod.Get,
                documentation = OpenApiRoute().apply(block),
                protected = false
            )
        }

        private data class QueryParamType(val value: String)

        private data class PathParamType(val value: String)
        private data class HeaderParamType(val value: String)
        private data class RequestBodyType(val value: String)
        private data class ResponseHeaderType(val value: String)
        private data class ResponseBodyType(val value: String)

        private data class SimpleDataClass(
            val text: String,
            val number: Int
        )

        private data class DataWrapper(
            val enabled: Boolean,
            val data: SimpleDataClass
        )

        private enum class SimpleEnum {
            RED, GREEN, BLUE
        }

        private data class DataClassWithMaps(
            val mapStringValues: Map<String, String>,
            val mapLongValues: Map<String, Long>
        )

    }

}
