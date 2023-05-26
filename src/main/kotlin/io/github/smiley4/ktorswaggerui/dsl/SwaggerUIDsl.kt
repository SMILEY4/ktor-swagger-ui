package io.github.smiley4.ktorswaggerui.dsl

@OpenApiDslMarker
class SwaggerUIDsl {

    /**
     * Whether to forward the root-url to the swagger-url
     */
    var forwardRoot: Boolean = false

    /**
     * the url to the swagger-ui
     */
    var swaggerUrl: String = "swagger-ui"

    /**
     * The name of the authentication to use for the swagger routes. Null to not protect the swagger-ui.
     */
    var authentication: String? = null

    /**
     * Swagger UI can attempt to validate specs against swagger.io's online validator.
     * You can use this parameter to set a different validator URL, for example for locally deployed validators.
     * Set to "null" to disable validation.
     * Validation is disabled when the url of the api-spec-file contains localhost.
     * (see https://github.com/swagger-api/swagger-ui/blob/master/docs/usage/configuration.md#network)
     */
    private var validatorUrl: String? = null

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
    var displayOperationId = false

    /**
     * Whether the top bar will show an edit box that you can use to filter the tagged operations.
     */
    var showTagFilterInput = false

    /**
     * Apply a sort to the operation list of each API
     */
    var sort = SwaggerUiSort.NONE

    /**
     * Syntax coloring theme to use
     */
    var syntaxHighlight = SwaggerUiSyntaxHighlight.AGATE

}

enum class SwaggerUiSort(val value: String) {
    /**
     * The order returned by the server unchanged
     */
    NONE("undefined"),

    /**
     * sort by paths alphanumerically
     */
    ALPHANUMERICALLY("alpha"),

    /**
     * sort by HTTP method
     */
    HTTP_METHOD("method")
}

enum class SwaggerUiSyntaxHighlight(val value: String) {
    AGATE("agate"),
    ARTA("arta"),
    MONOKAI("monokai"),
    NORD("nord"),
    OBSIDIAN("obsidian"),
    TOMORROW_NIGHT("tomorrow-night")
}