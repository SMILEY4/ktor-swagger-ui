package de.lruegner.ktorswaggerui.routing

import com.github.victools.jsonschema.generator.OptionPreset
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder
import com.github.victools.jsonschema.generator.SchemaVersion
import com.github.victools.jsonschema.module.jackson.JacksonModule
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
import java.net.URL

class SwaggerRouting(
    private val swaggerWebjarVersion: String,
    private val swaggerUrl: String,
    private val forwardRoot: Boolean,
    private val jsonSpecProvider: () -> String
) {

    fun setup(app: Application) {
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
                when (val filename = call.parameters["filename"]) {
                    "swagger-initializer.js" -> serveSwaggerInitializer(call)
                    "apiSpec.json" -> serveSpecJson(call)
                    null -> call.respond(HttpStatusCode.BadRequest, "no filename provided")
                    else -> serveStaticResource(filename, call)
                }
            }
            get("$swaggerUrl/schemas/{schemaname}") {
                val schemaName = call.parameters["schemaname"]!!
                val className = schemaName.replace("__", ".")
                println("generating json-schema for class: $className")
                val clazz = Class.forName(className)
                call.respondText(ContentType.Application.Json, HttpStatusCode.OK) {
                    generateJsonSchema(clazz)
                }
            }
        }
    }


    private suspend fun serveSwaggerInitializer(call: ApplicationCall) {
		val apiSpecUrl = "/" + (if (swaggerUrl.startsWith("/")) swaggerUrl.substring(1) else swaggerUrl) + "/apiSpec.json"
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

    private suspend fun serveSpecJson(call: ApplicationCall) {
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

    private fun <T> generateJsonSchema(type: Class<T>): String {
        val module = JacksonModule()
        val configBuilder = SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON).with(module)
        val config = configBuilder.build()
        val generator = SchemaGenerator(config)
        val jsonSchema = generator.generateSchema(type)
        return jsonSchema.toString()
    }

}


private val contentTypes = mapOf(
    "html" to ContentType.Text.Html,
    "css" to ContentType.Text.CSS,
    "js" to ContentType.Application.JavaScript,
    "json" to ContentType.Application.Json.withCharset(Charsets.UTF_8),
    "png" to ContentType.Image.PNG
)


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