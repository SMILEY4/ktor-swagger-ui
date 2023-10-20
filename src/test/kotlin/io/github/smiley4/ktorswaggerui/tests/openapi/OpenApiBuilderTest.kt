package io.github.smiley4.ktorswaggerui.tests.openapi

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.github.smiley4.ktorswaggerui.dsl.PluginConfigDsl
import io.github.smiley4.ktorswaggerui.builder.example.ExampleContext
import io.github.smiley4.ktorswaggerui.builder.example.ExampleContextBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.*
import io.github.smiley4.ktorswaggerui.builder.route.RouteMeta
import io.github.smiley4.ktorswaggerui.builder.schema.SchemaBuilder
import io.github.smiley4.ktorswaggerui.builder.schema.SchemaContext
import io.github.smiley4.ktorswaggerui.builder.schema.SchemaContextBuilder
import io.github.smiley4.ktorswaggerui.builder.schema.TypeOverwrites
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.swagger.v3.oas.models.OpenAPI


class OpenApiBuilderTest : StringSpec({

    "default openapi object" {
        buildOpenApiObject(emptyList()).also { openapi ->
            openapi.info shouldNotBe null
            openapi.extensions shouldBe null
            openapi.servers shouldHaveSize 0
            openapi.externalDocs shouldNotBe null
            openapi.security shouldBe null
            openapi.tags shouldHaveSize 0
            openapi.paths shouldHaveSize 0
            openapi.components shouldNotBe null
            openapi.extensions shouldBe null
        }
    }

    "multiple servers" {
        val config = PluginConfigDsl().also {
            it.server {
                url = "http://localhost:8080"
                description = "Development Server"
            }
            it.server {
                url = "https://127.0.0.1"
                description = "Production Server"
            }
        }
        buildOpenApiObject(emptyList(), config).also { openapi ->
            openapi.servers shouldHaveSize 2
            openapi.servers.map { it.url } shouldContainExactlyInAnyOrder listOf(
                "http://localhost:8080",
                "https://127.0.0.1"
            )
        }
    }

    "multiple tags" {
        val config = PluginConfigDsl().also {
            it.tag("tag-1") {
                description = "first test tag"
            }
            it.tag("tag-2") {
                description = "second test tag"
            }
        }
        buildOpenApiObject(emptyList(), config).also { openapi ->
            openapi.tags shouldHaveSize 2
            openapi.tags.map { it.name } shouldContainExactlyInAnyOrder listOf(
                "tag-1",
                "tag-2"
            )
        }
    }

}) {

    companion object {

        private val defaultPluginConfig = PluginConfigDsl()

        private fun schemaContext(routes: List<RouteMeta>, pluginConfig: PluginConfigDsl): SchemaContext {
            val pluginConfigData = pluginConfig.build(PluginConfigData.DEFAULT)
            return SchemaContextBuilder(
                config =pluginConfigData,
                schemaBuilder = SchemaBuilder(
                    definitionsField = pluginConfigData.encoding.schemaDefsField,
                    schemaEncoder = pluginConfigData.encoding.schemaEncoder,
                    ObjectMapper(),
                    TypeOverwrites.get()
                )
            ).build(routes)
        }

        private fun exampleContext(routes: List<RouteMeta>, pluginConfig: PluginConfigDsl): ExampleContext {
            return ExampleContextBuilder(
                exampleBuilder = ExampleBuilder(
                    config = pluginConfig.build(PluginConfigData.DEFAULT)
                )
            ).build(routes.toList())
        }

        private fun buildOpenApiObject(routes: List<RouteMeta>, pluginConfig: PluginConfigDsl = defaultPluginConfig): OpenAPI {
            val schemaContext = schemaContext(routes, pluginConfig)
            val exampleContext = exampleContext(routes, pluginConfig)
            val pluginConfigData = pluginConfig.build(PluginConfigData.DEFAULT)
            return OpenApiBuilder(
                config = pluginConfigData,
                schemaContext = schemaContext,
                exampleContext = exampleContext,
                infoBuilder = InfoBuilder(
                    contactBuilder = ContactBuilder(),
                    licenseBuilder = LicenseBuilder()
                ),
                externalDocumentationBuilder = ExternalDocumentationBuilder(),
                serverBuilder = ServerBuilder(),
                tagBuilder = TagBuilder(
                    tagExternalDocumentationBuilder = TagExternalDocumentationBuilder()
                ),
                pathsBuilder = PathsBuilder(
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
                ),
                componentsBuilder = ComponentsBuilder(
                    config = pluginConfigData,
                    securitySchemesBuilder = SecuritySchemesBuilder(
                        oAuthFlowsBuilder = OAuthFlowsBuilder()
                    )
                )
            ).build(routes)
        }
    }

}
