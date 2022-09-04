package io.github.smiley4.ktorswaggerui.dsl

import io.ktor.http.HttpStatusCode

/**
 * All possible responses of an operation
 */
@OpenApiDslMarker
class OpenApiResponses {

    private val responses = mutableMapOf<HttpStatusCode, OpenApiResponse>()


    /**
     * Information of response for a given http status code
     */
    infix fun HttpStatusCode.to(block: OpenApiResponse.() -> Unit) {
        responses[this] = OpenApiResponse(this).apply(block)
    }


    fun addResponse(response: OpenApiResponse) {
        responses[response.statusCode] = response
    }


    fun getResponses() = responses.values.toList()

}