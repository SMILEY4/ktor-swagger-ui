package io.github.smiley4.ktorswaggerui.dsl.routes

import io.github.smiley4.ktorswaggerui.data.ExampleDescriptor
import io.github.smiley4.ktorswaggerui.data.OpenApiRequestParameterData
import io.github.smiley4.ktorswaggerui.data.ParameterLocation
import io.github.smiley4.ktorswaggerui.data.SwaggerExampleDescriptor
import io.github.smiley4.ktorswaggerui.data.TypeDescriptor
import io.github.smiley4.ktorswaggerui.data.ValueExampleDescriptor
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.parameters.Parameter

/**
 * Describes a single request parameter.
 */
@OpenApiDslMarker
class OpenApiRequestParameter(
    /**
     * The name (case-sensitive) of the parameter
     */
    val name: String,
    /**
     * The type defining the schema used for the parameter.
     */
    val type: TypeDescriptor,
    /**
     * Location of the parameter
     */
    val location: ParameterLocation
) {

    /**
     * A brief description of the parameter
     */
    var description: String? = null


    /**
     * An example value for this parameter
     */
    var example: ExampleDescriptor? = null

    /**
     * An example value for this parameter
     */
    fun example(example: ExampleDescriptor) {
        this.example = example
    }

    /**
     * An example value for this parameter
     */
    fun example(name: String, example: Example) = example(SwaggerExampleDescriptor(name, example))

    /**
     * An example value for this parameter
     */
    fun example(name: String, example: ValueExampleDescriptorDsl.() -> Unit) = example(
        ValueExampleDescriptorDsl()
            .apply(example)
            .let { result ->
                ValueExampleDescriptor(
                    name = name,
                    value = result.value,
                    summary = result.summary,
                    description = result.description
                )
            }
    )


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
     * Describes how the parameter value will be serialized depending on the type of the parameter value.
     */
    var style: Parameter.StyleEnum? = null

    fun build() = OpenApiRequestParameterData(
        name = name,
        type = type,
        location = location,
        description = description,
        example = example,
        required = required ?: false,
        deprecated = deprecated ?: false,
        allowEmptyValue = allowEmptyValue ?: true,
        explode = explode ?: false,
        allowReserved = allowReserved ?: true,
        style = style
    )

}
