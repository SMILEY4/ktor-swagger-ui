package io.github.smiley4.ktorswaggerui.routing

import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.github.smiley4.ktorswaggerui.data.SwaggerUiSort
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

class SwaggerController(
    private val appConfig: ApplicationConfig,
    private val pluginConfig: PluginConfigData,
    private val swaggerWebjarVersion: String,
    private val specName: String?,
    private val jsonSpec: String,
) {

    companion object {
        const val DEFAULT_SPEC_NAME: String = "api"
    }

    fun setup(app: Application) {
        app.routing {
            if (pluginConfig.swaggerUI.authentication == null) {
                setup()
            } else {
                authenticate(pluginConfig.swaggerUI.authentication) {
                    setup()
                }
            }
        }
    }

    private fun Route.setup() {
        route(getSubUrl()) {
            get {
                val rootHostPath = if (pluginConfig.swaggerUI.rootHostPath.isNotBlank()) {
                    ControllerUtils.dropSlashes("/${pluginConfig.swaggerUI.rootHostPath}")
                } else ""
                call.respondRedirect("$rootHostPath${call.request.uri}/index.html")
            }
            get("{filename}") {
                serveStaticResource(call.parameters["filename"]!!, call)
            }
            get("swagger-initializer.js") {
                serveSwaggerInitializer(call)
            }
            get("${specName ?: DEFAULT_SPEC_NAME}.json") {
                serveOpenApiSpec(call)
            }
        }
    }

    private suspend fun serveSwaggerInitializer(call: ApplicationCall) {
        val swaggerUiConfig = pluginConfig.swaggerUI
        // see https://github.com/swagger-api/swagger-ui/blob/master/docs/usage/configuration.md for reference
        val propValidatorUrl = swaggerUiConfig.validatorUrl?.let { "validatorUrl: \"$it\"" } ?: "validatorUrl: false"
        val propDisplayOperationId = "displayOperationId: ${swaggerUiConfig.displayOperationId}"
        val propFilter = "filter: ${swaggerUiConfig.showTagFilterInput}"
        val propSort = "operationsSorter: " +
                if (swaggerUiConfig.sort == SwaggerUiSort.NONE) "undefined"
                else "\"${swaggerUiConfig.sort.value}\""
        val propSyntaxHighlight = "syntaxHighlight: { theme: \"${swaggerUiConfig.syntaxHighlight.value}\" }"
        val content = """
			window.onload = function() {
			  window.ui = SwaggerUIBundle({
				url: "${getRootUrl(appConfig)}/${specName ?: DEFAULT_SPEC_NAME}.json",
				dom_id: '#swagger-ui',
				deepLinking: true,
				presets: [
				  SwaggerUIBundle.presets.apis,
				  SwaggerUIStandalonePreset
				],
				plugins: [
				  SwaggerUIBundle.plugins.DownloadUrl
				],
				layout: "StandaloneLayout",
				withCredentials: ${swaggerUiConfig.withCredentials},
				$propValidatorUrl,
  				$propDisplayOperationId,
    		    $propFilter,
    		    $propSort,
				$propSyntaxHighlight
			  });
			};
		""".trimIndent()
        call.respondText(ContentType.Application.JavaScript, HttpStatusCode.OK) { content }
    }

    private suspend fun serveOpenApiSpec(call: ApplicationCall) {
        call.respondText(ContentType.Application.Json, HttpStatusCode.OK) { jsonSpec }
    }

    private suspend fun serveStaticResource(filename: String, call: ApplicationCall) {
        val resource = this::class.java.getResource("/META-INF/resources/webjars/swagger-ui/$swaggerWebjarVersion/$filename")
        if (resource != null) {
            call.respond(ResourceContent(resource))
        } else {
            call.respond(HttpStatusCode.NotFound, "$filename could not be found")
        }
    }

    private fun getRootUrl(appConfig: ApplicationConfig): String {
        return "${ControllerUtils.getRootPath(appConfig)}${getSubUrl(true)}"
    }

    private fun getSubUrl(withRootHostPath: Boolean = false): String {
        return "/" + listOfNotNull(
            if (withRootHostPath) pluginConfig.swaggerUI.rootHostPath else null,
            pluginConfig.swaggerUI.swaggerUrl,
            specName
        )
            .filter { it.isNotBlank() }
            .map { ControllerUtils.dropSlashes(it) }
            .joinToString("/")
    }


}
