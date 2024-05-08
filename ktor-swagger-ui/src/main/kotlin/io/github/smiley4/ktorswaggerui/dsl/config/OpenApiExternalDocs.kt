package io.github.smiley4.ktorswaggerui.dsl.config

import io.github.smiley4.ktorswaggerui.data.DataUtils
import io.github.smiley4.ktorswaggerui.data.ExternalDocsData
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker

/**
 * An object representing external documentation.
 */
@OpenApiDslMarker
class OpenApiExternalDocs {

    /**
     * A short description of the external documentation
     */
    var description: String? = null


    /**
     * A URL to the external documentation
     */
    var url: String = "/"


    fun build(base: ExternalDocsData) = ExternalDocsData(
        url = DataUtils.mergeDefault(base.url, url, ExternalDocsData.DEFAULT.url),
        description = DataUtils.merge(base.description, description)
    )

}
