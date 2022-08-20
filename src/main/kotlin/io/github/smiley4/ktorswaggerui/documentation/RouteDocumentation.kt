package io.github.smiley4.ktorswaggerui.documentation

import io.ktor.http.HttpStatusCode

class RouteDocumentation {

    /**
     * A list of tags for API documentation control. Tags can be used for logical grouping of operations by resources or any other qualifier
     */
    var tags: List<String> = emptyList()


    /**
     * A short summary of what the operation does.
     */
    var summary: String? = null


    /**
     * A verbose explanation of the operation behavior.
     */
    var description: String? = null


    /**
     * A declaration of which security mechanism can be used for this operation.
     * If not specified, defaultSecuritySchemeName (global plugin config) will be used
     */
    var securitySchemeName: String? = null


    /**
     * A list of parameters that are applicable for this operation
     */
    private val parameters = mutableListOf<RouteParameter>()

    fun pathParameter(name: String, schema: Class<*>, block: RouteParameter.() -> Unit) {
        parameters.add(RouteParameter(name, schema, RouteParameter.Location.PATH).apply(block))
    }

    fun queryParameter(name: String, schema: Class<*>, block: RouteParameter.() -> Unit) {
        parameters.add(RouteParameter(name, schema, RouteParameter.Location.QUERY).apply(block))
    }

    fun headerParameter(name: String, schema: Class<*>, block: RouteParameter.() -> Unit) {
        parameters.add(RouteParameter(name, schema, RouteParameter.Location.HEADER).apply(block))
    }

    fun getParameters(): List<RouteParameter> = parameters


    /**
     * The request body applicable for this operation
     */
    private var requestBody: RouteBody? = null

    fun requestBody(schema: Class<*>, block: RouteBody.() -> Unit) {
        requestBody = RouteBody(schema).apply(block)
    }

    fun requestBody(schema: Class<*>) = requestBody(schema) {}

    fun getRequestBody() = requestBody


    /**
     * The list of possible responses as they are returned from executing this operation.
     */
    private val responses = mutableMapOf<HttpStatusCode, RouteResponse>()

    fun response(responseCode: HttpStatusCode, block: RouteResponse.() -> Unit) {
        responses[responseCode] = RouteResponse(responseCode).apply(block)
    }

    fun response(responseCode: HttpStatusCode, description: String) = response(responseCode) { this.description = description }

    fun getResponses() = responses.values.toList()

}


class RouteParameter(
    /**
     * The name (case-sensitive) of the parameter
     */
    val name: String,
    /**
     * The schema defining the type used for the parameter.
     * Examples:
     * - Int::class.java
     * - UByte::class.java
     * - BooleanArray::class.java
     * - Array<String>::class.java
     * - Array<MyClass>::class.java
     */
    val schema: Class<*>,
    /**
     * Location of the parameter
     */
    val location: Location
) {

    enum class Location {
        QUERY, HEADER, PATH
    }


    /**
     * A brief description of the parameter
     */
    var description: String? = null


    /**
     * Determines whether this parameter is mandatory
     */
    var required: Boolean? = null


    /**
     * Specifies that a parameter is deprecated and SHOULD be transitioned out of usage
     */
    var deprecated: Boolean? = null


    /**
     * Sets the ability to pass empty-valued parameters.
     * This is valid only for query parameters and allows sending a parameter with an empty value.
     */
    var allowEmptyValue: Boolean? = null


    /**
     * When this is true, parameter values of type array or object generate separate parameters for each value of the array or key-value
     * pair of the map. For other types of parameters this property has no effect
     */
    var explode: Boolean? = null


    /**
     * Determines whether the parameter value SHOULD allow reserved characters, as defined by RFC3986 :/?#[]@!$&'()*+,;= to be included
     * without percent-encoding. This property only applies to parameters with an in value of query
     */
    var allowReserved: Boolean? = null

}


/**
 * Describes a single request/response body.
 */
class RouteBody(
    /**
     * The schema defining the type used for the parameter.
     * Examples:
     * - Int::class.java
     * - UByte::class.java
     * - BooleanArray::class.java
     * - Array<String>::class.java
     * - Array<MyClass>::class.java
     */
    val schema: Class<*>,
) {

    /**
     * A brief description of the request body
     */
    var description: String? = null


    /**
     * Determines if the request body is required in the request
     */
    var required: Boolean? = null

}


/**
 * A container for the expected responses of an operation. The container maps a HTTP response code to the expected response.
 * A response code can only have one response object.
 */
class RouteResponse(val statusCode: HttpStatusCode) {

    /**
     * A short description of the response
     */
    var description: String? = null


    /**
     * The optional response body
     */
    private var body: RouteBody? = null

    fun body(schema: Class<*>, block: RouteBody.() -> Unit) {
        body = RouteBody(schema).apply(block)
    }

    fun body(schema: Class<*>) = body(schema) {}

    fun getBody() = body

}

