package io.github.smiley4.ktorswaggerui.dsl

import io.github.smiley4.ktorswaggerui.data.OpenIdOAuthFlowData
import io.github.smiley4.ktorswaggerui.data.OpenIdOAuthFlowsData

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


    private var password: OpenIdOAuthFlow? = null


    /**
     * Configuration for the OAuth Resource Owner Password flow
     */
    fun password(block: OpenIdOAuthFlow.() -> Unit) {
        password = OpenIdOAuthFlow().apply(block)
    }


    private var clientCredentials: OpenIdOAuthFlow? = null


    /**
     * Configuration for the OAuth Client Credentials flow.
     */
    fun clientCredentials(block: OpenIdOAuthFlow.() -> Unit) {
        clientCredentials = OpenIdOAuthFlow().apply(block)
    }


    private var authorizationCode: OpenIdOAuthFlow? = null


    /**
     * Configuration for the OAuth Authorization Code flow.
     */
    fun authorizationCode(block: OpenIdOAuthFlow.() -> Unit) {
        authorizationCode = OpenIdOAuthFlow().apply(block)
    }


    fun build(base: OpenIdOAuthFlowsData) = OpenIdOAuthFlowsData(
        implicit = implicit?.build(base.implicit ?: OpenIdOAuthFlowData.DEFAULT) ?: base.implicit,
        password = password?.build(base.password ?: OpenIdOAuthFlowData.DEFAULT) ?: base.password,
        clientCredentials = clientCredentials?.build(base.clientCredentials ?: OpenIdOAuthFlowData.DEFAULT) ?: base.clientCredentials,
        authorizationCode = authorizationCode?.build(base.authorizationCode ?: OpenIdOAuthFlowData.DEFAULT) ?: base.authorizationCode,
    )

}
