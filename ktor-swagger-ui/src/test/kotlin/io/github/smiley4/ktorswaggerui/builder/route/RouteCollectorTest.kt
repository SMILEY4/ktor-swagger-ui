package io.github.smiley4.ktorswaggerui.builder.route

import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.ktor.server.application.Application
import io.ktor.server.application.RouteScopedPlugin
import io.ktor.server.application.call
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.install
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import java.util.UUID

data class RouteMeta(
    val method: String,
)

private val tracer: RouteScopedPlugin<RouteMeta> = createRouteScopedPlugin(
    name = "pluggy",
    createConfiguration = { RouteMeta("asdf") },
    body = {
        onCall {
            if (MDC.get("Trace") == null) {
                MDC.put("Trace", UUID.randomUUID().toString())
            }
        }
    }
)

class RouteCollectorTest {

    private val tests: List<Pair<List<String>, String>> = listOf(
        listOf("/") to "",
        listOf("/api") to "/api",
        listOf("/nested", "/routing") to "/nested/routing",
        listOf("/trailing/", "/slashes") to "/trailing/slashes",
    )

    private fun Application.testRoutes(): List<Route> {
        val memo = mutableListOf<Route>()
        routing {
            memo.add(get { call.respond("root") })
            route("/api") {
                memo.add(get { call.respond("Api") })
            }
            route("/nested") {
                route("/routing") {
                    install(tracer)
                    memo.add(get("/") { call.respond("Nested Routing") })
                }
            }
            route("/trailing/") {
                memo.add(get("/slashes") { call.respond("Trailing Slashes") })
            }
        }
        return memo
    }

    @Test
    fun `should be able to get nested route`() {
        // Given
        testApplication {
            application {
                val routes = testRoutes()

                val routeCollector = RouteCollector(RouteDocumentationMerger())

                for (i in tests.indices) {
                    // When
                    val nestedRoute = routeCollector.getPath(routes[i], PluginConfigData.DEFAULT)

                    // Then
                    assertThat(nestedRoute, equalTo(tests[i].second))
                }
            }
        }

    }
}
