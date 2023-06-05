package io.github.smiley4.ktorswaggerui

import io.github.smiley4.ktorswaggerui.spec.example.ExampleContext
import io.github.smiley4.ktorswaggerui.spec.example.ExampleContextBuilder
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
import io.github.smiley4.ktorswaggerui.spec.route.RouteCollector
import io.github.smiley4.ktorswaggerui.spec.route.RouteDocumentationMerger
import io.github.smiley4.ktorswaggerui.spec.route.RouteMeta
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaBuilder
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaContext
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaContextBuilder
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import io.ktor.server.application.install
import io.ktor.server.application.plugin
import io.ktor.server.application.pluginOrNull
import io.ktor.server.routing.Routing
import io.ktor.server.webjars.Webjars
import io.swagger.v3.core.util.Json
import mu.KotlinLogging

/**
 * This version must match the version of the gradle dependency
 */
internal const val SWAGGER_UI_WEBJARS_VERSION = "4.15.0"

private val logger = KotlinLogging.logger {}

val SwaggerUI = createApplicationPlugin(name = "SwaggerUI", createConfiguration = ::SwaggerUIPluginConfig) {
    var apiSpecJson = "{}"
    on(MonitoringEvent(ApplicationStarted)) { application ->
        if (application.pluginOrNull(Webjars) == null) {
            application.install(Webjars)
        }
        val routes = routes(application, pluginConfig)
        val schemaContext = schemaContext(pluginConfig, routes)
        val exampleContext = exampleContext(pluginConfig, routes)
        try {
            apiSpecJson = Json.pretty(builder(pluginConfig, schemaContext, exampleContext).build(routes))
        } catch (e: Exception) {
            logger.error("Error during openapi-generation", e)
        }
    }
    SwaggerRouting(
        pluginConfig.getSwaggerUI(),
        application.environment.config,
        SWAGGER_UI_WEBJARS_VERSION,
    ) { apiSpecJson }.setup(application)
}

private fun routes(application: Application, pluginConfig: SwaggerUIPluginConfig): List<RouteMeta> {
    return RouteCollector(RouteDocumentationMerger())
        .collectRoutes({ application.plugin(Routing) }, pluginConfig)
        .toList()
}

private fun schemaContext(pluginConfig: SwaggerUIPluginConfig, routes: List<RouteMeta>): SchemaContext {
    return SchemaContextBuilder(
        config = pluginConfig,
        schemaBuilder = SchemaBuilder(
            definitionsField = pluginConfig.encodingConfig.schemaDefinitionsField,
            schemaEncoder = pluginConfig.encodingConfig.getSchemaEncoder()
        ),
    ).build(routes.toList())
}

private fun exampleContext(pluginConfig: SwaggerUIPluginConfig, routes: List<RouteMeta>): ExampleContext {
    return ExampleContextBuilder(
        config = pluginConfig,
        exampleBuilder = ExampleBuilder(
            config = pluginConfig
        )
    ).build(routes.toList())
}

private fun builder(config: SwaggerUIPluginConfig, schemaContext: SchemaContext, exampleContext: ExampleContext): OpenApiBuilder {
    return OpenApiBuilder(
        config = config,
        schemaContext = schemaContext,
        exampleContext = exampleContext,
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
                    operationTagsBuilder = OperationTagsBuilder(config),
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
                        config = config
                    ),
                    securityRequirementsBuilder = SecurityRequirementsBuilder(config),
                )
            )
        ),
        componentsBuilder = ComponentsBuilder(
            config = config,
            securitySchemesBuilder = SecuritySchemesBuilder(
                oAuthFlowsBuilder = OAuthFlowsBuilder()
            )
        )
    )
}






























