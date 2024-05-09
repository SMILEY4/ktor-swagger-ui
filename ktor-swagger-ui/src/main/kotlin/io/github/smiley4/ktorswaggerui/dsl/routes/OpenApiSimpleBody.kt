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

    fun example(example: ExampleDescriptor) {
        examples.add(example)
    }


    override fun build() = OpenApiSimpleBodyData(
        description = description,
        required = required ?: false,
        mediaTypes = mediaTypes,
        type = type,
        examples = examples,
    )

}
