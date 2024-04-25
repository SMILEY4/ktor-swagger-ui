package io.github.smiley4.ktorswaggerui.dsl

import io.github.smiley4.ktorswaggerui.data.DataUtils.merge
import io.github.smiley4.ktorswaggerui.data.DataUtils.mergeBoolean
import io.github.smiley4.ktorswaggerui.data.DataUtils.mergeDefault
import io.github.smiley4.ktorswaggerui.data.SwaggerUIData


@OpenApiDslMarker
class SwaggerUIDsl {

    /**
     * Whether to use the automatic swagger-ui router or create swagger-ui router manually.
     * 'false' results in [forwardRoot], [swaggerUrl], [rootHostPath], [authentication] being ignored.
     */
    var automaticRouter: Boolean = SwaggerUIData.DEFAULT.automaticRouter

    /**
     * Whether to forward the root-url to the swagger-url
     */
    var forwardRoot: Boolean = SwaggerUIData.DEFAULT.forwardRoot


    /**
     * the url to the swagger-ui
     */
    var swaggerUrl: String = SwaggerUIData.DEFAULT.swaggerUrl


    /**
     * the path under which the KTOR app gets deployed. can be useful if reverse proxy is in use.
     */
    var rootHostPath: String = SwaggerUIData.DEFAULT.rootHostPath


    /**
     * The name of the authentication to use for the swagger routes. Null to not protect the swagger-ui.
     */
    var authentication: String? = SwaggerUIData.DEFAULT.authentication


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
            automaticRouter = automaticRouter,
            forwardRoot = mergeBoolean(base.forwardRoot, this.forwardRoot),
            swaggerUrl = mergeDefault(base.swaggerUrl, this.swaggerUrl, SwaggerUIData.DEFAULT.swaggerUrl),
            rootHostPath = mergeDefault(base.rootHostPath, this.rootHostPath, SwaggerUIData.DEFAULT.rootHostPath),
            authentication = merge(base.authentication, this.authentication),
            validatorUrl = merge(base.validatorUrl, this.validatorUrl),
            displayOperationId = mergeBoolean(base.displayOperationId, this.displayOperationId),
            showTagFilterInput = mergeBoolean(base.showTagFilterInput, this.showTagFilterInput),
            sort = mergeDefault(base.sort, this.sort, SwaggerUIData.DEFAULT.sort),
            syntaxHighlight = mergeDefault(base.syntaxHighlight, this.syntaxHighlight, SwaggerUIData.DEFAULT.syntaxHighlight),
            withCredentials = base.withCredentials
        )
    }

}

