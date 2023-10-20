package io.github.smiley4.ktorswaggerui.data

import io.github.smiley4.ktorswaggerui.dsl.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.OpenApiResponse
import kotlin.reflect.KClass

data class PluginConfigData(
    val defaultUnauthorizedResponse: OpenApiResponse?,
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
    val customSchemas: Map<String, BaseCustomSchema>,
    val includeAllCustomSchemas: Boolean,
    val encoding: EncodingData
) {

    companion object {
        val DEFAULT = PluginConfigData(
            defaultUnauthorizedResponse = null,
            defaultSecuritySchemeNames = emptySet(),
            tagGenerator = { emptyList() },
            specAssigner = { _, _ -> SwaggerUIPluginConfig.DEFAULT_SPEC_ID },
            pathFilter = { _, _ -> true },
            ignoredRouteSelectors = emptySet(),
            swaggerUI = SwaggerUIData.DEFAULT,
            info = InfoData.DEFAULT,
            servers = emptyList(),
            externalDocs = ExternalDocsData.DEFAULT,
            securitySchemes = emptyList(),
            tags = emptyList(),
            customSchemas = emptyMap(),
            includeAllCustomSchemas = false,
            encoding = EncodingData.DEFAULT
        )
    }

}