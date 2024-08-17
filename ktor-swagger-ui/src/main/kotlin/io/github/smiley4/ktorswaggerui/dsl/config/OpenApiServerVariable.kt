package io.github.smiley4.ktorswaggerui.dsl.config

import io.github.smiley4.ktorswaggerui.data.ServerVariableData
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker

/**
 * An object representing a Server Variable for server URL template substitution.
 */
@OpenApiDslMarker
class OpenApiServerVariable(
    /**
     * The name of this variable
     */
    private val name: String
) {

    /**
     * An enumeration of string values to be used if the substitution options are from a limited set. Must not be empty.
     */
    var enum: Collection<String> = emptyList()


    /**
     * The default value to use for substitution. Must be in the list of enums.
     */
    var default: String? = null


    /**
     * An optional description for this server variable.
     */
    var description: String? = null

    /**
     * Build the data object for this config.
     */
    fun build() = ServerVariableData(
        name = name,
        enum = enum.toSet(),
        default = (default ?: enum.firstOrNull()) ?: "",
        description = description
    )

}
