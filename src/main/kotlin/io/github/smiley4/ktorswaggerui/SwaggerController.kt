package io.github.smiley4.ktorswaggerui

import io.github.smiley4.ktorswaggerui.dsl.SwaggerUIDsl
import io.github.smiley4.ktorswaggerui.dsl.SwaggerUiSort
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.withCharset
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import java.net.URL

class SwaggerController(
    private val swaggerWebjarVersion: String,
    private val apiSpecUrl: String,
    private val jsonSpecProvider: () -> String,
    private val swaggerUiConfig: SwaggerUIDsl
) {

    suspend fun serveOpenApiSpec(call: ApplicationCall) {
        call.respondText(ContentType.Application.Json, HttpStatusCode.OK, jsonSpecProvider)
    }

    suspend fun serverSwaggerUI(call: ApplicationCall) {
        when (val filename = call.parameters["filename"]!!) {
            "swagger-initializer.js" -> serveSwaggerInitializer(call)
            else -> serveStaticResource(filename, call)
        }
    }

    private suspend fun serveSwaggerInitializer(call: ApplicationCall) {
        val propValidatorUrl = swaggerUiConfig.getSpecValidatorUrl()?.let { "validatorUrl: \"$it\"" } ?: "validatorUrl: false"
        val propDisplayOperationId = "displayOperationId: ${swaggerUiConfig.displayOperationId}"
        val propFilter = "filter: ${swaggerUiConfig.showTagFilterInput}"
        val propSort = "operationsSorter: " + if (swaggerUiConfig.sort == SwaggerUiSort.NONE) "undefined" else
            "\"${swaggerUiConfig.sort.value}\""
        val propSyntaxHighlight = "syntaxHighlight: { theme: \"${swaggerUiConfig.syntaxHighlight.value}\" }"
        // see https://github.com/swagger-api/swagger-ui/blob/master/docs/usage/configuration.md for reference
        val content = """
			window.onload = function() {
			  //<editor-fold desc="Changeable Configuration Block">
			  window.ui = SwaggerUIBundle({
				url: "$apiSpecUrl",
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
			  //</editor-fold>
			};
		""".trimIndent()
        call.respondText(ContentType.Application.JavaScript, HttpStatusCode.OK) { content }
    }


    private suspend fun serveStaticResource(filename: String, call: ApplicationCall) {
        val resource = this::class.java.getResource("/META-INF/resources/webjars/swagger-ui/$swaggerWebjarVersion/$filename")
        if (resource == null) {
            call.respond(HttpStatusCode.NotFound, "$filename could not be found")
        } else {
            call.respond(ResourceContent(resource))
        }
    }

}


private class ResourceContent(val resource: URL) : OutgoingContent.ByteArrayContent() {
    private val bytes by lazy { resource.readBytes() }

    override val contentType: ContentType? by lazy {
        val extension = resource.file.substring(resource.file.lastIndexOf('.') + 1)
        contentTypes[extension] ?: ContentType.Text.Html
    }

    override val contentLength: Long? by lazy {
        bytes.size.toLong()
    }

    override fun bytes(): ByteArray = bytes

    override fun toString() = "ResourceContent \"$resource\""
}


private val contentTypes = mapOf(
    "html" to ContentType.Text.Html,
    "css" to ContentType.Text.CSS,
    "js" to ContentType.Application.JavaScript,
    "json" to ContentType.Application.Json.withCharset(Charsets.UTF_8),
    "png" to ContentType.Image.PNG
)
