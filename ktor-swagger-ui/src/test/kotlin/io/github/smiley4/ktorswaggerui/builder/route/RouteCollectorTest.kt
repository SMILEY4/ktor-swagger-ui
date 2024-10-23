package io.github.smiley4.ktorswaggerui.builder.route

import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.kotest.matchers.shouldBe
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.RouteScopedPlugin
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.plugin
import io.ktor.server.response.respond
import io.ktor.server.routing.HttpMethodRouteSelector
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingNode
import io.ktor.server.routing.RoutingResolveContext
import io.ktor.server.routing.RoutingRoot
import io.ktor.server.routing.get
import io.ktor.server.routing.intercept
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import java.util.UUID

private data class RouteMetas(
    val method: String,
)

private val tracer: RouteScopedPlugin<RouteMetas> = createRouteScopedPlugin(
    name = "pluggy",
    createConfiguration = { RouteMetas("asdf") },
    body = {
        onCall {
            if (MDC.get("Trace") == null) {
                MDC.put("Trace", UUID.randomUUID().toString())
            }
        }
    }
)

class RouteCollectorTest {

    private fun allRoutes(root: RoutingNode): List<RoutingNode> {
        return (listOf(root) + root.children.flatMap { allRoutes(it) })
            .filter { it.selector is HttpMethodRouteSelector }
    }

    private fun Application.allRoutesPlease(): Pair<RouteCollector, List<RoutingNode>> {
        val routeCollector = RouteCollector(RouteDocumentationMerger())
        val rootNode = plugin(RoutingRoot)
        val allRoutes = allRoutes(rootNode)
        return routeCollector to allRoutes
    }

    private fun Application.rutten() {
        routing {
            install(createRouteScopedPlugin("FooPlugg", { "" },{}))
            innerRoute()
        }
    }

    private fun Route.innerRoute() {
        route("/api") {
            auth {
                get("/v1/get_this") { call.respond("Api") }
            }
        }
    }

    private fun Route.auth(build: Route.() -> Unit): Route {
        val guardedRoute = createChild(TransparentRouteSelector())
        guardedRoute.intercept(ApplicationCallPipeline.Plugins) {
            proceed()
        }
        guardedRoute.build()
        return guardedRoute
    }

    private class TransparentRouteSelector : RouteSelector() {
        override suspend fun evaluate(
            context: RoutingResolveContext,
            segmentIndex: Int,
        ): RouteSelectorEvaluation = RouteSelectorEvaluation.Transparent

        override fun toString(): String = ""
    }

    @Test
    fun `should be able to handle transparent route selectors`() {
        testApplication {
            application {
                rutten()
                val (routeCollector, allRoutes) = allRoutesPlease()

                val actual = routeCollector.getPath(allRoutes[0], PluginConfigData.DEFAULT)
                println("actual = $actual")
                actual shouldBe "/api/v1/get_this"
            }
        }
    }
}
