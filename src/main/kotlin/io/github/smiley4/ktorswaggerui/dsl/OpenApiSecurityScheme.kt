package io.github.smiley4.ktorswaggerui.dsl


enum class AuthType {
    API_KEY, HTTP, OAUTH2, OPENID_CONNECT, MUTUAL_TLS
}

enum class AuthKeyLocation {
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
@OpenApiDslMarker
class OpenApiSecurityScheme(
    /**
     * The name of the header, query or cookie parameter to be used.
     * Required for type [AuthType.API_KEY]
     */
    val name: String
) {

    /**
     * The type of the security scheme
     */
    var type: AuthType? = null


    /**
     * The location of the API key (OpenAPI 'in').
     * Required for type [AuthType.API_KEY]
     */
    var location: AuthKeyLocation? = null


    /**
     * The name of the HTTP Authorization scheme to be used.
     * Required for type [AuthType.HTTP]
     */
    var scheme: AuthScheme? = null


    /**
     * A hint to the client to identify how the bearer token is formatted.
     * Used for type [AuthType.HTTP] and schema [AuthScheme.BEARER]
     */
    var bearerFormat: String? = null

    private var flows: OpenIdOAuthFlows? = null


    /**
     * information for the oauth flow types supported.
     * Required for type [AuthType.OAUTH2]
     */
    fun flows(block: OpenIdOAuthFlows.() -> Unit) {
        flows = OpenIdOAuthFlows().apply(block)
    }


    fun getFlows() = flows


    /**
     * OpenId Connect URL to discover OAuth2 configuration values.
     * Required for type [AuthType.OPENID_CONNECT]
     */
    var openIdConnectUrl: String? = null


    /**
     * A short description for security scheme.
     */
    var description: String? = null

}