package io.github.smiley4.ktorswaggerui.dsl

@OpenApiDslMarker
class SwaggerUI {

    /**
     * Whether to forward the root-url to the swagger-url
     */
    var forwardRoot: Boolean = false


    /**
     * the url to the swagger-ui
     */
    var swaggerUrl: String = "swagger-ui"

}