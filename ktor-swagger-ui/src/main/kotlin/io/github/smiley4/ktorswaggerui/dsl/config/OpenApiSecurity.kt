package io.github.smiley4.ktorswaggerui.dsl.config

import io.github.smiley4.ktorswaggerui.data.DataUtils.merge
import io.github.smiley4.ktorswaggerui.data.SecurityData
import io.github.smiley4.ktorswaggerui.data.SecuritySchemeData
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker
import io.github.smiley4.ktorswaggerui.dsl.routes.OpenApiResponse
import io.ktor.http.HttpStatusCode

/**
 * Configuration for security and authentication.
 */
@OpenApiDslMarker
class OpenApiSecurity {

    /**
     * Default response to automatically add to each protected route for the "Unauthorized"-Response-Code.
     * Generated response can be overwritten with custom response.
     */
    fun defaultUnauthorizedResponse(block: OpenApiResponse.() -> Unit) {
        defaultUnauthorizedResponse = OpenApiResponse(HttpStatusCode.Unauthorized.value.toString()).apply(block)
    }

    private var defaultUnauthorizedResponse: OpenApiResponse? = null


    /**
     * The names of the security schemes available for use for the protected paths
     */
    var defaultSecuritySchemeNames: Collection<String>? = SecurityData.DEFAULT.defaultSecuritySchemeNames

    /**
     * Set the names of the security schemes available for use for the protected paths
     */
    fun defaultSecuritySchemeNames(names: Collection<String>) {
        this.defaultSecuritySchemeNames = names
    }

    /**
     * Set the names of the security schemes available for use for the protected paths
     */
    fun defaultSecuritySchemeNames(vararg names: String) {
        this.defaultSecuritySchemeNames = names.toList()
    }

    /**
     * Defines security schemes that can be used by operations
     */
    fun securityScheme(name: String, block: OpenApiSecurityScheme.() -> Unit) {
        securitySchemes.add(OpenApiSecurityScheme(name).apply(block))
    }

    private val securitySchemes = mutableListOf<OpenApiSecurityScheme>()

    fun build(base: SecurityData) = SecurityData(
        defaultUnauthorizedResponse = merge(base.defaultUnauthorizedResponse, defaultUnauthorizedResponse?.build()),
        defaultSecuritySchemeNames = buildSet {
            addAll(base.defaultSecuritySchemeNames)
            defaultSecuritySchemeNames?.also { addAll(it) }
        },
        securitySchemes = buildList {
            addAll(base.securitySchemes)
            addAll(securitySchemes.map { it.build(SecuritySchemeData.DEFAULT) })
        }
    )

}
