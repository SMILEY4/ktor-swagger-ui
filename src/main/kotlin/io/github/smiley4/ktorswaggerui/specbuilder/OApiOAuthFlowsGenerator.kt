package io.github.smiley4.ktorswaggerui.specbuilder

import io.github.smiley4.ktorswaggerui.dsl.OpenIdOAuthFlow
import io.github.smiley4.ktorswaggerui.dsl.OpenIdOAuthFlows
import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.OAuthFlows
import io.swagger.v3.oas.models.security.Scopes

/**
 * Generator for the Flows Info-Object
 */
class OApiOAuthFlowsGenerator {

    /**
     * Generate the OpenAPI Flows-Object from the given config
     */
    fun generate(config: OpenIdOAuthFlows): OAuthFlows {
        return OAuthFlows().apply {
            implicit = config.getImplicit()?.let { generate(it) }
            password = config.getPassword()?.let { generate(it) }
            clientCredentials = config.getClientCredentials()?.let { generate(it) }
            authorizationCode = config.getAuthorizationCode()?.let { generate(it) }
        }
    }

    private fun generate(flow: OpenIdOAuthFlow): OAuthFlow {
        return OAuthFlow().apply {
            authorizationUrl = flow.authorizationUrl
            tokenUrl = flow.tokenUrl
            refreshUrl = flow.refreshUrl
            scopes = flow.scopes?.let { s ->
                Scopes().apply {
                    s.forEach { (k, v) -> addString(k, v) }
                }
            }
        }
    }

}