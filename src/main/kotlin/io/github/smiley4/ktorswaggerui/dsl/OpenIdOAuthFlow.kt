package io.github.smiley4.ktorswaggerui.dsl
/**
 * Configuration details for a supported OAuth Flow
 */
@OpenApiDslMarker
class OpenIdOAuthFlow {

    /**
     * The authorization URL to be used for this flow
     */
    var authorizationUrl: String? = null


    /**
     * The token URL to be used for this flow
     */
    var tokenUrl: String? = null


    /**
     * The URL to be used for obtaining refresh tokens
     */
    var refreshUrl: String? = null


    /**
     * The available scopes for the OAuth2 security scheme. A map between the scope name and a short description for it
     */
    var scopes: Map<String, String>? = null

}
