package io.github.smiley4.ktorswaggerui.tests.schema

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.github.victools.jsonschema.generator.Option
import com.github.victools.jsonschema.generator.SchemaGenerator
import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.github.smiley4.ktorswaggerui.dsl.asSchemaType
import io.github.smiley4.ktorswaggerui.dsl.getSchemaType
import io.github.smiley4.ktorswaggerui.spec.route.RouteMeta
import io.github.smiley4.ktorswaggerui.spec.schemaV2.SchemaBuilder
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaContext
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import kotlin.reflect.jvm.javaType

class SchemaContextTest : StringSpec({

    "route with all schemas" {
        val routes = listOf(
            RouteMeta(
                path = "/test",
                method = HttpMethod.Get,
                documentation = OpenApiRoute().apply {
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
                },
                protected = false
            )
        )
        val schemaContext = schemaContext().initialize(routes)
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
        schemaContext.getComponentSection().also { components ->
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
            RouteMeta(
                path = "/test",
                method = HttpMethod.Get,
                documentation = OpenApiRoute().apply {
                    request {
                        body<Int>()
                    }
                },
                protected = false
            )
        )
        val schemaContext = schemaContext().initialize(routes)
        schemaContext.getSchema(Integer::class.asSchemaType()).also { schema ->
            schema.type shouldBe "integer"
            schema.format shouldBe "int32"
            schema.`$ref` shouldBe null
        }
        schemaContext.getComponentSection().also { components ->
            components.shouldBeEmpty()
        }
    }

    "primitive array" {
        val routes = listOf(
            RouteMeta(
                path = "/test",
                method = HttpMethod.Get,
                documentation = OpenApiRoute().apply {
                    request {
                        body<List<String>>()
                    }
                },
                protected = false
            )
        )
        val schemaContext = schemaContext().initialize(routes)
        schemaContext.getSchema(getType<List<String>>()).also { schema ->
            schema.type shouldBe "array"
            schema.`$ref` shouldBe null
            schema.items.also { item ->
                item.type shouldBe "string"
            }
        }
        schemaContext.getComponentSection().also { components ->
            components.shouldBeEmpty()
        }
    }

    "primitive deep array" {
        val routes = listOf(
            RouteMeta(
                path = "/test",
                method = HttpMethod.Get,
                documentation = OpenApiRoute().apply {
                    request {
                        body<List<List<List<Boolean>>>>()
                    }
                },
                protected = false
            )
        )
        val schemaContext = schemaContext().initialize(routes)
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
        schemaContext.getComponentSection().also { components ->
            components.shouldBeEmpty()
        }
    }

    "object" {
        val routes = listOf(
            RouteMeta(
                path = "/test",
                method = HttpMethod.Get,
                documentation = OpenApiRoute().apply {
                    request {
                        body<SimpleDataClass>()
                    }
                },
                protected = false
            )
        )
        val schemaContext = schemaContext().initialize(routes)
        schemaContext.getSchema(SimpleDataClass::class.asSchemaType()).also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/SimpleDataClass"
        }
        schemaContext.getComponentSection().also { components ->
            components.keys shouldContainExactlyInAnyOrder listOf("SimpleDataClass")
            components["SimpleDataClass"]?.also { schema ->
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactlyInAnyOrder listOf("text", "number")
            }
        }
    }

    "object array" {
        val routes = listOf(
            RouteMeta(
                path = "/test",
                method = HttpMethod.Get,
                documentation = OpenApiRoute().apply {
                    request {
                        body<List<SimpleDataClass>>()
                    }
                },
                protected = false
            )
        )
        val schemaContext = schemaContext().initialize(routes)
        schemaContext.getSchema(getType<List<SimpleDataClass>>()).also { schema ->
            schema.type shouldBe "array"
            schema.`$ref` shouldBe null
            schema.items.also { item ->
                item.type shouldBe null
                item.`$ref` shouldBe "#/components/schemas/SimpleDataClass"
            }
        }
        schemaContext.getComponentSection().also { components ->
            components.keys shouldContainExactlyInAnyOrder listOf("SimpleDataClass")
            components["SimpleDataClass"]?.also { schema ->
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactlyInAnyOrder listOf("text", "number")
            }
        }
    }

    "nested objects" {
        val routes = listOf(
            RouteMeta(
                path = "/test",
                method = HttpMethod.Get,
                documentation = OpenApiRoute().apply {
                    request {
                        body<DataWrapper>()
                    }
                },
                protected = false
            )
        )
        val schemaContext = schemaContext().initialize(routes)
        schemaContext.getSchema(DataWrapper::class.asSchemaType()).also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/DataWrapper"
        }
        schemaContext.getComponentSection().also { components ->
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
            RouteMeta(
                path = "/test",
                method = HttpMethod.Get,
                documentation = OpenApiRoute().apply {
                    request {
                        body<SimpleEnum>()
                    }
                },
                protected = false
            )
        )
        val schemaContext = schemaContext().initialize(routes)
        schemaContext.getSchema(SimpleEnum::class.asSchemaType()).also { schema ->
            schema.type shouldBe "string"
            schema.enum shouldContainExactlyInAnyOrder SimpleEnum.values().map { it.name }
            schema.`$ref` shouldBe null
        }
        schemaContext.getComponentSection().also { components ->
            components.keys.shouldBeEmpty()
        }
    }

    "maps" {
        val routes = listOf(
            RouteMeta(
                path = "/test",
                method = HttpMethod.Get,
                documentation = OpenApiRoute().apply {
                    request {
                        body<DataClassWithMaps>()
                    }
                },
                protected = false
            )
        )
        val schemaContext = schemaContext().initialize(routes)
        schemaContext.getSchema(DataClassWithMaps::class.asSchemaType()).also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/DataClassWithMaps"
        }
        schemaContext.getComponentSection().also { components ->
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

}) {

    companion object {

        inline fun <reified T> getType() = getSchemaType<T>()

        private val defaultPluginConfig = SwaggerUIPluginConfig().also {
            it.schemaGeneratorConfigBuilder = it.schemaGeneratorConfigBuilder.without(Option.DEFINITION_FOR_MAIN_SCHEMA)
        }

        private fun schemaContext(pluginConfig: SwaggerUIPluginConfig = defaultPluginConfig): SchemaContext {
            return SchemaContext(
                config = pluginConfig,
                schemaBuilder = SchemaBuilder("\$defs") { type ->
                    SchemaGenerator(pluginConfig.schemaGeneratorConfigBuilder.build()).generateSchema(type.javaType).toString()
                }
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

        private data class AnotherDataClass(
            val primitiveValue: Int,
            val primitiveList: List<Int>,
            private val privateValue: String,
            val nestedClass: SimpleDataClass,
            val nestedClassList: List<SimpleDataClass>
        )


        @JsonTypeInfo(
            use = JsonTypeInfo.Id.CLASS,
            include = JsonTypeInfo.As.PROPERTY,
            property = "_type",
        )
        @JsonSubTypes(
            JsonSubTypes.Type(value = SubClassA::class),
            JsonSubTypes.Type(value = SubClassB::class),
        )
        private abstract class Superclass(
            val superField: String,
        )

        private class SubClassA(
            superField: String,
            val subFieldA: Int
        ) : Superclass(superField)

        private class SubClassB(
            superField: String,
            val subFieldB: Boolean
        ) : Superclass(superField)


        private data class ClassWithNestedAbstractClass(
            val nestedClass: Superclass,
            val someField: String
        )

        private class ClassWithGenerics<T>(
            val genericField: T,
            val genericList: List<T>
        )

        private class WrapperForClassWithGenerics(
            val genericClass: ClassWithGenerics<String>
        )

    }

}