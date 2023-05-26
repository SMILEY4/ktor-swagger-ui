package io.github.smiley4.ktorswaggerui.tests.openapi

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.github.smiley4.ktorswaggerui.spec.example.ExampleContext
import io.github.smiley4.ktorswaggerui.spec.example.ExampleContextBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.ContentBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.ExampleBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.HeaderBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.OperationBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.OperationTagsBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.ParameterBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.PathBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.PathsBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.RequestBodyBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.ResponseBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.ResponsesBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.SecurityRequirementsBuilder
import io.github.smiley4.ktorswaggerui.spec.route.RouteMeta
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaBuilder
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaContext
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaContextBuilder
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

        private val defaultPluginConfig = SwaggerUIPluginConfig()

        private fun schemaContext(routes: List<RouteMeta>, pluginConfig: SwaggerUIPluginConfig = defaultPluginConfig): SchemaContext {
            return SchemaContextBuilder(
                config = pluginConfig,
                schemaBuilder = SchemaBuilder(
                    definitionsField = pluginConfig.encodingConfig.schemaDefinitionsField,
                    schemaEncoder = pluginConfig.encodingConfig.getSchemaEncoder()
                )
            ).build(routes)
        }

        private fun exampleContext(routes: List<RouteMeta>, pluginConfig: SwaggerUIPluginConfig = defaultPluginConfig): ExampleContext {
            return ExampleContextBuilder(
                config = pluginConfig,
                exampleBuilder = ExampleBuilder(
                    config = pluginConfig
                )
            ).build(routes.toList())
        }

        private fun buildPathsObject(
            routes: Collection<RouteMeta>,
            schemaContext: SchemaContext,
            exampleContext: ExampleContext,
            pluginConfig: SwaggerUIPluginConfig = defaultPluginConfig
        ): Paths {
            return PathsBuilder(
                pathBuilder = PathBuilder(
                    operationBuilder = OperationBuilder(
                        operationTagsBuilder = OperationTagsBuilder(pluginConfig),
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
                            config = pluginConfig
                        ),
                        securityRequirementsBuilder = SecurityRequirementsBuilder(pluginConfig),
                    )
                )
            ).build(routes)
        }

    }

}