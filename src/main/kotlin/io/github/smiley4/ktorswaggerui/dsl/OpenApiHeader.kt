package io.github.smiley4.ktorswaggerui.dsl

import kotlin.reflect.KClass


@OpenApiDslMarker
class OpenApiHeader {

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
