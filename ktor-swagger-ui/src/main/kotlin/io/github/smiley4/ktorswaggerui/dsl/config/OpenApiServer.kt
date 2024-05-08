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


    fun build(base: ServerData) = ServerData(
        url = mergeDefault(base.url, url, ServerData.DEFAULT.url),
        description = merge(base.description, description)
    )

}
