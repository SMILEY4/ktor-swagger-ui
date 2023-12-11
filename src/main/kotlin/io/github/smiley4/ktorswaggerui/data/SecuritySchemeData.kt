package io.github.smiley4.ktorswaggerui.data

data class SecuritySchemeData(
    val schemeName: String,
    val type: AuthType?,
    val name: String?,
    val location: AuthKeyLocation?,
    val scheme: AuthScheme?,
    val bearerFormat: String?,
    val flows: OpenIdOAuthFlowsData?,
    val openIdConnectUrl: String?,
    val description: String?
) {

    companion object {
        val DEFAULT = SecuritySchemeData(
            schemeName = "",
            type = null,
            name = null,
            location = null,
            scheme = null,
            bearerFormat = null,
            flows = null,
            openIdConnectUrl = null,
            description = null,
        )
    }

}
