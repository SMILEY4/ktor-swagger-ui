package io.github.smiley4.ktorswaggerui.data

/**
 * Common security configuration information.
 */
data class SecurityData(
    val defaultUnauthorizedResponse: OpenApiResponseData?,
    val defaultSecuritySchemeNames: Set<String>,
    val securitySchemes: List<SecuritySchemeData>,
) {
    companion object {
        val DEFAULT = SecurityData(
            defaultUnauthorizedResponse = null,
            defaultSecuritySchemeNames = emptySet(),
            securitySchemes = emptyList()
        )
    }
}
