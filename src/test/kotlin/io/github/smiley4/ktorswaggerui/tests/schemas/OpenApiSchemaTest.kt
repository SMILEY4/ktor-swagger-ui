package io.github.smiley4.ktorswaggerui.tests.schemas

import com.fasterxml.jackson.core.type.TypeReference
import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.github.smiley4.ktorswaggerui.spec.route.RouteMeta
import io.github.smiley4.ktorswaggerui.spec.schema.JsonSchemaBuilder
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaContext
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpMethod

class OpenApiSchemaTest : StringSpec({

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
        schemaContext.getSchema(QueryParamType::class.java).also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/QueryParamType"
        }
        schemaContext.getSchema(PathParamType::class.java).also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/PathParamType"
        }
        schemaContext.getSchema(HeaderParamType::class.java).also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/HeaderParamType"
        }
        schemaContext.getSchema(RequestBodyType::class.java).also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/RequestBodyType"
        }
        schemaContext.getSchema(ResponseHeaderType::class.java).also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/ResponseHeaderType"
        }
        schemaContext.getSchema(ResponseBodyType::class.java).also { schema ->
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
        schemaContext.getSchema(Integer::class.java).also { schema ->
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
                        body<Data>()
                    }
                },
                protected = false
            )
        )
        val schemaContext = schemaContext().initialize(routes)
        schemaContext.getSchema(Data::class.java).also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/Data"
        }
        schemaContext.getComponentSection().also { components ->
            components.keys shouldContainExactlyInAnyOrder listOf("Data")
            components["Data"]?.also { schema ->
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
                        body<List<Data>>()
                    }
                },
                protected = false
            )
        )
        val schemaContext = schemaContext().initialize(routes)
        schemaContext.getSchema(getType<List<Data>>()).also { schema ->
            schema.type shouldBe "array"
            schema.`$ref` shouldBe null
            schema.items.also { item ->
                item.type shouldBe null
                item.`$ref` shouldBe "#/components/schemas/Data"
            }
        }
        schemaContext.getComponentSection().also { components ->
            components.keys shouldContainExactlyInAnyOrder listOf("Data")
            components["Data"]?.also { schema ->
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
        schemaContext.getSchema(DataWrapper::class.java).also { schema ->
            schema.type shouldBe null
            schema.`$ref` shouldBe "#/components/schemas/DataWrapper"
        }
        schemaContext.getComponentSection().also { components ->
            components.keys shouldContainExactlyInAnyOrder listOf("Data", "DataWrapper")
            components["Data"]?.also { schema ->
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactlyInAnyOrder listOf("text", "number")
            }
            components["DataWrapper"]?.also { schema ->
                schema.type shouldBe "object"
                schema.properties.keys shouldContainExactlyInAnyOrder listOf("data", "enabled")
                schema.properties["data"]?.also { nestedSchema ->
                    nestedSchema.type shouldBe null
                    nestedSchema.`$ref` shouldBe "#/components/schemas/Data"
                }
            }
        }
    }

}) {

    companion object {

        inline fun <reified T> getType() = object : TypeReference<T>() {}.type

        private data class QueryParamType(val value: String)
        private data class PathParamType(val value: String)
        private data class HeaderParamType(val value: String)
        private data class RequestBodyType(val value: String)
        private data class ResponseHeaderType(val value: String)
        private data class ResponseBodyType(val value: String)

        private data class Data(
            val text: String,
            val number: Int
        )

        private data class DataWrapper(
            val enabled: Boolean,
            val data: Data
        )

        private val defaultPluginConfig = SwaggerUIPluginConfig()

        private fun schemaContext(pluginConfig: SwaggerUIPluginConfig = defaultPluginConfig): SchemaContext {
            return SchemaContext(pluginConfig, JsonSchemaBuilder(pluginConfig.schemaGeneratorConfigBuilder.build()))
        }

    }

}