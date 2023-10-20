package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.data.OpenIdOAuthFlowData
import io.github.smiley4.ktorswaggerui.data.OpenIdOAuthFlowsData
import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.OAuthFlows
import io.swagger.v3.oas.models.security.Scopes

class OAuthFlowsBuilder {

    fun build(flows: OpenIdOAuthFlowsData): OAuthFlows {
        return OAuthFlows().apply {
            implicit = flows.implicit?.let { build(it) }
            password = flows.password?.let { build(it) }
            clientCredentials = flows.clientCredentials?.let { build(it) }
            authorizationCode = flows.authorizationCode?.let { build(it) }
        }
    }

    private fun build(flow: OpenIdOAuthFlowData): OAuthFlow {
        return OAuthFlow().apply {
            authorizationUrl = flow.authorizationUrl
            tokenUrl = flow.tokenUrl
            refreshUrl = flow.refreshUrl
            scopes = flow.scopes?.let { buildScopes(it) }
        }
    }

    private fun buildScopes(scopes: Map<String, String>): Scopes {
        return Scopes().apply {
            scopes.forEach { (k, v) -> addString(k, v) }
        }
    }

}
