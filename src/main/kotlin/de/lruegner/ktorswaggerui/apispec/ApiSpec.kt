package de.lruegner.ktorswaggerui

import de.lruegner.ktorswaggerui.ParameterDocumentation.Companion.ParameterDataType
import de.lruegner.ktorswaggerui.ParameterDocumentation.Companion.ParamType.PATH
import de.lruegner.ktorswaggerui.ParameterDocumentation.Companion.ParamType.QUERY
import de.lruegner.ktorswaggerui.documentation.DocumentedRouteSelector
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.plugin
import io.ktor.server.auth.AuthenticationRouteSelector
import io.ktor.server.routing.HttpMethodRouteSelector
import io.ktor.server.routing.RootRouteSelector
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.TrailingSlashRouteSelector
import io.swagger.v3.core.util.Json
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.Paths
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import java.lang.reflect.Type


class RouteDocumentation(
    var summary: String? = "",
    var description: String? = null,
    var responses: MutableList<ResponseDocumentation> = mutableListOf(),
    var parameters: MutableList<ParameterDocumentation> = mutableListOf(),
    var body: BodyDocumentation? = null
) {

    fun response(code: HttpStatusCode, description: String, body: Type? = null) {
        responses.find { it.statusCode == code }
            ?.descriptions?.add(description)
            ?: responses.add(ResponseDocumentation(code, mutableListOf(description), body))
    }

    fun pathParam(name: String, type: ParameterDataType, description: String) {
        parameters.add(ParameterDocumentation(PATH, name, description, type))
    }

    fun pathParam(name: String, builder: ParameterDocumentation.() -> Unit) {
        parameters.add(ParameterDocumentation(PATH, name).apply(builder))
    }

    fun queryParam(name: String, builder: ParameterDocumentation.() -> Unit) {
        parameters.add(ParameterDocumentation(QUERY, name).apply(builder))
    }

    fun requestBody(schema: Type, description: String? = null) {
        body = JsonBodyDocumentation(description, schema)
    }

    fun requestBodyPlainText(description: String? = null) {
        body = PlainTextBodyDocumentation(description)
    }

}

class ResponseDocumentation(
    val statusCode: HttpStatusCode,
    val descriptions: MutableList<String>,
    val body: Type? = null
)

class ParameterDocumentation(
    val type: ParamType,
    val name: String,
    var description: String? = null,
    var dataType: ParameterDataType = ParameterDataType.STRING
) {
    companion object {
        enum class ParamType {
            PATH, QUERY
        }

        enum class ParameterDataType {
            STRING, INT, NUMBER, BOOL, OBJECT,
            STRING_ARRAY, INT_ARRAY, NUMBER_ARRAY, BOOL_ARRAY, OBJECT_ARRAY
        }
    }
}

sealed class BodyDocumentation(val description: String? = null)

class JsonBodyDocumentation(
    description: String? = null,
    val schema: Type,
) : BodyDocumentation(description)

class PlainTextBodyDocumentation(description: String? = null) : BodyDocumentation(description)


data class RouteMeta(
    val path: String,
    val method: HttpMethod,
    val documentation: RouteDocumentation,
    val protected: Boolean
)

object ApiSpec {

    var jsonSpec: String = ""

    fun build(
        application: Application,
        config: SwaggerPluginConfiguration
    ) {
        val openAPI = OpenAPI().apply {
            info = Info().apply {
                title = config.info.title
                description = config.info.description
                version = config.info.version
            }
            servers = config.servers.map {
                Server().apply {
                    url = it.url
                    description = it.description
                }
            }
            components = Components().apply {
                securitySchemes = mapOf(
                    "BearerAuth" to SecurityScheme().apply {
                        type = SecurityScheme.Type.HTTP
                        scheme = "bearer"
                        bearerFormat = "JWT"
                    }
                )
            }
            paths = Paths().apply {
                collectRoutes(application)
                    .filter { it.path != config.swaggerUrl }
                    .filter { it.path != "${config.swaggerUrl}/{filename}" }
                    .filter { it.path != "${config.swaggerUrl}/schemas/{schemaname}" }
                    .filter { !config.forwardRoot || it.path != "/" }
                    .forEach { route ->
                        addPathItem(route.path, PathItem().apply {
                            val operation = Operation().apply {
                                summary = route.documentation.summary
                                description = route.documentation.description
                                if (route.protected) {
                                    security = mutableListOf(
                                        SecurityRequirement().apply {
                                            addList("BearerAuth", listOf())
                                        }
                                    )
                                }
                                responses = ApiResponses().apply {
                                    if (route.protected && route.documentation.responses.count { it.statusCode == HttpStatusCode.Unauthorized } == 0) {
                                        route.documentation.responses.add(
                                            ResponseDocumentation(
                                                HttpStatusCode.Unauthorized,
                                                mutableListOf("Authentication failed")
                                            )
                                        )
                                    }
                                    route.documentation.responses.forEach { response ->
                                        addApiResponse(response.statusCode.value.toString(), ApiResponse().apply {
                                            description = response.descriptions.joinToString(" OR ")
                                            response.body?.let { responseBody ->
                                                content = Content().apply {
                                                    addMediaType("application/json", MediaType().apply {
                                                        schema = Schema<String>().apply {
                                                            `$ref` = "schemas/" + responseBody.typeName.replace(".", "__")
                                                        }
                                                    })
                                                }
                                            }
                                        })
                                    }
                                }
                                parameters = route.documentation.parameters.map { param ->
                                    Parameter().apply {
                                        name = param.name
                                        description = param.description
                                        `in` = when (param.type) {
                                            PATH -> "path"
                                            QUERY -> "query"
                                        }
                                        schema = Schema<String>().apply {
                                            when (param.dataType) {
                                                ParameterDataType.STRING -> {
                                                    type = "string"
                                                }
                                                ParameterDataType.INT -> {
                                                    type = "integer"
                                                }
                                                ParameterDataType.NUMBER -> {
                                                    type = "number"
                                                }
                                                ParameterDataType.BOOL -> {
                                                    type = "boolean"
                                                }
                                                ParameterDataType.OBJECT -> {
                                                    type = "object"
                                                }
                                                ParameterDataType.STRING_ARRAY -> {
                                                    type = "array"
                                                    items = Schema<String>().apply {
                                                        type = "string"
                                                    }
                                                }
                                                ParameterDataType.INT_ARRAY -> {
                                                    type = "integer"
                                                    items = Schema<String>().apply {
                                                        type = "string"
                                                    }
                                                }
                                                ParameterDataType.NUMBER_ARRAY -> {
                                                    type = "number"
                                                    items = Schema<String>().apply {
                                                        type = "string"
                                                    }
                                                }
                                                ParameterDataType.BOOL_ARRAY -> {
                                                    type = "boolean"
                                                    items = Schema<String>().apply {
                                                        type = "string"
                                                    }
                                                }
                                                ParameterDataType.OBJECT_ARRAY -> {
                                                    type = "object"
                                                    items = Schema<String>().apply {
                                                        type = "string"
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                route.documentation.body?.let { body ->
                                    requestBody = RequestBody().apply {
                                        description = body.description
                                        content = Content().apply {
                                            when (body) {
                                                is JsonBodyDocumentation -> {
                                                    addMediaType("application/json", MediaType().apply {
                                                        schema = Schema<String>().apply {
                                                            `$ref` = "schemas/" + body.schema.typeName.replace(".", "__")
                                                        }
                                                    })
                                                }
                                                is PlainTextBodyDocumentation -> {
                                                    addMediaType("text/plain", MediaType().apply {
                                                        schema = Schema<String>().apply {
                                                            type = "string"
                                                        }
                                                    })
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            when (route.method) {
                                HttpMethod.Get -> get = operation
                                HttpMethod.Post -> post = operation
                                HttpMethod.Put -> put = operation
                                HttpMethod.Patch -> patch = operation
                                HttpMethod.Delete -> delete = operation
                                HttpMethod.Head -> head = operation
                                HttpMethod.Options -> options = operation
                            }
                        })
                    }
            }
        }
        jsonSpec = Json.pretty(openAPI)
    }

    private fun collectRoutes(application: Application): List<RouteMeta> {
        val routes = allRoutes(application.plugin(Routing))
        return routes.map { route ->
            RouteMeta(
                method = getMethod(route),
                path = getPath(route),
                documentation = getDocumentation(route),
                protected = isProtected(route)
            )
        }
    }

    private fun getDocumentation(route: Route): RouteDocumentation {
        val selector = route.selector
        return when (selector) {
            is DocumentedRouteSelector -> selector.documentation
            else -> route.parent?.let { getDocumentation(it) } ?: RouteDocumentation()
        }
    }

    private fun getMethod(route: Route): HttpMethod {
        return (route.selector as HttpMethodRouteSelector).method
    }

    private fun getPath(route: Route): String {
        return when (route.selector) {
            is TrailingSlashRouteSelector -> "/"
            is RootRouteSelector -> ""
            is DocumentedRouteSelector -> route.parent?.let { getPath(it) } ?: ""
            is HttpMethodRouteSelector -> route.parent?.let { getPath(it) } ?: ""
            is AuthenticationRouteSelector -> route.parent?.let { getPath(it) } ?: ""
            else -> (route.parent?.let { getPath(it) } ?: "") + "/" + route.selector.toString()
        }
    }

    private fun isProtected(route: Route): Boolean {
        return when (route.selector) {
            is TrailingSlashRouteSelector -> false
            is RootRouteSelector -> false
            is DocumentedRouteSelector -> route.parent?.let { isProtected(it) } ?: false
            is HttpMethodRouteSelector -> route.parent?.let { isProtected(it) } ?: false
            is AuthenticationRouteSelector -> true
            else -> route.parent?.let { isProtected(it) } ?: false
        }
    }

    private fun allRoutes(root: Route): List<Route> {
        return (listOf(root) + root.children.flatMap { allRoutes(it) })
            .filter { it.selector is HttpMethodRouteSelector }
    }

}
