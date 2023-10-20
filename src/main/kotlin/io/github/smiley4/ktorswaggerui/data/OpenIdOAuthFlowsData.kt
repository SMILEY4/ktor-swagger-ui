package io.github.smiley4.ktorswaggerui.data

data class OpenIdOAuthFlowsData(
    val implicit: OpenIdOAuthFlowData?,
    val password: OpenIdOAuthFlowData?,
    val clientCredentials: OpenIdOAuthFlowData?,
    val authorizationCode: OpenIdOAuthFlowData?,
) {

    companion object {
        val DEFAULT = OpenIdOAuthFlowsData(
            implicit = null,
            password = null,
            clientCredentials = null,
            authorizationCode = null,
        )
    }

}
