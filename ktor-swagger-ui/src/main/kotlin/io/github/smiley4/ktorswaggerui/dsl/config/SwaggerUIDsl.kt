package io.github.smiley4.ktorswaggerui.dsl.config

import io.github.smiley4.ktorswaggerui.data.DataUtils.merge
import io.github.smiley4.ktorswaggerui.data.DataUtils.mergeBoolean
import io.github.smiley4.ktorswaggerui.data.DataUtils.mergeDefault
import io.github.smiley4.ktorswaggerui.data.SwaggerUIData
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker


@OpenApiDslMarker
class SwaggerUIDsl {

    /**
     * Swagger UI can attempt to validate specs against swagger.io's online validator.
     * You can use this parameter to set a different validator URL, for example for locally deployed validators.
     * Set to "null" to disable validation.
     * Validation is disabled when the url of the api-spec-file contains localhost.
     * (see https://github.com/swagger-api/swagger-ui/blob/master/docs/usage/configuration.md#network)
     */
    private var validatorUrl: String? = SwaggerUIData.DEFAULT.validatorUrl

    fun disableSpecValidator() {
        validatorUrl = null
    }

    fun specValidator(url: String) {
        validatorUrl = url
    }

    fun onlineSpecValidator() {
        specValidator("https://validator.swagger.io/validator")
    }

    fun getSpecValidatorUrl() = validatorUrl


    /**
     * Whether to show the operation-id of endpoints in the list
     */
    var displayOperationId = SwaggerUIData.DEFAULT.displayOperationId


    /**
     * Whether the top bar will show an edit box that you can use to filter the tagged operations.
     */
    var showTagFilterInput = SwaggerUIData.DEFAULT.showTagFilterInput


    /**
     * Apply a sort to the operation list of each API
     */
    var sort = SwaggerUIData.DEFAULT.sort


    /**
     * Syntax coloring theme to use
     */
    var syntaxHighlight = SwaggerUIData.DEFAULT.syntaxHighlight

    /**
     * Bring cookies when initiating network requests
     */
    var withCredentials: Boolean = false

    internal fun build(base: SwaggerUIData): SwaggerUIData {
        return SwaggerUIData(
            validatorUrl = merge(base.validatorUrl, this.validatorUrl),
            displayOperationId = mergeBoolean(base.displayOperationId, this.displayOperationId),
            showTagFilterInput = mergeBoolean(base.showTagFilterInput, this.showTagFilterInput),
            sort = mergeDefault(base.sort, this.sort, SwaggerUIData.DEFAULT.sort),
            syntaxHighlight = mergeDefault(base.syntaxHighlight, this.syntaxHighlight, SwaggerUIData.DEFAULT.syntaxHighlight),
            withCredentials = mergeBoolean(base.withCredentials, this.withCredentials)
        )
    }

}

