package io.github.smiley4.ktorswaggerui.dsl.routes

import io.github.smiley4.ktorswaggerui.data.ExampleDescriptor
import io.github.smiley4.ktorswaggerui.data.OpenApiSimpleBodyData
import io.github.smiley4.ktorswaggerui.data.TypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker


/**
 * Describes the base of a single request/response body.
 */
@OpenApiDslMarker
class OpenApiSimpleBody(
    /**
     * The type defining the schema used for the body.
     */
    val type: TypeDescriptor,
) : OpenApiBaseBody() {

    /**
     * Examples for this body
     */
    private val examples = mutableListOf<ExampleDescriptor>()

//    fun example(name: String, value: Any, block: OpenApiExample.() -> Unit) {
//        examples[name] = OpenApiExample(value).apply(block)
//    }

    fun example(example: ExampleDescriptor) {
        examples.add(example)
    }

    fun getExamples(): List<ExampleDescriptor> = examples


    override fun build() = OpenApiSimpleBodyData(
        description = description,
        required = required ?: false,
        mediaTypes = getMediaTypes(),
        type = type,
        examples = getExamples(),
    )

}