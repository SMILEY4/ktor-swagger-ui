package io.github.smiley4.ktorswaggerui

import io.github.smiley4.ktorswaggerui.dsl.CustomSchemas
import io.github.smiley4.ktorswaggerui.dsl.EncodingConfig
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker
import io.github.smiley4.ktorswaggerui.dsl.OpenApiInfo
import io.github.smiley4.ktorswaggerui.dsl.OpenApiResponse
import io.github.smiley4.ktorswaggerui.dsl.OpenApiSecurityScheme
import io.github.smiley4.ktorswaggerui.dsl.OpenApiServer
import io.github.smiley4.ktorswaggerui.dsl.OpenApiTag
import io.github.smiley4.ktorswaggerui.dsl.SwaggerUIDsl
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.RouteSelector
import kotlin.reflect.KClass

/**
 * Main-Configuration of the "SwaggerUI"-Plugin
 */
@OpenApiDslMarker
class SwaggerUIPluginConfig {

    /**
     * Default response to automatically add to each protected route for the "Unauthorized"-Response-Code.
     * Generated response can be overwritten with custom response.
     */
    fun defaultUnauthorizedResponse(block: OpenApiResponse.() -> Unit) {
        defaultUnauthorizedResponse = OpenApiResponse(HttpStatusCode.Unauthorized.value.toString()).apply(block)
    }

    private var defaultUnauthorizedResponse: OpenApiResponse? = null

    fun getDefaultUnauthorizedResponse() = defaultUnauthorizedResponse


    /**
     * The name of the security scheme to use for the protected paths
     */
    var defaultSecuritySchemeName: String? = null


    /**
     * The names of the security schemes available for use for the protected paths
     */
    var defaultSecuritySchemeNames: Collection<String>? = null


    /**
     * Automatically add tags to the route with the given url.
     * The returned (non-null) tags will be added to the tags specified in the route-specific documentation.
     */
    fun generateTags(generator: TagGenerator) {
        tagGenerator = generator
    }

    private var tagGenerator: TagGenerator = { emptyList() }

    fun getTagGenerator() = tagGenerator


    /**
     * Filter to apply to all routes. Return 'false' for routes to not include them in the OpenApi-Spec and Swagger-UI.
     * The url of the paths are already split at '/'.
     */
    var pathFilter: ((method: HttpMethod, url: List<String>) -> Boolean)? = null


    /**
     * Swagger-UI configuration
     */
    fun swagger(block: SwaggerUIDsl.() -> Unit) {
        swaggerUI = SwaggerUIDsl().apply(block)
    }

    private var swaggerUI = SwaggerUIDsl()

    fun getSwaggerUI() = swaggerUI


    /**
     * OpenAPI info configuration - provides metadata about the API
     */
    fun info(block: OpenApiInfo.() -> Unit) {
        info = OpenApiInfo().apply(block)
    }

    private var info = OpenApiInfo()

    fun getInfo() = info


    /**
     * OpenAPI server configuration - an array of servers, which provide connectivity information to a target server
     */
    fun server(block: OpenApiServer.() -> Unit) {
        servers.add(OpenApiServer().apply(block))
    }

    private val servers = mutableListOf<OpenApiServer>()

    fun getServers(): List<OpenApiServer> = servers


    /**
     * Defines security schemes that can be used by operations
     */
    fun securityScheme(name: String, block: OpenApiSecurityScheme.() -> Unit) {
        securitySchemes.add(OpenApiSecurityScheme(name).apply(block))
    }

    private val securitySchemes = mutableListOf<OpenApiSecurityScheme>()

    fun getSecuritySchemes(): List<OpenApiSecurityScheme> = securitySchemes


    /**
     * Tags used by the specification with additional metadata. Not all tags that are used must be declared
     */
    fun tag(name: String, block: OpenApiTag.() -> Unit) {
        tags.add(OpenApiTag(name).apply(block))
    }

    private val tags = mutableListOf<OpenApiTag>()

    fun getTags(): List<OpenApiTag> = tags


    /**
     * Custom schemas to reference via [io.github.smiley4.ktorswaggerui.dsl.CustomSchemaRef]
     */
    fun customSchemas(block: CustomSchemas.() -> Unit) {
        this.customSchemas = CustomSchemas().apply(block)
    }

    private var customSchemas = CustomSchemas()

    fun getCustomSchemas() = customSchemas


    /**
     * customize the behaviour of different encoders (examples, schemas, ...)
     */
    fun encoding(block: EncodingConfig.() -> Unit) {
        block(encodingConfig)
    }

    val encodingConfig: EncodingConfig = EncodingConfig()


    /**
     * List of all [RouteSelector] types in that should be ignored in the resulting url of any route.
     */
    var ignoredRouteSelectors: List<KClass<*>> = listOf()

}

/**
 * url - the parts of the route-url split at all `/`.
 * return a collection of tags. "Null"-entries will be ignored.
 */
typealias TagGenerator = (url: List<String>) -> Collection<String?>
