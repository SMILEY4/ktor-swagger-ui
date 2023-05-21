package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.spec.route.RouteMeta

class OperationTagsBuilder(
    private val config: SwaggerUIPluginConfig
) {

    fun build(route: RouteMeta): List<String> {
        val generatedTags = config.automaticTagGenerator?.let {
            it(route.path.split("/").filter { it.isNotEmpty() })
        }
        return (route.documentation.tags + generatedTags).filterNotNull()
    }

}