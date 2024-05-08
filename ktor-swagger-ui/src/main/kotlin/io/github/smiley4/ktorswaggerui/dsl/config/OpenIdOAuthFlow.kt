package io.github.smiley4.ktorswaggerui.dsl.config

import io.github.smiley4.ktorswaggerui.data.DataUtils.merge
import io.github.smiley4.ktorswaggerui.data.OpenIdOAuthFlowData
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker

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


    fun build(base: OpenIdOAuthFlowData) = OpenIdOAuthFlowData(
        authorizationUrl = merge(base.authorizationUrl, authorizationUrl),
        tokenUrl = merge(base.tokenUrl, tokenUrl),
        refreshUrl = merge(base.refreshUrl, refreshUrl),
        scopes = merge(base.scopes, scopes),
    )

}
