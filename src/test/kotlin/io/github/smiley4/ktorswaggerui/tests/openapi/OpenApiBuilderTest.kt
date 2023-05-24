package io.github.smiley4.ktorswaggerui.tests.openapi

import com.github.victools.jsonschema.generator.SchemaGenerator
import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.spec.openapi.ComponentsBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.ContactBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.ContentBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.ExampleBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.ExternalDocumentationBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.HeaderBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.InfoBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.LicenseBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.OAuthFlowsBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.OpenApiBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.OperationBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.OperationTagsBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.ParameterBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.PathBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.PathsBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.RequestBodyBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.ResponseBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.ResponsesBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.SecurityRequirementsBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.SecuritySchemesBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.ServerBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.TagBuilder
import io.github.smiley4.ktorswaggerui.spec.route.RouteMeta
import io.github.smiley4.ktorswaggerui.spec.schemaV2.SchemaBuilder
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaContext
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.swagger.v3.oas.models.OpenAPI
import kotlin.reflect.jvm.javaType


class OpenApiBuilderTest : StringSpec({

    "default openapi object" {
        buildOpenApiObject(emptyList()).also { openapi ->
            openapi.info shouldNotBe null
            openapi.extensions shouldBe null
            openapi.servers shouldHaveSize 0
            openapi.security shouldBe null
            openapi.tags shouldHaveSize 0
            openapi.paths shouldHaveSize 0
            openapi.components shouldNotBe null
            openapi.extensions shouldBe null
        }
    }

    "multiple servers" {
        val config = SwaggerUIPluginConfig().also {
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
        val config = SwaggerUIPluginConfig().also {
            it.tag("tag-1") {
                description = "first test tag"
            }
            it.tag("tag-2") {
                description = "second test tag"
            }
        }
        buildOpenApiObject(emptyList(), config).also { openapi ->
            openapi.tags shouldHaveSize 2
            openapi.tags.map { it.name} shouldContainExactlyInAnyOrder listOf(
                "tag-1",
                "tag-2"
            )
        }
    }

}) {

    companion object {

        private val defaultPluginConfig = SwaggerUIPluginConfig()

        private fun schemaContext(pluginConfig: SwaggerUIPluginConfig): SchemaContext {
            return SchemaContext(
                config = pluginConfig,
                schemaBuilder = SchemaBuilder("\$defs") { type ->
                    SchemaGenerator(pluginConfig.schemaGeneratorConfigBuilder.build()).generateSchema(type.javaType).toString()
                }
            )
        }

        private fun buildOpenApiObject(routes: List<RouteMeta>, pluginConfig: SwaggerUIPluginConfig = defaultPluginConfig): OpenAPI {
            val schemaContext = schemaContext(pluginConfig).initialize(routes)
            return OpenApiBuilder(
                config = pluginConfig,
                schemaContext = schemaContext,
                infoBuilder = InfoBuilder(
                    contactBuilder = ContactBuilder(),
                    licenseBuilder = LicenseBuilder()
                ),
                serverBuilder = ServerBuilder(),
                tagBuilder = TagBuilder(
                    externalDocumentationBuilder = ExternalDocumentationBuilder()
                ),
                pathsBuilder = PathsBuilder(
                    pathBuilder = PathBuilder(
                        operationBuilder = OperationBuilder(
                            operationTagsBuilder = OperationTagsBuilder(pluginConfig),
                            parameterBuilder = ParameterBuilder(schemaContext),
                            requestBodyBuilder = RequestBodyBuilder(
                                contentBuilder = ContentBuilder(
                                    schemaContext = schemaContext,
                                    exampleBuilder = ExampleBuilder(
                                        config = pluginConfig
                                    ),
                                    headerBuilder = HeaderBuilder(schemaContext)
                                )
                            ),
                            responsesBuilder = ResponsesBuilder(
                                responseBuilder = ResponseBuilder(
                                    headerBuilder = HeaderBuilder(schemaContext),
                                    contentBuilder = ContentBuilder(
                                        schemaContext = schemaContext,
                                        exampleBuilder = ExampleBuilder(
                                            config = pluginConfig
                                        ),
                                        headerBuilder = HeaderBuilder(schemaContext)
                                    )
                                ),
                                config = pluginConfig
                            ),
                            securityRequirementsBuilder = SecurityRequirementsBuilder(pluginConfig),
                        )
                    )
                ),
                componentsBuilder = ComponentsBuilder(
                    config = pluginConfig,
                    securitySchemesBuilder = SecuritySchemesBuilder(
                        oAuthFlowsBuilder = OAuthFlowsBuilder()
                    )
                )
            ).build(routes)
        }
    }

}