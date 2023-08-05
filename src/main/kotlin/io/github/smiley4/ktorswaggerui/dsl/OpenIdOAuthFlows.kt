package io.github.smiley4.ktorswaggerui.dsl

/**
 * An object containing configuration information for the oauth flow types supported
 */
@OpenApiDslMarker
class OpenIdOAuthFlows {

    private var implicit: OpenIdOAuthFlow? = null


    /**
     * Configuration for the OAuth Implicit flow
     */
    fun implicit(block: OpenIdOAuthFlow.() -> Unit) {
        implicit = OpenIdOAuthFlow().apply(block)
    }


    fun getImplicit() = implicit


    private var password: OpenIdOAuthFlow? = null


    /**
     * Configuration for the OAuth Resource Owner Password flow
     */
    fun password(block: OpenIdOAuthFlow.() -> Unit) {
        password = OpenIdOAuthFlow().apply(block)
    }


    fun getPassword() = password


    private var clientCredentials: OpenIdOAuthFlow? = null


    /**
     * Configuration for the OAuth Client Credentials flow.
     */
    fun clientCredentials(block: OpenIdOAuthFlow.() -> Unit) {
        clientCredentials = OpenIdOAuthFlow().apply(block)
    }


    fun getClientCredentials() = clientCredentials


    private var authorizationCode: OpenIdOAuthFlow? = null


    /**
     * Configuration for the OAuth Authorization Code flow.
     */
    fun authorizationCode(block: OpenIdOAuthFlow.() -> Unit) {
        authorizationCode = OpenIdOAuthFlow().apply(block)
    }


    fun getAuthorizationCode() = authorizationCode

}
