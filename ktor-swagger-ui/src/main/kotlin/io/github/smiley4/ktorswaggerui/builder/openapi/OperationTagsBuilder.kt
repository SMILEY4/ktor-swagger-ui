package io.github.smiley4.ktorswaggerui.builder.openapi

import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.github.smiley4.ktorswaggerui.builder.route.RouteMeta

/**
 * Builds the list of tags for a single route.
 */
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

    private fun getGeneratedTags(route: RouteMeta) = config.tagsConfig.generator(route.path.split("/").filter { it.isNotEmpty() })

}
