package io.github.smiley4.ktorswaggerui

import io.github.smiley4.ktorswaggerui.specbuilder.ApiSpecBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiComponentsBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiContentBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiExampleBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiInfoBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiJsonSchemaBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiOAuthFlowsBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiParametersBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiPathBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiPathsBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiRequestBodyBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiResponsesBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiSchemaBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiSecuritySchemesBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiServersBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiTagsBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.RouteCollector
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import io.ktor.server.application.install
import io.ktor.server.application.pluginOrNull
import io.ktor.server.webjars.Webjars

/**
 * This version must match the version of the gradle dependency
 */
internal const val SWAGGER_UI_WEBJARS_VERSION = "4.14.0"

val SwaggerUI = createApplicationPlugin(name = "SwaggerUI", createConfiguration = ::SwaggerUIPluginConfig) {

    var apiSpecJson = "{}"

    on(MonitoringEvent(ApplicationStarted)) { application ->
        if (application.pluginOrNull(Webjars) == null) {
            application.install(Webjars)
        }
        apiSpecJson = getBuilder().build(application, pluginConfig)
    }

    SwaggerRouting(
        SWAGGER_UI_WEBJARS_VERSION,
        pluginConfig.getSwaggerUI().swaggerUrl,
        pluginConfig.getSwaggerUI().forwardRoot
    ) { apiSpecJson }.setup(application)

}

private fun getBuilder(): ApiSpecBuilder {
    return ApiSpecBuilder(
        OApiInfoBuilder(),
        OApiServersBuilder(),
        OApiSecuritySchemesBuilder(
            OApiOAuthFlowsBuilder()
        ),
        OApiTagsBuilder(),
        OApiPathsBuilder(
            RouteCollector(),
            OApiPathBuilder(
                OApiParametersBuilder(
                    OApiSchemaBuilder(
                        OApiJsonSchemaBuilder()
                    )
                ),
                OApiRequestBodyBuilder(
                    OApiContentBuilder(
                        OApiSchemaBuilder(
                            OApiJsonSchemaBuilder()
                        ),
                        OApiExampleBuilder()
                    )
                ),
                OApiResponsesBuilder(
                    OApiContentBuilder(
                        OApiSchemaBuilder(
                            OApiJsonSchemaBuilder()
                        ),
                        OApiExampleBuilder()
                    ),
                    OApiSchemaBuilder(
                        OApiJsonSchemaBuilder()
                    )
                ),
            ),
        ),
        OApiComponentsBuilder(
            OApiExampleBuilder()
        ),
    )
}


