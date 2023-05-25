package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.spec.route.RouteMeta

class OperationTagsBuilder(
    private val config: SwaggerUIPluginConfig
) {

    fun build(route: RouteMeta): List<String> {
        return mutableSetOf<String?>().also { tags ->
            tags.add(getGeneratedTagsDeprecated(route))
            tags.addAll(getGeneratedTags(route))
            tags.addAll(getRouteTags(route))
        }.filterNotNull()
    }

    private fun getRouteTags(route: RouteMeta) = route.documentation.tags

    @Deprecated("see PluginConfig#automaticTagGenerator")
    private fun getGeneratedTagsDeprecated(route: RouteMeta) = config.automaticTagGenerator?.let { it(route.path.split("/")) }

    private fun getGeneratedTags(route: RouteMeta) = config.getTagGenerator()(route.path.split("/"))

}