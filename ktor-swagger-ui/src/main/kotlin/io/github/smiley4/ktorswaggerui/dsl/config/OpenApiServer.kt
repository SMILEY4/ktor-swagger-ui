package io.github.smiley4.ktorswaggerui.dsl.config

import io.github.smiley4.ktorswaggerui.data.DataUtils.merge
import io.github.smiley4.ktorswaggerui.data.DataUtils.mergeDefault
import io.github.smiley4.ktorswaggerui.data.ServerData
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker

/**
 * An object representing a Server.
 */
@OpenApiDslMarker
class OpenApiServer {

    /**
     * A URL to the target host. This URL supports Server Variables and MAY be relative, to indicate that the host location is relative to
     * the location where the OpenAPI document is being served
     */
    var url: String = ServerData.DEFAULT.url

    /**
     * An optional string describing the host designated by the URL
     */
    var description: String? = ServerData.DEFAULT.description

    private val variables = mutableMapOf<String, OpenApiServerVariable>()


    /**
     * Adds a new server variable with the given name
     */
    fun variable(name: String, block: OpenApiServerVariable.() -> Unit) {
        variables[name] = OpenApiServerVariable(name).apply(block)
    }

    /**
     * Build the data object for this config.
     * @param base the base config to "inherit" from. Values from the base should be copied, replaced or merged together.
     */
    fun build(base: ServerData) = ServerData(
        url = mergeDefault(base.url, url, ServerData.DEFAULT.url),
        description = merge(base.description, description),
        variables = buildMap {
            base.variables.forEach { this[it.name] = it }
            variables.values.map { it.build() }.forEach { this[it.name] = it }
        }.values.toList()
    )

}
