package io.github.smiley4.ktorswaggerui

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.github.smiley4.ktorswaggerui.dsl.PluginConfigDsl
import io.github.smiley4.ktorswaggerui.routing.ForwardRouteController
import io.github.smiley4.ktorswaggerui.routing.SwaggerController
import io.github.smiley4.ktorswaggerui.builder.example.ExampleContext
import io.github.smiley4.ktorswaggerui.builder.example.ExampleContextBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.ComponentsBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.ContactBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.ContentBuilder
import io.github.smiley4.ktorswaggerui.builder.openapi.ExampleBuilder
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
import io.github.smiley4.ktorswaggerui.builder.route.RouteCollector
import io.github.smiley4.ktorswaggerui.builder.route.RouteDocumentationMerger
import io.github.smiley4.ktorswaggerui.builder.route.RouteMeta
import io.github.smiley4.ktorswaggerui.builder.schema.SchemaBuilder
import io.github.smiley4.ktorswaggerui.builder.schema.SchemaContext
import io.github.smiley4.ktorswaggerui.builder.schema.SchemaContextBuilder
import io.github.smiley4.ktorswaggerui.builder.schema.TypeOverwrites
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
internal const val SWAGGER_UI_WEBJARS_VERSION = "5.9.0"

private val logger = KotlinLogging.logger {}

val SwaggerUI = createApplicationPlugin(name = "SwaggerUI", createConfiguration = ::PluginConfigDsl) {

    val config = pluginConfig.build(PluginConfigData.DEFAULT)

    on(MonitoringEvent(ApplicationStarted)) { application ->

        if (application.pluginOrNull(Webjars) == null) {
            application.install(Webjars)
        }

        val apiSpecsJson = mutableMapOf<String, String>()
        try {
            val routes = routes(application, config)
            apiSpecsJson.putAll(buildOpenApiSpecs(config, routes))
        } catch (e: Exception) {
            logger.error("Error during application startup in swagger-ui-plugin", e)
        }

        apiSpecsJson.forEach { (specId, json) ->
            val specConfig = config.specConfigs[specId] ?: config
            SwaggerController(
                applicationConfig!!,
                specConfig,
                SWAGGER_UI_WEBJARS_VERSION,
                if (apiSpecsJson.size > 1) specId else null,
                json
            ).setup(application)
        }

        if (apiSpecsJson.size == 1 && config.swaggerUI.forwardRoot) {
            ForwardRouteController(applicationConfig!!, config).setup(application)
        }

    }
}

private fun buildOpenApiSpecs(config: PluginConfigData, routes: List<RouteMeta>): Map<String, String> {
    val routesBySpec = buildMap<String, MutableList<RouteMeta>> {
        routes.forEach { route ->
            val specName = route.documentation.specId ?: config.specAssigner(route.path, route.documentation.tags)
            computeIfAbsent(specName) { mutableListOf() }.add(route)
        }
    }
    return buildMap {
        routesBySpec.forEach { (specName, routes) ->
            val specConfig = config.specConfigs[specName] ?: config
            this[specName] = buildOpenApiSpec(specConfig, routes)
        }
    }
}

private fun buildOpenApiSpec(pluginConfig: PluginConfigData, routes: List<RouteMeta>): String {
    return try {
        val schemaContext = schemaContext(pluginConfig, routes)
        val exampleContext = exampleContext(pluginConfig, routes)
        val openApi = builder(pluginConfig, schemaContext, exampleContext).build(routes)
        pluginConfig.whenBuildOpenApiSpecs?.invoke(openApi)
        Json.pretty(openApi)
    } catch (e: Exception) {
        logger.error("Error during openapi-generation", e)
        "{}"
    }
}

private fun routes(application: Application, config: PluginConfigData): List<RouteMeta> {
    return RouteCollector(RouteDocumentationMerger())
        .collectRoutes({ application.plugin(Routing) }, config)
        .toList()
}

private fun schemaContext(config: PluginConfigData, routes: List<RouteMeta>): SchemaContext {
    return SchemaContextBuilder(
        config = config,
        schemaBuilder = SchemaBuilder(
            definitionsField = config.encoding.schemaDefsField,
            schemaEncoder = config.encoding.schemaEncoder,
            ObjectMapper(),
            TypeOverwrites.get()
        ),
    ).build(routes.toList())
}

private fun exampleContext(config: PluginConfigData, routes: List<RouteMeta>): ExampleContext {
    return ExampleContextBuilder(
        exampleBuilder = ExampleBuilder(
            config = config
        )
    ).build(routes.toList())
}

private fun builder(config: PluginConfigData, schemaContext: SchemaContext, exampleContext: ExampleContext): OpenApiBuilder {
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
