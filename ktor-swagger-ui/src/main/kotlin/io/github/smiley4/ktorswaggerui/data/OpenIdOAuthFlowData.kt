package io.github.smiley4.ktorswaggerui.data

/**
 * See [OpenAPI Specification - OAuth Flow Object](https://swagger.io/specification/#oauth-flow-object).
 */
data class OpenIdOAuthFlowData(
    val authorizationUrl: String? = null,
    val tokenUrl: String? = null,
    val refreshUrl: String? = null,
    val scopes: Map<String, String>? = null,
) {

    companion object {
        val DEFAULT = OpenIdOAuthFlowData(
            authorizationUrl = null,
            tokenUrl = null,
            refreshUrl = null,
            scopes = null,
        )
    }

}
