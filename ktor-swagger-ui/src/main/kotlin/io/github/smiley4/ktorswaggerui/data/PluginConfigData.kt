package io.github.smiley4.ktorswaggerui.data

import io.github.smiley4.ktorswaggerui.dsl.config.PluginConfigDsl
import kotlin.reflect.KClass

/**
 * Complete plugin configuration
 */
data class PluginConfigData(
    val specAssigner: SpecAssigner,
    val pathFilter: PathFilter,
    val ignoredRouteSelectors: Set<KClass<*>>,
    val swagger: SwaggerUIData,
    val info: InfoData,
    val servers: List<ServerData>,
    val externalDocs: ExternalDocsData,
    val specConfigs: MutableMap<String, PluginConfigData>,
    val postBuild: PostBuild?,
    val schemaConfig: SchemaConfigData,
    val exampleConfig: ExampleConfigData,
    val securityConfig: SecurityData,
    val tagsConfig: TagsData,
    val outputFormat: OutputFormat
) {

    companion object {
        val DEFAULT = PluginConfigData(
            specAssigner = { _, _ -> PluginConfigDsl.DEFAULT_SPEC_ID },
            pathFilter = { _, _ -> true },
            ignoredRouteSelectors = emptySet(),
            swagger = SwaggerUIData.DEFAULT,
            info = InfoData.DEFAULT,
            servers = emptyList(),
            externalDocs = ExternalDocsData.DEFAULT,
            specConfigs = mutableMapOf(),
            postBuild = null,
            schemaConfig = SchemaConfigData.DEFAULT,
            exampleConfig = ExampleConfigData.DEFAULT,
            securityConfig = SecurityData.DEFAULT,
            tagsConfig = TagsData.DEFAULT,
            outputFormat = OutputFormat.JSON
        )
    }

}
