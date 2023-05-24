package io.github.smiley4.ktorswaggerui

import com.github.victools.jsonschema.generator.SchemaGenerator
import io.github.smiley4.ktorswaggerui.spec.openapi.*
import io.github.smiley4.ktorswaggerui.spec.route.RouteCollector
import io.github.smiley4.ktorswaggerui.spec.route.RouteDocumentationMerger
import io.github.smiley4.ktorswaggerui.spec.route.RouteMeta
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaBuilder
import io.github.smiley4.ktorswaggerui.spec.schema.SchemaContext
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.routing.*
import io.ktor.server.webjars.*
import io.swagger.v3.core.util.Json
import kotlin.reflect.jvm.javaType

/**
 * This version must match the version of the gradle dependency
 */
internal const val SWAGGER_UI_WEBJARS_VERSION = "4.15.0"

val SwaggerUI = createApplicationPlugin(name = "SwaggerUI", createConfiguration = ::SwaggerUIPluginConfig) {

    var apiSpecJson = "{}"

    on(MonitoringEvent(ApplicationStarted)) { application ->
        if (application.pluginOrNull(Webjars) == null) {
            application.install(Webjars)
        }
        val routes = routes(application, pluginConfig)
        val schemaContext = schemaContext(pluginConfig, routes)
        apiSpecJson = Json.pretty(builder(pluginConfig, schemaContext).build(routes))
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
    return SchemaContext(
        config = pluginConfig,
        schemaBuilder = SchemaBuilder("\$defs") { type ->
            SchemaGenerator(pluginConfig.schemaGeneratorConfigBuilder.build()).generateSchema(type.javaType).toString()
        }
    ).initialize(routes.toList())
}

private fun builder(config: SwaggerUIPluginConfig, schemaContext: SchemaContext): OpenApiBuilder {
    return OpenApiBuilder(
        config = config,
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
                    operationTagsBuilder = OperationTagsBuilder(config),
                    parameterBuilder = ParameterBuilder(schemaContext),
                    requestBodyBuilder = RequestBodyBuilder(
                        contentBuilder = ContentBuilder(
                            schemaContext = schemaContext,
                            exampleBuilder = ExampleBuilder(
                                config = config
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
                                    config = config
                                ),
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






























