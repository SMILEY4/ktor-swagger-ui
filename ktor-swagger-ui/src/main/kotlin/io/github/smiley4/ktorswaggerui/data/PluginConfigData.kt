package io.github.smiley4.ktorswaggerui.data

import io.github.smiley4.ktorswaggerui.dsl.config.PluginConfigDsl
import kotlin.reflect.KClass

data class PluginConfigData(
    val defaultUnauthorizedResponse: OpenApiResponseData?,
    val defaultSecuritySchemeNames: Set<String>,
    val tagGenerator: TagGenerator,
    val specAssigner: SpecAssigner,
    val pathFilter: PathFilter,
    val ignoredRouteSelectors: Set<KClass<*>>,
    val swaggerUI: SwaggerUIData,
    val info: InfoData,
    val servers: List<ServerData>,
    val externalDocs: ExternalDocsData,
    val securitySchemes: List<SecuritySchemeData>,
    val tags: List<TagData>,
    val specConfigs: MutableMap<String, PluginConfigData>,
    val whenBuildOpenApiSpecs: WhenBuildOpenApiSpecs?
) {

    companion object {
        val DEFAULT = PluginConfigData(
            defaultUnauthorizedResponse = null,
            defaultSecuritySchemeNames = emptySet(),
            tagGenerator = { emptyList() },
            specAssigner = { _, _ -> PluginConfigDsl.DEFAULT_SPEC_ID },
            pathFilter = { _, _ -> true },
            ignoredRouteSelectors = emptySet(),
            swaggerUI = SwaggerUIData.DEFAULT,
            info = InfoData.DEFAULT,
            servers = emptyList(),
            externalDocs = ExternalDocsData.DEFAULT,
            securitySchemes = emptyList(),
            tags = emptyList(),
            specConfigs = mutableMapOf(),
            whenBuildOpenApiSpecs = null
        )
    }

}
