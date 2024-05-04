package io.github.smiley4.ktorswaggerui.dsl.routes

import io.github.smiley4.ktorswaggerui.data.OpenApiRouteData
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker

@OpenApiDslMarker
class OpenApiRoute {

    /**
     * the id of the openapi-spec this route belongs to. 'Null' to use default spec.
     */
    var specId: String? = null


    /**
     * A list of tags for API documentation control. Tags can be used for logical grouping of operations by resources or any other qualifier
     */
    var tags: List<String> = emptyList()


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
     * A declaration of which security mechanisms can be used for this operation (i.e. any of the specified ones).
     * If none is specified, defaultSecuritySchemeName (global plugin config) will be used.
     * Only applied to [protected] operations.
     */
    var securitySchemeNames: Collection<String>? = null


    /**
     * Specifies whether this operation is protected.
     * If not specified, the authentication state of the Ktor route will be used
     * (i.e. whether it is surrounded by an [authenticate][io.ktor.server.auth.authenticate] block or not).
     */
    var protected: Boolean? = null

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


    fun build() = OpenApiRouteData(
        specId = specId,
        tags = tags,
        summary = summary,
        description = description,
        operationId = operationId,
        deprecated = deprecated,
        hidden = hidden,
        securitySchemeNames = securitySchemeNames?.toList() ?: emptyList(),
        protected = protected,
        request = request.build(),
        responses = responses.getResponses().map { it.build() },
    )

}
