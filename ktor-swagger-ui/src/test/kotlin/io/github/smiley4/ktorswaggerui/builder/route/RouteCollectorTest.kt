package io.github.smiley4.ktorswaggerui.builder.route

import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.github.smiley4.ktorswaggerui.dsl.routes.OpenApiRoute
import io.github.smiley4.ktorswaggerui.dsl.routing.DocumentedRouteSelector
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class RouteCollectorTest {

    private val tests: List<Pair<List<String>, String>> = listOf(
        listOf("/") to "/",
        listOf("/api") to "/api",
        listOf("/nested/", "/asdg") to "/nested/asdg",
    )

    @Test
    fun `should be able to get nested route`() {
        // Given
        for ((path, expected) in tests) {
            val rootR = Route(null, DocumentedRouteSelector(OpenApiRoute()), true)
            var parent = rootR
            for (p in path) {
                parent = parent.route(p) {}
            }
            parent = parent.get("/") {
                call.respond("Hello")
            }
            val routeCollector = RouteCollector(RouteDocumentationMerger())

            // When
            val nestedRoute = routeCollector.getPath(parent, PluginConfigData.DEFAULT)

            // Then
            assertThat(nestedRoute, equalTo(expected))
        }
    }
}