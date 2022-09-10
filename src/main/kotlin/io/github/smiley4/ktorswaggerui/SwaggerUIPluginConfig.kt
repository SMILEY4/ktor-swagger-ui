package io.github.smiley4.ktorswaggerui

import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker
import io.github.smiley4.ktorswaggerui.dsl.OpenApiInfo
import io.github.smiley4.ktorswaggerui.dsl.OpenApiResponse
import io.github.smiley4.ktorswaggerui.dsl.OpenApiSecurityScheme
import io.github.smiley4.ktorswaggerui.dsl.OpenApiServer
import io.github.smiley4.ktorswaggerui.dsl.OpenApiTag
import io.github.smiley4.ktorswaggerui.dsl.SwaggerUI
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode


/**
 * Main-Configuration of the "SwaggerUI"-Plugin
 */
@OpenApiDslMarker
class SwaggerUIPluginConfig {

    private var defaultUnauthorizedResponse: OpenApiResponse? = null


    /**
     * Default response to automatically add to each protected route for the "Unauthorized"-Response-Code.
     * Generated response can be overwritten with custom response.
     */
    fun defaultUnauthorizedResponse(block: OpenApiResponse.() -> Unit) {
        defaultUnauthorizedResponse = OpenApiResponse(HttpStatusCode.Unauthorized.value.toString()).apply(block)
    }


    fun getDefaultUnauthorizedResponse() = defaultUnauthorizedResponse


    /**
     * The name of the security scheme to use for the protected paths
     */
    var defaultSecuritySchemeName: String? = null


    /**
     * function to generate a tag from the given url for a path. Result will be added to the tags defined for each path
     */
    var automaticTagGenerator: ((url: List<String>) -> String?)? = null


    /**
     * Whether to put json-schemas in the component section and reference them or inline the schemas at the actual place of usage.
     * (https://swagger.io/specification/#components-object)
     */
    var schemasInComponentSection: Boolean = false


    /**
     * Whether to put example objects in the component section and reference them or inline the examples at the actual place of usage.
     */
    var examplesInComponentSection: Boolean = false


    /**
     * Filter to apply to all routes. Return 'false' for routes to not include them in the OpenApi-Spec and Swagger-UI.
     * The url of the paths are already split at '/'.
     */
    var pathFilter: ((method: HttpMethod, url: List<String>) -> Boolean)? = null

    private var swaggerUI = SwaggerUI()


    /**
     * Swagger-UI configuration
     */
    fun swagger(block: SwaggerUI.() -> Unit) {
        swaggerUI = SwaggerUI().apply(block)
    }


    fun getSwaggerUI() = swaggerUI


    private var info = OpenApiInfo()


    /**
     * OpenAPI info configuration - provides metadata about the API
     */
    fun info(block: OpenApiInfo.() -> Unit) {
        info = OpenApiInfo().apply(block)
    }


    fun getInfo() = info


    private val servers = mutableListOf<OpenApiServer>()


    /**
     * OpenAPI server configuration - an array of servers, which provide connectivity information to a target server
     */
    fun server(block: OpenApiServer.() -> Unit) {
        servers.add(OpenApiServer().apply(block))
    }


    fun getServers(): List<OpenApiServer> = servers


    private val securitySchemes = mutableListOf<OpenApiSecurityScheme>()


    /**
     * Defines security schemes that can be used by operations
     */
    fun securityScheme(name: String, block: OpenApiSecurityScheme.() -> Unit) {
        securitySchemes.add(OpenApiSecurityScheme(name).apply(block))
    }


    fun getSecuritySchemes(): List<OpenApiSecurityScheme> = securitySchemes


    private val tags = mutableListOf<OpenApiTag>()


    /**
     * Tags used by the specification with additional metadata. Not all tags that are used must be declared
     */
    fun tag(name: String, block: OpenApiTag.() -> Unit) {
        tags.add(OpenApiTag(name).apply(block))
    }


    fun getTags(): List<OpenApiTag> = tags

}
