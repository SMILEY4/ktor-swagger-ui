package io.github.smiley4.ktorswaggerui.builder

import io.github.smiley4.ktorswaggerui.builder.example.ExampleContext
import io.github.smiley4.ktorswaggerui.builder.example.ExampleContextImpl
import io.github.smiley4.ktorswaggerui.builder.openapi.ComponentsBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.ContactBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.ContentBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.ExternalDocumentationBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.HeaderBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.InfoBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.LicenseBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.OAuthFlowsBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.OpenApiBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.OperationBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.OperationTagsBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.ParameterBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.PathBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.PathsBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.RequestBodyBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.ResponseBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.ResponsesBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.SecurityRequirementsBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.SecuritySchemesBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.ServerBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.TagBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.TagExternalDocumentationBuilder
import io.github.smiley4.ktorswaggerui.builder.route.RouteMeta
import io.github.smiley4.ktorswaggerui.builder.schema.SchemaContext
import io.github.smiley4.ktorswaggerui.builder.schema.SchemaContextImpl
import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.github.smiley4.ktorswaggerui.dsl.config.PluginConfigDsl
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
            openapi.servers.find { it.url == "http://localhost:8080" }!!.also { server ->
                server.url shouldBe "http://localhost:8080"
                server.description shouldBe "Development Server"
                server.variables shouldBe null
            }
            openapi.servers.find { it.url == "https://127.0.0.1" }!!.also { server ->
                server.url shouldBe "https://127.0.0.1"
                server.description shouldBe "Production Server"
                server.variables shouldBe null
            }
        }
    }

    "multiple tags" {
        val config = PluginConfigDsl().also {
            it.tags {
                tag("tag-1") {
                    description = "first test tag"
                }
                tag("tag-2") {
                    description = "second test tag"
                }
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
            return SchemaContextImpl(pluginConfigData.schemaConfig).also {
                it.addGlobal(pluginConfigData.schemaConfig)
                it.add(routes)
            }
        }

        private fun exampleContext(routes: List<RouteMeta>, pluginConfig: PluginConfigDsl): ExampleContext {
            val pluginConfigData = pluginConfig.build(PluginConfigData.DEFAULT)
            return ExampleContextImpl(pluginConfigData.exampleConfig.exampleEncoder).also {
                it.addShared(pluginConfigData.exampleConfig)
                it.add(routes)
            }
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
                            externalDocumentationBuilder = ExternalDocumentationBuilder(),
                            serverBuilder = ServerBuilder()
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
