package io.github.smiley4.ktorswaggerui.dsl

/**
 * An object representing a Server.
 */
@OpenApiDslMarker
class OpenApiServer {

    /**
     * A URL to the target host. This URL supports Server Variables and MAY be relative, to indicate that the host location is relative to
     * the location where the OpenAPI document is being served
     */
    var url: String = "/"


    /**
     * An optional string describing the host designated by the URL
     */
    var description: String? = null

}
