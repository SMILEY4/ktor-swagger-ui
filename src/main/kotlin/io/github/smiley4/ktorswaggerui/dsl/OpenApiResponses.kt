package io.github.smiley4.ktorswaggerui.dsl

import io.ktor.http.HttpStatusCode

/**
 * All possible responses of an operation
 */
@OpenApiDslMarker
class OpenApiResponses {

    private val responses = mutableMapOf<String, OpenApiResponse>()


    /**
     * Information of response for a given http status code
     */
    infix fun String.to(block: OpenApiResponse.() -> Unit) {
        responses[this] = OpenApiResponse(this).apply(block)
    }


    /**
     * Information of response for a given http status code
     */
    infix fun HttpStatusCode.to(block: OpenApiResponse.() -> Unit) = this.value.toString() to block


    /**
     * Information of the default response
     */
    fun default(block: OpenApiResponse.() -> Unit) = "default" to block


    /**
     * Add the given response. Intended for internal use.
     */
    fun addResponse(response: OpenApiResponse) {
        responses[response.statusCode] = response
    }


    fun getResponses() = responses.values.toList()

}
