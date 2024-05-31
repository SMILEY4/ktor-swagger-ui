package io.github.smiley4.ktorswaggerui.dsl.config

import io.github.smiley4.ktorswaggerui.data.ContactData
import io.github.smiley4.ktorswaggerui.data.DataUtils.merge
import io.github.smiley4.ktorswaggerui.data.DataUtils.mergeDefault
import io.github.smiley4.ktorswaggerui.data.InfoData
import io.github.smiley4.ktorswaggerui.data.LicenseData
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker

@OpenApiDslMarker
class OpenApiInfo {

    /**
     * The title of the api
     */
    var title: String = "API"


    /**
     * The version of the OpenAPI document
     */
    var version: String? = "latest"


    /**
     * A short description of the API
     */
    var description: String? = null

    /**
     * A short summary of the API
     */
    var summary: String? = null


    /**
     * A URL to the Terms of Service for the API. MUST be in the format of a URL.
     */
    var termsOfService: String? = null

    private var contact: OpenApiContact? = null


    /**
     * The contact information for the exposed API.
     */
    fun contact(block: OpenApiContact.() -> Unit) {
        contact = OpenApiContact().apply(block)
    }


    private var license: OpenApiLicense? = null


    /**
     * The license information for the exposed API.
     */
    fun license(block: OpenApiLicense.() -> Unit) {
        license = OpenApiLicense().apply(block)
    }


    fun build(base: InfoData): InfoData {
        return InfoData(
            title = mergeDefault(base.title, this.title, InfoData.DEFAULT.title),
            version = merge(base.version, this.version),
            description = merge(base.description, this.description),
            termsOfService = merge(base.termsOfService, this.termsOfService),
            contact = contact?.build(base.contact ?: ContactData.DEFAULT) ?: base.contact,
            license = license?.build(base.license ?: LicenseData.DEFAULT) ?: base.license,
            summary = merge(base.summary, this.summary)
        )
    }

}
