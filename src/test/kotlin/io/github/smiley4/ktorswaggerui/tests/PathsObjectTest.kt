package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.specbuilder.ComponentsContext
import io.github.smiley4.ktorswaggerui.specbuilder.RouteCollector
import io.github.smiley4.ktorswaggerui.specbuilder.RouteMeta
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.mockk.every
import io.mockk.mockk
import io.swagger.v3.oas.models.Paths

class PathsObjectTest : StringSpec({

    "test paths" {
        val config = pluginConfig {}
        val paths = buildPaths(
            config, listOf(
                HttpMethod.Get to "/",
                HttpMethod.Delete to "/test/path",
                HttpMethod.Post to "/other/test/route",
            )
        )
        paths shouldHaveSize 3
        paths.keys shouldContainExactlyInAnyOrder listOf(
            "/",
            "/test/path",
            "/other/test/route"
        )
        paths["/"]!!.get.shouldNotBeNull()
        paths["/test/path"]!!.delete.shouldNotBeNull()
        paths["/other/test/route"]!!.post.shouldNotBeNull()
    }

    "test filter out swagger-ui routes" {
        val config = pluginConfig {
            swagger {
                swaggerUrl = "swagger-ui"
                forwardRoot = false
            }
        }
        val paths = buildPaths(
            config, listOf(
                HttpMethod.Get to "/",
                HttpMethod.Get to "/swagger-ui",
                HttpMethod.Get to "swagger-ui",
                HttpMethod.Get to "/swagger-ui/{filename}",
                HttpMethod.Get to "swagger-ui/{filename}",
                HttpMethod.Delete to "/test/path",
            )
        )
        paths shouldHaveSize 2
        paths.keys shouldContainExactlyInAnyOrder listOf("/", "/test/path")
    }

    "test filter out swagger-ui routes (forward root)" {
        val config = pluginConfig {
            swagger {
                swaggerUrl = "swagger-ui"
                forwardRoot = true
            }
        }
        val paths = buildPaths(
            config, listOf(
                HttpMethod.Get to "/",
                HttpMethod.Get to "/swagger-ui",
                HttpMethod.Get to "swagger-ui",
                HttpMethod.Get to "/swagger-ui/{filename}",
                HttpMethod.Get to "swagger-ui/{filename}",
                HttpMethod.Delete to "/test/path",
            )
        )
        paths shouldHaveSize 1
        paths.keys shouldContainExactlyInAnyOrder listOf("/test/path")
    }

    "test merge paths" {
        val config = pluginConfig {}
        val paths = buildPaths(
            config, listOf(
                HttpMethod.Get to "/different/path",
                HttpMethod.Get to "/test/path",
                HttpMethod.Post to "/test/path",
            )
        )
        paths shouldHaveSize 2
        paths.keys shouldContainExactlyInAnyOrder listOf(
            "/different/path",
            "/test/path",
        )
        paths["/different/path"]!!.get.shouldNotBeNull()
        paths["/test/path"]!!.get.shouldNotBeNull()
        paths["/test/path"]!!.post.shouldNotBeNull()
    }

    "test filter paths" {
        val config = pluginConfig {
            pathFilter = {_, url -> url.firstOrNull() == "test"}
        }
        val paths = buildPaths(
            config, listOf(
                HttpMethod.Get to "/different/path",
                HttpMethod.Get to "/test/path",
                HttpMethod.Post to "/test/path/2",
            )
        )
        paths shouldHaveSize 2
        paths.keys shouldContainExactlyInAnyOrder listOf(
            "/test/path",
            "/test/path/2",
        )
    }

}) {

    companion object {

        private fun buildPaths(config: SwaggerUIPluginConfig, routes: List<Pair<HttpMethod, String>>): Paths {
            return getOApiPathsBuilder(routeCollector(routes)).build(config, application(), ComponentsContext.NOOP)
        }

        private fun pluginConfig(builder: SwaggerUIPluginConfig.() -> Unit): SwaggerUIPluginConfig {
            return SwaggerUIPluginConfig().apply(builder)
        }

        private fun application() = mockk<Application>()

        private fun routeCollector(routes: List<Pair<HttpMethod, String>>) = mockk<RouteCollector>().also {
            every { it.collectRoutes(any(), any()) } returns routes
                .map {
                    RouteMeta(
                        method = it.first,
                        path = it.second,
                        documentation = OpenApiRoute(),
                        protected = false,
                    )
                }
                .asSequence()
        }

    }

}