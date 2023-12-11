package io.github.smiley4.ktorswaggerui.dsl

import io.github.smiley4.ktorswaggerui.data.AuthKeyLocation
import io.github.smiley4.ktorswaggerui.data.AuthScheme
import io.github.smiley4.ktorswaggerui.data.AuthType
import io.github.smiley4.ktorswaggerui.data.DataUtils.merge
import io.github.smiley4.ktorswaggerui.data.OpenIdOAuthFlowsData
import io.github.smiley4.ktorswaggerui.data.SecuritySchemeData


/**
 * Defines a security scheme that can be used by the operations. Supported schemes are HTTP authentication, an API key (either as a header,
 * a cookie parameter or as a query parameter), OAuth2's common flows (implicit, password, client credentials and authorization code)
 */
@OpenApiDslMarker
class OpenApiSecurityScheme(
    /**
     * The name of the security scheme.
     */
    val schemeName: String
) {

    /**
     * The type of the security scheme
     */
    var type: AuthType? = null

    /**
     * The name scheme and of the header, query or cookie parameter to be used.
     */
    var name: String? = null

    /**
     * The location of the API key (OpenAPI 'in').
     * Required for type [AuthType.API_KEY]
     */
    var location: AuthKeyLocation? = null


    /**
     * The name of the HTTP Authorization scheme to be used.
     * Required for type [AuthType.HTTP]
     */
    var scheme: AuthScheme? = null


    /**
     * A hint to the client to identify how the bearer token is formatted.
     * Used for type [AuthType.HTTP] and schema [AuthScheme.BEARER]
     */
    var bearerFormat: String? = null

    private var flows: OpenIdOAuthFlows? = null


    /**
     * information for the oauth flow types supported.
     * Required for type [AuthType.OAUTH2]
     */
    fun flows(block: OpenIdOAuthFlows.() -> Unit) {
        flows = OpenIdOAuthFlows().apply(block)
    }


    /**
     * OpenId Connect URL to discover OAuth2 configuration values.
     * Required for type [AuthType.OPENID_CONNECT]
     */
    var openIdConnectUrl: String? = null


    /**
     * A short description for security scheme.
     */
    var description: String? = null


    fun build(base: SecuritySchemeData) = SecuritySchemeData(
        schemeName = schemeName,
        type = merge(base.type, type),
        name = merge(base.name, name),
        location = merge(base.location, location),
        scheme = merge(base.scheme, scheme),
        bearerFormat = merge(base.bearerFormat, bearerFormat),
        flows = flows?.build(base.flows ?: OpenIdOAuthFlowsData.DEFAULT) ?: base.flows,
        openIdConnectUrl = merge(base.openIdConnectUrl, openIdConnectUrl),
        description = merge(base.description, description),
    )
}
