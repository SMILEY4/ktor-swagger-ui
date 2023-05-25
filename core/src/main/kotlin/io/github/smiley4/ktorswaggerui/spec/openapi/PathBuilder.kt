package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.spec.route.RouteMeta
import io.ktor.http.HttpMethod
import io.swagger.v3.oas.models.PathItem

class PathBuilder(
    private val operationBuilder: OperationBuilder
) {

    fun build(route: RouteMeta): PathItem =
        PathItem().also {
            val operation = operationBuilder.build(route)
            when (route.method) {
                HttpMethod.Get -> it.get = operation
                HttpMethod.Post -> it.post = operation
                HttpMethod.Put -> it.put = operation
                HttpMethod.Patch -> it.patch = operation
                HttpMethod.Delete -> it.delete = operation
                HttpMethod.Head -> it.head = operation
                HttpMethod.Options -> it.options = operation
            }
        }

}