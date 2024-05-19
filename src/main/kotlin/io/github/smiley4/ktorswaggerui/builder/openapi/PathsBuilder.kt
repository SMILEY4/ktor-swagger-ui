package io.github.smiley4.ktorswaggerui.builder.openapi

import io.github.smiley4.ktorswaggerui.builder.route.RouteMeta
import io.github.smiley4.ktorswaggerui.routing.ControllerUtils
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.Paths

class PathsBuilder(
    private val pathBuilder: PathBuilder
) {

    fun build(routes: Collection<RouteMeta>): Paths =
        Paths().also {
            routes.forEach { route ->
                val existingPath = it[route.path]
                if (existingPath != null) {
                    addToExistingPath(existingPath, route)
                } else {
                    addAsNewPath(it, route)
                }
            }
        }

    private fun addAsNewPath(paths: Paths, route: RouteMeta) {
        val rootPath = ControllerUtils.appConfig?.let { ControllerUtils.getRootPath(it) } ?: ""
        paths.addPathItem("$rootPath${route.path}", pathBuilder.build(route))
    }

    private fun addToExistingPath(existing: PathItem, route: RouteMeta) {
        val path = pathBuilder.build(route)
        existing.get = path.get ?: existing.get
        existing.put = path.put ?: existing.put
        existing.post = path.post ?: existing.post
        existing.delete = path.delete ?: existing.delete
        existing.options = path.options ?: existing.options
        existing.head = path.head ?: existing.head
        existing.patch = path.patch ?: existing.patch
        existing.trace = path.trace ?: existing.trace
    }

}
