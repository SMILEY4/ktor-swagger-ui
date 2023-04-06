package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.ktor.server.application.Application
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.Paths
import mu.KotlinLogging

/**
 * Builder for the OpenAPI Paths
 */
class OApiPathsBuilder(private val routeCollector: RouteCollector) {

    private val pathBuilder = OApiPathBuilder()
    private val logger = KotlinLogging.logger {}

    fun build(config: SwaggerUIPluginConfig, application: Application, components: ComponentsContext): Paths {
        return Paths().apply {
            routeCollector.collectRoutes(application, config)
                .filter { removeLeadingSlash(it.path) != removeLeadingSlash(config.getSwaggerUI().swaggerUrl) }
                .filter { removeLeadingSlash(it.path) != removeLeadingSlash("${config.getSwaggerUI().swaggerUrl}/api.json") }
                .filter { removeLeadingSlash(it.path) != removeLeadingSlash("${config.getSwaggerUI().swaggerUrl}/{filename}") }
                .filter { !config.getSwaggerUI().forwardRoot || it.path != "/" }
                .filter { !it.documentation.hidden }
                .filter { path ->
                    config.pathFilter
                        ?.let { it(path.method, path.path.split("/").filter { it.isNotEmpty() }) }
                        ?: true
                }
                .onEach { logger.debug("Configure path: ${it.method.value} ${it.path}") }
                .map { pathBuilder.build(it, components, config) }
                .forEach { addToPaths(this, it.first, it.second) }
        }
    }

    private fun removeLeadingSlash(str: String): String {
        return if (str.startsWith("/")) {
            str.substring(1)
        } else {
            str
        }
    }

    private fun addToPaths(paths: Paths, name: String, item: PathItem) {
        paths[name]
            ?.let {
                it.get = if (item.get != null) item.get else it.get
                it.put = if (item.put != null) item.put else it.put
                it.post = if (item.post != null) item.post else it.post
                it.delete = if (item.delete != null) item.delete else it.delete
                it.options = if (item.options != null) item.options else it.options
                it.head = if (item.head != null) item.head else it.head
                it.patch = if (item.patch != null) item.patch else it.patch
                it.trace = if (item.trace != null) item.trace else it.trace
            }
            ?: paths.addPathItem(name, item)
    }


}