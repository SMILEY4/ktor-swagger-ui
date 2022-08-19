package io.github.smiley4.ktorswaggerui

class SwaggerUIPluginConfig {

    /**
     * Whether to automatically add responses for the "Unauthorized"-Response-Code for protected routes.
     * Generated response can be overwritten with custom responses.
     */
    var automaticUnauthorizedResponses: Boolean = true


    /**
     * The name of the security scheme to use for the protected paths
     */
    var defaultSecurityScheme: String? = null

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

    fun securityScheme(block: OpenApiSecuritySchemeConfig.() -> Unit) {
        securitySchemes.add(OpenApiSecuritySchemeConfig().apply(block))
    }

    fun getSecuritySchemes(): List<OpenApiSecuritySchemeConfig> = securitySchemes


    /**
     * A list of tags used by the specification with additional metadata. The order of the tags can be used to reflect on their order by the
     * parsing tools. Not all tags that are used must be declared
     */
    private val tags = mutableListOf<OpenApiTagConfig>()

    fun tag(block: OpenApiTagConfig.() -> Unit) {
        tags.add(OpenApiTagConfig().apply(block))
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
        contact =  OpenApiContactConfig().apply(block)
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


/**
 * Defines a security scheme that can be used by the operations. Supported schemes are HTTP authentication, an API key (either as a header,
 * a cookie parameter or as a query parameter), OAuth2's common flows (implicit, password, client credentials and authorization code)
 */
class OpenApiSecuritySchemeConfig {

    enum class Type {
        API_KEY, HTTP, OAUTH2, OPENID_CONNECT, MUTUAL_TLS
    }

    enum class KeyLocation {
        QUERY, HEADER, COOKIE
    }

    enum class Scheme {
        //  https://www.iana.org/assignments/http-authschemes/http-authschemes.xhtml
        BASIC, BEARER, DIGEST, HOBA, MUTUAL, OAUTH, SCRAM_SHA_1, SCRAM_SHA_256, VAPID
    }


    /**
     * The type of the security scheme
     */
    var type: Type? = null


    /**
     * The name of the header, query or cookie parameter to be used
     */
    var name: String = ""


    /**
     * The location of the API key (OpenAPI 'in')
     */
    var location: KeyLocation? = null


    /**
     * The name of the HTTP Authorization scheme to be used
     */
    var scheme: Scheme? = null


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
class OpenApiTagConfig {
    /**
     * The name of the tag.
     */
    var name: String? = null


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
