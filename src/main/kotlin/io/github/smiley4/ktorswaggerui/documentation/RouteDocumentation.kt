package io.github.smiley4.ktorswaggerui.documentation

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import kotlin.reflect.KClass

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
     * A verbose explanation of the operations' behavior.
     */
    var description: String? = null


    /**
     * A declaration of which security mechanism can be used for this operation.
     * If not specified, defaultSecuritySchemeName (global plugin config) will be used
     */
    var securitySchemeName: String? = null


    /**
     * Information about the request
     */
    private val request = RequestDocumentation()

    fun request(block: RequestDocumentation.() -> Unit) {
        request.apply(block)
    }

    fun getRequest() = request


    /**
     * Possible responses as they are returned from executing this operation.
     */
    private val responses = ResponseDocumentation()

    fun response(block: ResponseDocumentation.() -> Unit) {
        responses.apply(block)
    }

    fun getResponses() = responses

}


class RequestDocumentation {

    /**
     * A list of parameters that are applicable for this operation
     */
    private val parameters = mutableListOf<RequestParameterDocumentation>()

    fun pathParameter(name: String, schema: KClass<*>, block: RequestParameterDocumentation.() -> Unit) {
        parameters.add(RequestParameterDocumentation(name, schema, RequestParameterDocumentation.Location.PATH).apply(block))
    }

    fun pathParameter(name: String, schema: KClass<*>) = pathParameter(name, schema) {}

    fun queryParameter(name: String, schema: KClass<*>, block: RequestParameterDocumentation.() -> Unit) {
        parameters.add(RequestParameterDocumentation(name, schema, RequestParameterDocumentation.Location.QUERY).apply(block))
    }

    fun queryParameter(name: String, schema: KClass<*>) = queryParameter(name, schema) {}


    fun headerParameter(name: String, schema: KClass<*>, block: RequestParameterDocumentation.() -> Unit) {
        parameters.add(RequestParameterDocumentation(name, schema, RequestParameterDocumentation.Location.HEADER).apply(block))
    }

    fun headerParameter(name: String, schema: KClass<*>) = headerParameter(name, schema) {}

    fun getParameters(): List<RequestParameterDocumentation> = parameters


    /**
     * The request body applicable for this operation
     */
    private var body: BodyDocumentation? = null

    fun body(schema: KClass<*>, block: BodyDocumentation.() -> Unit) {
        body = BodyDocumentation(schema).apply(block)
    }

    fun body(schema: KClass<*>) = body(schema) {}

    fun body(block: BodyDocumentation.() -> Unit) {
        body = BodyDocumentation(null).apply(block)
    }

    fun setBody(body: BodyDocumentation?) {
        this.body = body
    }

    fun getBody() = body

}


class RequestParameterDocumentation(
    /**
     * The name (case-sensitive) of the parameter
     */
    val name: String,
    /**
     * The schema defining the type used for the parameter.
     * Examples:
     * - Int::class
     * - UByte::class
     * - BooleanArray::class
     * - Array<String>::class
     * - Array<MyClass>::class
     */
    val schema: KClass<*>,
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


class ResponseDocumentation {

    private val responses = mutableMapOf<HttpStatusCode, SingleResponseDocumentation>()

    infix fun HttpStatusCode.to(block: SingleResponseDocumentation.() -> Unit) {
        responses[this] = SingleResponseDocumentation(this).apply(block)
    }

    fun addResponse(response: SingleResponseDocumentation) {
        responses[response.statusCode] = response
    }

    fun getResponses() = responses.values.toList()

}


/**
 * A container for the expected responses of an operation. The container maps a HTTP response code to the expected response.
 * A response code can only have one response object.
 */
class SingleResponseDocumentation(val statusCode: HttpStatusCode) {

    /**
     * A short description of the response
     */
    var description: String? = null


    /**
     * Possible headers returned with this response
     */
    private val headers = mutableMapOf<String, HeaderDocumentation>()

    fun header(name: String, block: HeaderDocumentation.() -> Unit) {
        headers[name] = HeaderDocumentation().apply(block)
    }

    fun header(name: String, schema: KClass<*>) {
        headers[name] = HeaderDocumentation().apply {
            this.schema = schema
        }
    }

    fun getHeaders(): Map<String, HeaderDocumentation> = headers


    /**
     * The optional response body
     */
    private var body: BodyDocumentation? = null

    fun body(schema: KClass<*>, block: BodyDocumentation.() -> Unit) {
        body = BodyDocumentation(schema).apply(block)
    }

    fun body(schema: KClass<*>) = body(schema) {}

    fun body(block: BodyDocumentation.() -> Unit) {
        body = BodyDocumentation(null).apply(block)
    }

    fun getBody() = body

}


class HeaderDocumentation {

    /**
     * A description of the header
     */
    var description: String? = null


    /**
     * The schema of the header
     */
    var schema: KClass<*>? = null

    /**
     * Determines whether this header is mandatory
     */
    var required: Boolean? = null


    /**
     * Specifies that a header is deprecated and SHOULD be transitioned out of usage
     */
    var deprecated: Boolean? = null

}


/**
 * Describes a single request/response body.
 */
class BodyDocumentation(
    /**
     * The schema defining the type used for the parameter.
     * Examples:
     * - Int::class.java
     * - UByte::class.java
     * - BooleanArray::class.java
     * - Array<String>::class.java
     * - Array<MyClass>::class.java
     */
    val schema: KClass<*>?,
) {

    /**
     * A brief description of the request body
     */
    var description: String? = null


    /**
     * Determines if the request body is required in the request
     */
    var required: Boolean? = null


    /**
     * Examples for this body
     */
    private val examples = mutableMapOf<String, ExampleDocumentation>()

    fun example(name: String, value: Any, block: ExampleDocumentation.() -> Unit) {
        examples[name] = ExampleDocumentation(value).apply(block)
    }

    fun example(name: String, value: Any) = example(name, value) {}

    fun getExamples(): Map<String, ExampleDocumentation> = examples


    /**
     * Allowed Media Types for this body. If none specified, a media type will be chosen automatically based on the provided schema
     */
    private val mediaTypes = mutableSetOf<ContentType>()

    fun mediaType(type: ContentType) {
        mediaTypes.add(type)
    }

    fun getMediaTypes(): Set<ContentType> = mediaTypes

}


/**
 * Documentation for an example object
 */
class ExampleDocumentation(
    /**
     * The actual example object/value
     */
    val value: Any
) {

    /**
     * A short description of the example
     */
    var summary: String? = null


    /**
     * A long description of the example
     */
    var description: String? = null

}