package io.github.smiley4.ktorswaggerui.routing

import io.github.smiley4.ktorswaggerui.dsl.SwaggerUIDsl
import io.github.smiley4.ktorswaggerui.dsl.SwaggerUiSort
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

class SwaggerController(
    private val appConfig: ApplicationConfig,
    private val swaggerUiConfig: SwaggerUIDsl,
    private val swaggerWebjarVersion: String,
    private val specName: String,
    private val jsonSpec: String,
) {

    fun setup(app: Application) {
        app.routing {
            if (swaggerUiConfig.authentication == null) {
                setup()
            } else {
                authenticate(swaggerUiConfig.authentication) {
                    setup()
                }
            }
        }
    }

    private fun Route.setup() {
        route(getSubUrl()) {
            get {
                call.respondRedirect("${getSubUrl()}/index.html")
            }
            get("{filename}") {
                serveStaticResource(call.parameters["filename"]!!, call)
            }
            get("swagger-initializer.js") {
                serveSwaggerInitializer(call)
            }
            get("$specName.json") {
                serveOpenApiSpec(call)
            }
        }
    }

    private suspend fun serveSwaggerInitializer(call: ApplicationCall) {
        // see https://github.com/swagger-api/swagger-ui/blob/master/docs/usage/configuration.md for reference
        val propValidatorUrl = swaggerUiConfig.getSpecValidatorUrl()?.let { "validatorUrl: \"$it\"" } ?: "validatorUrl: false"
        val propDisplayOperationId = "displayOperationId: ${swaggerUiConfig.displayOperationId}"
        val propFilter = "filter: ${swaggerUiConfig.showTagFilterInput}"
        val propSort = "operationsSorter: " +
                if (swaggerUiConfig.sort == SwaggerUiSort.NONE) "undefined"
                else "\"${swaggerUiConfig.sort.value}\""
        val propSyntaxHighlight = "syntaxHighlight: { theme: \"${swaggerUiConfig.syntaxHighlight.value}\" }"
        val content = """
			window.onload = function() {
			  window.ui = SwaggerUIBundle({
				url: "${getRootUrl(appConfig)}/$specName.json",
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
        val rootPath = appConfig.propertyOrNull("ktor.deployment.rootPath")?.getString()?.let { "/${dropSlashes(it)}" } ?: ""
        return "$rootPath${swaggerUiConfig.rootHostPath}/${dropSlashes(swaggerUiConfig.swaggerUrl)}/$specName"
    }

    private fun getSubUrl(): String {
        return "${swaggerUiConfig.rootHostPath}/${dropSlashes(swaggerUiConfig.swaggerUrl)}/$specName"
    }

    private fun dropSlashes(str: String): String {
        var value = str
        value = if (value.startsWith("/")) value.substring(1) else value
        value = if (value.endsWith("/")) value.substring(0, value.length - 1) else value
        return value
    }

}
