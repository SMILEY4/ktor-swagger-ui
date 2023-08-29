package io.github.smiley4.ktorswaggerui

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.smiley4.ktorswaggerui.spec.example.ExampleContext
import io.github.smiley4.ktorswaggerui.spec.example.ExampleContextBuilder
import io.github.smiley4.ktorswaggerui.spec.openapi.*
import io.github.smiley4.ktorswaggerui.spec.route.RouteCollector
import io.github.smiley4.ktorswaggerui.spec.route.RouteDocumentationMerger
import io.github.smiley4.ktorswaggerui.spec.route.RouteMeta
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaBuilder
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaContext
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaContextBuilder
import io.github.smiley4.ktorswaggerui.spec.schema.TypeOverwrites
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.routing.*
import io.ktor.server.webjars.*
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
        try {
            val routes = routes(application, pluginConfig)
            val schemaContext = schemaContext(pluginConfig, routes)
            val exampleContext = exampleContext(pluginConfig, routes)
            val openApi = builder(pluginConfig, schemaContext, exampleContext).build(routes)
            apiSpecJson = Json.pretty(openApi)
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
            schemaEncoder = pluginConfig.encodingConfig.getSchemaEncoder(),
            ObjectMapper(),
            TypeOverwrites.get()
        ),
    ).build(routes.toList())
}

private fun exampleContext(pluginConfig: SwaggerUIPluginConfig, routes: List<RouteMeta>): ExampleContext {
    return ExampleContextBuilder(
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
        externalDocumentationBuilder = ExternalDocumentationBuilder(),
        serverBuilder = ServerBuilder(),
        tagBuilder = TagBuilder(
            tagExternalDocumentationBuilder = TagExternalDocumentationBuilder()
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
