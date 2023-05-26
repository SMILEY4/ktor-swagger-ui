package io.github.smiley4.ktorswaggerui.dsl


@OpenApiDslMarker
class OpenApiRequestParameter(
    /**
     * The name (case-sensitive) of the parameter
     */
    val name: String,
    /**
     * The type defining the schema used for the parameter.
     */
    val type: SchemaType,
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
     * An example value for this parameter
     */
    var example: Any? = null


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
