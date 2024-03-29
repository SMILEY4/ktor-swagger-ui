package io.github.smiley4.ktorswaggerui.data

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
