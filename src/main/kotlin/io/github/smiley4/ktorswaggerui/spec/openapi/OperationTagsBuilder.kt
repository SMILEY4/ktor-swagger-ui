package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.github.smiley4.ktorswaggerui.spec.route.RouteMeta

class OperationTagsBuilder(
    private val config: PluginConfigData
) {

    fun build(route: RouteMeta): List<String> {
        return mutableSetOf<String?>().also { tags ->
            tags.addAll(getGeneratedTags(route))
            tags.addAll(getRouteTags(route))
        }.filterNotNull()
    }

    private fun getRouteTags(route: RouteMeta) = route.documentation.tags

    private fun getGeneratedTags(route: RouteMeta) = config.tagGenerator(route.path.split("/").filter { it.isNotEmpty() })

}
