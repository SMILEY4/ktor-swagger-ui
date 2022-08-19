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
     * A list of parameters that are applicable for this operation
     */
    private val parameters = mutableListOf<RouteParameter>()

    fun pathParameter(block: RouteParameter.() -> Unit) {
        parameters.add(RouteParameter(RouteParameter.Location.PATH).apply(block))
    }

    fun queryParameter(block: RouteParameter.() -> Unit) {
        parameters.add(RouteParameter(RouteParameter.Location.QUERY).apply(block))
    }

    fun headerParameter(block: RouteParameter.() -> Unit) {
        parameters.add(RouteParameter(RouteParameter.Location.HEADER).apply(block))
    }

    fun getParameters(): List<RouteParameter> = parameters


    /**
     * The request body applicable for this operation
     */
    private var requestBody: RouteBody? = null

    fun typedRequestBody(schema: Class<*>, block: RouteTypedBody.() -> Unit) {
        requestBody = RouteTypedBody(schema).apply(block)
    }

    fun textRequestBody(block: RoutePlainTextBody.() -> Unit) {
        requestBody = RoutePlainTextBody().apply(block)
    }

    fun getRequestBody() = requestBody


    /**
     * The list of possible responses as they are returned from executing this operation.
     */
    private val responses = mutableMapOf<HttpStatusCode, RouteResponse>()

    fun response(responseCode: HttpStatusCode, block: RouteResponse.() -> Unit) {
        responses[responseCode] = RouteResponse(responseCode).apply(block)
    }

    fun getResponses() = responses.values.toList()

}


class RouteParameter(val location: Location) {

    enum class Location {
        QUERY, HEADER, PATH
    }

    enum class Type {
        STRING,
        INTEGER,
        NUMBER,
        BOOLEAN,
    }

    sealed class AbstractSchema

    sealed class TypedSchema : AbstractSchema()

    class PrimitiveSchema(val type: Type) : TypedSchema()

    class ObjectSchema(val type: Class<*>) : TypedSchema()

    class ArraySchema(val type: TypedSchema) : AbstractSchema()


    /**
     * The name of the parameter. Parameter names are case-sensitive
     */
    var name: String? = null


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


    /**
     * The schema defining the type used for the parameter.
     */
    private var schema: AbstractSchema? = null

    fun schema(type: Type) {
        schema = PrimitiveSchema(type)
    }

    fun schema(type: Class<*>) {
        schema = ObjectSchema(type)
    }

    fun schemaArray(type: Type) {
        schema = ArraySchema(PrimitiveSchema(type))
    }

    fun schemaArray(type: Class<*>) {
        schema = ArraySchema(ObjectSchema(type))
    }

    fun getSchema() = schema

}


/**
 * Describes a single request/response body.
 */
sealed class RouteBody {

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
 * Describes a single request/response body defined by a java/kotlin class as json
 */
class RouteTypedBody(val schema: Class<*>) : RouteBody()


/**
 * Describes a single request/response body defined as plain text
 */
class RoutePlainTextBody : RouteBody()


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

    fun typedBody(schema: Class<*>, block: RouteTypedBody.() -> Unit) {
        body = RouteTypedBody(schema).apply(block)
    }

    fun textBody(block: RoutePlainTextBody.() -> Unit) {
        body = RoutePlainTextBody().apply(block)
    }

    fun getBody() = body

}

