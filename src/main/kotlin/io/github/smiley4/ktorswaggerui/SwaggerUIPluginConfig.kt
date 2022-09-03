package io.github.smiley4.ktorswaggerui

import io.github.smiley4.ktorswaggerui.documentation.SingleResponseDocumentation
import io.ktor.http.HttpStatusCode

class SwaggerUIPluginConfig {

    /**
     * Default response to automatically add to each protected route for the "Unauthorized"-Response-Code.
     * Generated response can be overwritten with custom response.
     */
    private var defaultUnauthorizedResponse: SingleResponseDocumentation? = null

    fun defaultUnauthorizedResponse(block: SingleResponseDocumentation.() -> Unit) {
        defaultUnauthorizedResponse =  SingleResponseDocumentation(HttpStatusCode.Unauthorized).apply(block)
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
     * Swagger-UI configuration
     */
    private var swaggerUIConfig = SwaggerUIConfig()

    fun swagger(block: SwaggerUIConfig.() -> Unit) {
        swaggerUIConfig = SwaggerUIConfig().apply(block)
    }

    fun getSwaggerUI() = swaggerUIConfig


    /**
     * OpenAPI info configuration - provides metadata about the API
     */
    private var info = OpenApiInfoConfig()

    fun info(block: OpenApiInfoConfig.() -> Unit) {
        info = OpenApiInfoConfig().apply(block)
    }

    fun getInfo() = info


    /**
     * OpenAPI server configuration - an array of servers, which provide connectivity information to a target server
     */
    private val servers = mutableListOf<OpenApiServerConfig>()

    fun server(block: OpenApiServerConfig.() -> Unit) {
        servers.add(OpenApiServerConfig().apply(block))
    }

    fun getServers(): List<OpenApiServerConfig> = servers


    /**
     * Defines security schemes that can be used by operations
     */
    private val securitySchemes = mutableListOf<OpenApiSecuritySchemeConfig>()

    fun securityScheme(name: String, block: OpenApiSecuritySchemeConfig.() -> Unit) {
        securitySchemes.add(OpenApiSecuritySchemeConfig(name).apply(block))
    }

    fun getSecuritySchemes(): List<OpenApiSecuritySchemeConfig> = securitySchemes


    /**
     * A list of tags used by the specification with additional metadata. The order of the tags can be used to reflect on their order by the
     * parsing tools. Not all tags that are used must be declared
     */
    private val tags = mutableListOf<OpenApiTagConfig>()

    fun tag(name: String, block: OpenApiTagConfig.() -> Unit) {
        tags.add(OpenApiTagConfig(name).apply(block))
    }

    fun getTags(): List<OpenApiTagConfig> = tags

}


class SwaggerUIConfig {
    /**
     * Whether to forward the root-url to the swagger-url
     */
    var forwardRoot: Boolean = false


    /**
     * the url to the swagger-ui
     */
    var swaggerUrl: String = "swagger-ui"
}


class OpenApiInfoConfig {
    /**
     * The title of the api
     */
    var title: String = "API"


    /**
     * The version of the OpenAPI document
     */
    var version: String? = "latest"


    /**
     * A short description of the API
     */
    var description: String? = null


    /**
     * A URL to the Terms of Service for the API. MUST be in the format of a URL.
     */
    var termsOfService: String? = null


    /**
     * The contact information for the exposed API.
     */
    private var contact: OpenApiContactConfig? = null

    fun contact(block: OpenApiContactConfig.() -> Unit) {
        contact = OpenApiContactConfig().apply(block)
    }

    fun getContact() = contact


    /**
     * The license information for the exposed API.
     */
    private var license: OpenApiLicenseConfig? = null

    fun license(block: OpenApiLicenseConfig.() -> Unit) {
        license = OpenApiLicenseConfig().apply(block)
    }

    fun getLicense() = license

}


/**
 * Contact information for the exposed API.
 */
class OpenApiContactConfig {
    /**
     * The identifying name of the contact person/organization.
     */
    var name: String? = null


    /**
     * The URL pointing to the contact information. MUST be in the format of a URL.
     */
    var url: String? = null


    /**
     * The email address of the contact person/organization. MUST be in the format of an email address.
     */
    var email: String? = null
}


/**
 * License information for the exposed API.
 */
class OpenApiLicenseConfig {
    /**
     * The license name used for the API
     */
    var name: String = "?"


    /**
     * A URL to the license used for the API. MUST be in the format of a URL.
     */
    var url: String? = null

}


/**
 * An object representing a Server.
 */
class OpenApiServerConfig {

    /**
     * A URL to the target host. This URL supports Server Variables and MAY be relative, to indicate that the host location is relative to
     * the location where the OpenAPI document is being served
     */
    var url: String = "/"


    /**
     * An optional string describing the host designated by the URL
     */
    var description: String? = null
}


/**
 * Allows referencing an external resource for extended documentation.
 */
class OpenApiExternalDocumentationConfig {

    /**
     * The URL for the target documentation. Value MUST be in the format of a URL.
     */
    var url: String = "/"


    /**
     * A short description of the target documentation
     */
    var description: String? = null

}


enum class AuthType {
    API_KEY, HTTP, OAUTH2, OPENID_CONNECT, MUTUAL_TLS
}

enum class KeyLocation {
    QUERY, HEADER, COOKIE
}

enum class AuthScheme {
    //  https://www.iana.org/assignments/http-authschemes/http-authschemes.xhtml
    BASIC, BEARER, DIGEST, HOBA, MUTUAL, OAUTH, SCRAM_SHA_1, SCRAM_SHA_256, VAPID
}


/**
 * Defines a security scheme that can be used by the operations. Supported schemes are HTTP authentication, an API key (either as a header,
 * a cookie parameter or as a query parameter), OAuth2's common flows (implicit, password, client credentials and authorization code)
 */
class OpenApiSecuritySchemeConfig(
    /**
     * The name of the header, query or cookie parameter to be used
     */
    val name: String
) {

    /**
     * The type of the security scheme
     */
    var type: AuthType? = null


    /**
     * The location of the API key (OpenAPI 'in')
     */
    var location: KeyLocation? = null


    /**
     * The name of the HTTP Authorization scheme to be used
     */
    var scheme: AuthScheme? = null


    /**
     * A hint to the client to identify how the bearer token is formatted
     */
    var bearerFormat: String? = null


    /**
     * A short description for security scheme.
     */
    var description: String? = null
}


/**
 * Adds metadata to a single tag.
 */
class OpenApiTagConfig(
    /**
     * The name of the tag.
     */
    var name: String
) {

    /**
     * A short description for the tag.
     */
    var description: String? = null


    /**
     * A short description of additional external documentation for this tag.
     */
    var externalDocDescription: String? = null


    /**
     *The URL for additional external documentation for this tag.
     */
    var externalDocUrl: String? = null

}
