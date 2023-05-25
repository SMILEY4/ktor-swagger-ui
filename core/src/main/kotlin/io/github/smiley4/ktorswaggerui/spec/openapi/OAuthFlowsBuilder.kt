package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.dsl.OpenIdOAuthFlow
import io.github.smiley4.ktorswaggerui.dsl.OpenIdOAuthFlows
import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.OAuthFlows
import io.swagger.v3.oas.models.security.Scopes

class OAuthFlowsBuilder {

    fun build(flows: OpenIdOAuthFlows): OAuthFlows {
        return OAuthFlows().apply {
            implicit = flows.getImplicit()?.let { build(it) }
            password = flows.getPassword()?.let { build(it) }
            clientCredentials = flows.getClientCredentials()?.let { build(it) }
            authorizationCode = flows.getAuthorizationCode()?.let { build(it) }
        }
    }

    private fun build(flow: OpenIdOAuthFlow): OAuthFlow {
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