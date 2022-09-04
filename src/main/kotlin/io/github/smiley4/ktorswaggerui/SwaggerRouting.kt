package io.github.smiley4.ktorswaggerui

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.withCharset
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import mu.KotlinLogging
import java.net.URL

/**
 * Registers and handles routes required for the swagger-ui
 */
class SwaggerRouting(
    private val swaggerWebjarVersion: String,
    private val swaggerUrl: String,
    private val forwardRoot: Boolean,
    private val jsonSpecProvider: () -> String
) {

    private val logger = KotlinLogging.logger {}


    /**
     * registers the required routes
     */
    fun setup(app: Application) {
        logger.info("Registering routes for swagger-ui: $swaggerUrl (forwardRoot=$forwardRoot)")
        app.routing {
            if (forwardRoot) {
                get("/") {
                    call.respondRedirect("$swaggerUrl/index.html")
                }
            }
            get(swaggerUrl) {
                call.respondRedirect("$swaggerUrl/index.html")
            }
            get("$swaggerUrl/{filename}") {
                when (val filename = call.parameters["filename"]!!) {
                    "swagger-initializer.js" -> serveSwaggerInitializer(call)
                    "api.json" -> serveApiSpecJson(call)
                    else -> serveStaticResource(filename, call)
                }
            }
        }
    }


    private suspend fun serveSwaggerInitializer(call: ApplicationCall) {
        val apiSpecUrl = "/" + (if (swaggerUrl.startsWith("/")) swaggerUrl.substring(1) else swaggerUrl) + "/api.json"
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
				layout: "StandaloneLayout"
			  });
			  //</editor-fold>
			};
		""".trimIndent()
        call.respondText(ContentType.Application.JavaScript, HttpStatusCode.OK) { content }
    }


    private suspend fun serveApiSpecJson(call: ApplicationCall) {
        call.respondText(ContentType.Application.Json, HttpStatusCode.OK) { jsonSpecProvider() }
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