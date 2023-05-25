package io.github.smiley4.ktorswaggerui.dsl

@OpenApiDslMarker
class OpenApiRoute {

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
     * A declaration of which security mechanism can be used for this operation.
     * If not specified (and none specified with [securitySchemeNames]), defaultSecuritySchemeName (global plugin config) will be used
     */
    var securitySchemeName: String? = null


    /**
     * A declaration of which security mechanisms can be used for this operation (i.e. any of the specified ones).
     * If none specified (and none with [securitySchemeName]), defaultSecuritySchemeName (global plugin config) will be used.
     */
    var securitySchemeNames: Collection<String>? = null

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

}
