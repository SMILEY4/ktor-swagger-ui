package io.github.smiley4.ktorswaggerui.data

data class SecuritySchemeData(
    val name: String,
    val type: AuthType?,
    val location: AuthKeyLocation?,
    val scheme: AuthScheme?,
    val bearerFormat: String?,
    val flows: OpenIdOAuthFlowsData?,
    val openIdConnectUrl: String?,
    val description: String?
) {

    companion object {
        val DEFAULT = SecuritySchemeData(
            name = "",
            type = null,
            location = null,
            scheme = null,
            bearerFormat = null,
            flows = null,
            openIdConnectUrl = null,
            description = null,
        )
    }

}
