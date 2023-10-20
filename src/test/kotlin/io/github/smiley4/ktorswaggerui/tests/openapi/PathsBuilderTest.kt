package io.github.smiley4.ktorswaggerui.tests.openapi

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.github.smiley4.ktorswaggerui.dsl.PluginConfigDsl
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.github.smiley4.ktorswaggerui.builder.example.ExampleContext
import io.github.smiley4.ktorswaggerui.builder.example.ExampleContextBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.ContentBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.ExampleBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.HeaderBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.OperationBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.OperationTagsBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.ParameterBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.PathBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.PathsBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.RequestBodyBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.ResponseBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.ResponsesBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.SecurityRequirementsBuilder
import io.github.smiley4.ktorswaggerui.builder.route.RouteMeta
import io.github.smiley4.ktorswaggerui.builder.schema.SchemaBuilder
import io.github.smiley4.ktorswaggerui.builder.schema.SchemaContext
import io.github.smiley4.ktorswaggerui.builder.schema.SchemaContextBuilder
import io.github.smiley4.ktorswaggerui.builder.schema.TypeOverwrites
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.ktor.http.HttpMethod
import io.swagger.v3.oas.models.Paths

class PathsBuilderTest : StringSpec({

    "simple paths" {
        val routes = listOf(
            route(HttpMethod.Get, "/"),
            route(HttpMethod.Delete, "/test/path"),
            route(HttpMethod.Post, "/other/test/route")
        )
        val schemaContext = schemaContext(routes)
        val exampleContext = exampleContext(routes)
        buildPathsObject(routes, schemaContext, exampleContext).also { paths ->
            paths shouldHaveSize 3
            paths.keys shouldContainExactlyInAnyOrder listOf(
                "/",
                "/test/path",
                "/other/test/route"
            )
            paths["/"]!!.get.shouldNotBeNull()
            paths["/test/path"]!!.delete.shouldNotBeNull()
            paths["/other/test/route"]!!.post.shouldNotBeNull()
        }
    }
    "merge paths" {
        val config = defaultPluginConfig.also {
            it.swagger {
                swaggerUrl = "swagger-ui"
                forwardRoot = true
            }
        }
        val routes = listOf(
            route(HttpMethod.Get, "/different/path"),
            route(HttpMethod.Get, "/test/path"),
            route(HttpMethod.Post, "/test/path"),
        )
        val schemaContext = schemaContext(routes, config)
        val exampleContext = exampleContext(routes, config)
        buildPathsObject(routes, schemaContext, exampleContext, config).also { paths ->
            paths shouldHaveSize 2
            paths.keys shouldContainExactlyInAnyOrder listOf(
                "/different/path",
                "/test/path"
            )
            paths["/different/path"]!!.get.shouldNotBeNull()
            paths["/test/path"]!!.get.shouldNotBeNull()
            paths["/test/path"]!!.post.shouldNotBeNull()
        }
    }

}) {

    companion object {

        private fun route(method: HttpMethod, url: String) = RouteMeta(
            path = url,
            method = method,
            documentation = OpenApiRoute(),
            protected = false
        )

        private val defaultPluginConfig = PluginConfigDsl()

        private fun schemaContext(routes: List<RouteMeta>, pluginConfig: PluginConfigDsl = defaultPluginConfig): SchemaContext {
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

        private fun exampleContext(routes: List<RouteMeta>, pluginConfig: PluginConfigDsl = defaultPluginConfig): ExampleContext {
            return ExampleContextBuilder(
                exampleBuilder = ExampleBuilder(
                    config = pluginConfig.build(PluginConfigData.DEFAULT)
                )
            ).build(routes.toList())
        }

        private fun buildPathsObject(
            routes: Collection<RouteMeta>,
            schemaContext: SchemaContext,
            exampleContext: ExampleContext,
            pluginConfig: PluginConfigDsl = defaultPluginConfig
        ): Paths {
            val pluginConfigData = pluginConfig.build(PluginConfigData.DEFAULT)
            return PathsBuilder(
                pathBuilder = PathBuilder(
                    operationBuilder = OperationBuilder(
                        operationTagsBuilder = OperationTagsBuilder(pluginConfigData),
                        parameterBuilder = ParameterBuilder(
                            schemaContext = schemaContext,
                            exampleContext = exampleContext
                        ),
                        requestBodyBuilder = RequestBodyBuilder(
                            contentBuilder = ContentBuilder(
                                schemaContext = schemaContext,
                                exampleContext = exampleContext,
                                headerBuilder = HeaderBuilder(schemaContext)
                            )
                        ),
                        responsesBuilder = ResponsesBuilder(
                            responseBuilder = ResponseBuilder(
                                headerBuilder = HeaderBuilder(schemaContext),
                                contentBuilder = ContentBuilder(
                                    schemaContext = schemaContext,
                                    exampleContext = exampleContext,
                                    headerBuilder = HeaderBuilder(schemaContext)
                                )
                            ),
                            config = pluginConfigData
                        ),
                        securityRequirementsBuilder = SecurityRequirementsBuilder(pluginConfigData),
                    )
                )
            ).build(routes)
        }

    }

}
