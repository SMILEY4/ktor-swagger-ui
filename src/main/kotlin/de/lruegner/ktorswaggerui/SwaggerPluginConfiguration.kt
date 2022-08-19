package de.lruegner.ktorswaggerui

class SwaggerPluginConfiguration {
    var forwardRoot: Boolean = false
    var swaggerUrl: String = "swagger-ui"
    var info: OpenAPIInfoConfiguration = OpenAPIInfoConfiguration()
    var servers: List<OpenAPIServerConfiguration> = emptyList()

    fun info(block: OpenAPIInfoConfiguration.() -> Unit): OpenAPIInfoConfiguration {
        return OpenAPIInfoConfiguration().apply(block)
    }

    fun server(block: OpenAPIServerConfiguration.() -> Unit): OpenAPIServerConfiguration {
        return OpenAPIServerConfiguration().apply(block)
    }

}


class OpenAPIInfoConfiguration {
    var title: String? = null
    var description: String? = null
    var version: String? = null
}

class OpenAPIServerConfiguration {
    var url: String? = null
    var description: String? = null
}