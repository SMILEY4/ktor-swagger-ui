package io.github.smiley4.ktorswaggerui.data

data class SwaggerUIData(
    val validatorUrl: String?,
    val displayOperationId: Boolean,
    val showTagFilterInput: Boolean,
    val sort: SwaggerUiSort,
    val syntaxHighlight: SwaggerUiSyntaxHighlight,
    val withCredentials: Boolean,
) {

    companion object {
        val DEFAULT = SwaggerUIData(
            validatorUrl = null,
            displayOperationId = false,
            showTagFilterInput = false,
            sort = SwaggerUiSort.NONE,
            syntaxHighlight = SwaggerUiSyntaxHighlight.AGATE,
            withCredentials = false,
        )
    }

}
