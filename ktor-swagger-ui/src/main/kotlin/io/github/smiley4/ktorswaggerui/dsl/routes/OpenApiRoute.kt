package io.github.smiley4.ktorswaggerui.dsl.routes

import io.github.smiley4.ktorswaggerui.data.ExternalDocsData
import io.github.smiley4.ktorswaggerui.data.OpenApiRouteData
import io.github.smiley4.ktorswaggerui.data.ServerData
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker
import io.github.smiley4.ktorswaggerui.dsl.config.OpenApiExternalDocs
import io.github.smiley4.ktorswaggerui.dsl.config.OpenApiServer

/**
 * Describes a single route including request and responses.
 */
@OpenApiDslMarker
class OpenApiRoute {

    /**
     * the id of the openapi-spec this route belongs to. 'Null' to use default spec.
     */
    var specId: String? = null


    /**
     * A list of tags for API documentation control. Tags can be used for logical grouping of operations by resources or any other qualifier
     */
    var tags: Collection<String> = emptyList()

    /**
     * Set the list of tags for API documentation control.
     * Tags can be used for logical grouping of operations by resources or any other qualifier
     */
    fun tags(tags: Collection<String>) {
        this.tags = tags
    }

    /**
     * Set the list of tags for API documentation control.
     * Tags can be used for logical grouping of operations by resources or any other qualifier
     */
    fun tags(vararg tags: String) {
        this.tags = tags.toList()
    }

    /**
     * A short summary of what the operation does.
     */
    var summary: String? = null


    /**
     * A verbose explanation of the operations' behavior.
     */
    var description: String? = null


    /**
     * Unique string used to identify the operation. The id MUST be unique among all operations described in the API.
     * The operationId value is case-sensitive.
     */
    var operationId: String? = null


    /**
     * Whether this route is deprecated
     */
    var deprecated: Boolean = false


    /**
     * Whether this route is hidden.
     */
    var hidden: Boolean = false


    /**
     * Specifies whether this operation is protected.
     * If not specified, the authentication state of the Ktor route will be used
     * (i.e. whether it is surrounded by an [authenticate][io.ktor.server.auth.authenticate] block or not).
     */
    var protected: Boolean? = null


    /**
     * A declaration of which security mechanisms can be used for this operation (i.e. any of the specified ones).
     * If none is specified, defaultSecuritySchemeName (global plugin config) will be used.
     * Only applied to [protected] operations.
     */
    var securitySchemeNames: Collection<String>? = null

    /**
     * Set the declarations of which security mechanisms can be used for this operation (i.e. any of the specified ones).
     * If none is specified, defaultSecuritySchemeName (global plugin config) will be used.
     * Only applied to [protected] operations.
     */
    fun securitySchemeNames(names: Collection<String>) {
        this.securitySchemeNames = names
    }

    /**
     * Set the declarations of which security mechanisms can be used for this operation (i.e. any of the specified ones).
     * If none is specified, defaultSecuritySchemeName (global plugin config) will be used.
     * Only applied to [protected] operations.
     */
    fun securitySchemeNames(vararg names: String) {
        this.securitySchemeNames = names.toList()
    }


    private val request = OpenApiRequest()


    /**
     * Information about the request
     */
    fun request(block: OpenApiRequest.() -> Unit) {
        request.apply(block)
    }

    fun getRequest() = request

    private val responses = OpenApiResponses()


    /**
     * Possible responses as they are returned from executing this operation.
     */
    fun response(block: OpenApiResponses.() -> Unit) {
        responses.apply(block)
    }

    fun getResponses() = responses


    /**
     * OpenAPI external docs configuration - link and description of an external documentation
     */
    fun externalDocs(block: OpenApiExternalDocs.() -> Unit) {
        externalDocs = OpenApiExternalDocs().apply(block)
    }

    private var externalDocs: OpenApiExternalDocs? = null


    /**
     * OpenAPI server configuration - an array of servers, which provide connectivity information to a target server
     */
    fun server(block: OpenApiServer.() -> Unit) {
        servers.add(OpenApiServer().apply(block))
    }

    private val servers = mutableListOf<OpenApiServer>()

    /**
     * Build the data object for this config.
     */
    fun build() = OpenApiRouteData(
        specId = specId,
        tags = tags.toSet(),
        summary = summary,
        description = description,
        operationId = operationId,
        deprecated = deprecated,
        hidden = hidden,
        securitySchemeNames = securitySchemeNames?.toList() ?: emptyList(),
        protected = protected,
        request = request.build(),
        responses = responses.getResponses().map { it.build() },
        externalDocs = externalDocs?.build(ExternalDocsData.DEFAULT),
        servers = servers.map { it.build(ServerData.DEFAULT) }
    )

}
