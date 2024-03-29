package io.github.smiley4.ktorswaggerui.dsl

import io.github.smiley4.ktorswaggerui.data.*
import io.github.smiley4.ktorswaggerui.data.DataUtils.merge
import io.github.smiley4.ktorswaggerui.data.DataUtils.mergeBoolean
import io.ktor.http.*
import io.ktor.server.routing.*
import kotlin.collections.Collection
import kotlin.collections.Set
import kotlin.collections.buildList
import kotlin.collections.buildMap
import kotlin.collections.buildSet
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach
import kotlin.collections.map
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.reflect.KClass

/**
 * Main-Configuration of the "SwaggerUI"-Plugin
 */
@OpenApiDslMarker
class PluginConfigDsl {

    companion object {
        const val DEFAULT_SPEC_ID = "api"
    }


    private val specConfigs = mutableMapOf<String, PluginConfigDsl>()

    fun spec(specId: String, block: PluginConfigDsl.() -> Unit) {
        specConfigs[specId] = PluginConfigDsl().apply(block)
    }


    /**
     * Default response to automatically add to each protected route for the "Unauthorized"-Response-Code.
     * Generated response can be overwritten with custom response.
     */
    fun defaultUnauthorizedResponse(block: OpenApiResponse.() -> Unit) {
        defaultUnauthorizedResponse = OpenApiResponse(HttpStatusCode.Unauthorized.value.toString()).apply(block)
    }

    private var defaultUnauthorizedResponse: OpenApiResponse? = PluginConfigData.DEFAULT.defaultUnauthorizedResponse


    /**
     * The name of the security scheme to use for the protected paths
     */
    var defaultSecuritySchemeName: String? = null


    /**
     * The names of the security schemes available for use for the protected paths
     */
    var defaultSecuritySchemeNames: Collection<String>? = PluginConfigData.DEFAULT.defaultSecuritySchemeNames


    /**
     * Automatically add tags to the route with the given url.
     * The returned (non-null) tags will be added to the tags specified in the route-specific documentation.
     */
    fun generateTags(generator: TagGenerator) {
        tagGenerator = generator
    }

    private var tagGenerator: TagGenerator? = PluginConfigData.DEFAULT.tagGenerator


    /**
     * Assigns routes without an [io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute.specId] to a specified openapi-spec.
     */
    var specAssigner: SpecAssigner? = PluginConfigData.DEFAULT.specAssigner


    /**
     * Filter to apply to all routes. Return 'false' for routes to not include them in the OpenApi-Spec and Swagger-UI.
     * The url of the paths are already split at '/'.
     */
    var pathFilter: PathFilter? = PluginConfigData.DEFAULT.pathFilter


    /**
     * Swagger-UI configuration
     */
    fun swagger(block: SwaggerUIDsl.() -> Unit) {
        swaggerUI = SwaggerUIDsl().apply(block)
    }

    private var swaggerUI = SwaggerUIDsl()


    /**
     * OpenAPI info configuration - provides metadata about the API
     */
    fun info(block: OpenApiInfo.() -> Unit) {
        info = OpenApiInfo().apply(block)
    }

    private var info = OpenApiInfo()


    /**
     * OpenAPI server configuration - an array of servers, which provide connectivity information to a target server
     */
    fun server(block: OpenApiServer.() -> Unit) {
        servers.add(OpenApiServer().apply(block))
    }

    private val servers = mutableListOf<OpenApiServer>()


    /**
     * OpenAPI external docs configuration - link and description of an external documentation
     */
    fun externalDocs(block: OpenApiExternalDocs.() -> Unit) {
        externalDocs = OpenApiExternalDocs().apply(block)
    }

    private var externalDocs = OpenApiExternalDocs()


    /**
     * Defines security schemes that can be used by operations
     */
    fun securityScheme(name: String, block: OpenApiSecurityScheme.() -> Unit) {
        securitySchemes.add(OpenApiSecurityScheme(name).apply(block))
    }

    private val securitySchemes = mutableListOf<OpenApiSecurityScheme>()


    /**
     * Tags used by the specification with additional metadata. Not all tags that are used must be declared
     */
    fun tag(name: String, block: OpenApiTag.() -> Unit) {
        tags.add(OpenApiTag(name).apply(block))
    }

    private val tags = mutableListOf<OpenApiTag>()


    /**
     * Custom schemas to reference via [io.github.smiley4.ktorswaggerui.dsl.CustomSchemaRef]
     */
    fun customSchemas(block: CustomSchemas.() -> Unit) {
        this.customSchemas = CustomSchemas().apply(block)
    }

    private var customSchemas = CustomSchemas()


    /**
     * customize the behaviour of different encoders (examples, schemas, ...)
     */
    fun encoding(block: EncodingConfig.() -> Unit) {
        block(encodingConfig)
    }

    val encodingConfig: EncodingConfig = EncodingConfig()


    /**
     * List of all [RouteSelector] types in that should be ignored in the resulting url of any route.
     */
    var ignoredRouteSelectors: Set<KClass<*>> = PluginConfigData.DEFAULT.ignoredRouteSelectors


    /**
     * Invoked after generating the openapi-spec. Can be to e.g. further customize the spec.
     */
    var whenBuildOpenApiSpecs: WhenBuildOpenApiSpecs? = null


    internal fun build(base: PluginConfigData): PluginConfigData {
        return PluginConfigData(
            defaultUnauthorizedResponse = merge(base.defaultUnauthorizedResponse, defaultUnauthorizedResponse),
            defaultSecuritySchemeNames = buildSet {
                addAll(base.defaultSecuritySchemeNames)
                defaultSecuritySchemeNames?.also { addAll(it) }
                defaultSecuritySchemeName?.also { add(it) }
            },
            tagGenerator = merge(base.tagGenerator, tagGenerator) ?: PluginConfigData.DEFAULT.tagGenerator,
            specAssigner = merge(base.specAssigner, specAssigner) ?: PluginConfigData.DEFAULT.specAssigner,
            pathFilter = merge(base.pathFilter, pathFilter) ?: PluginConfigData.DEFAULT.pathFilter,
            ignoredRouteSelectors = buildSet {
                addAll(base.ignoredRouteSelectors)
                addAll(ignoredRouteSelectors)
            },
            swaggerUI = swaggerUI.build(base.swaggerUI),
            info = info.build(base.info),
            servers = buildList {
                addAll(base.servers)
                addAll(servers.map { it.build(ServerData.DEFAULT) })
            },
            externalDocs = externalDocs.build(base.externalDocs),
            securitySchemes = buildList {
                addAll(base.securitySchemes)
                addAll(securitySchemes.map { it.build(SecuritySchemeData.DEFAULT) })
            },
            tags = buildList {
                addAll(base.tags)
                addAll(tags.map { it.build(TagData.DEFAULT) })
            },
            customSchemas = buildMap {
                putAll(base.customSchemas)
                putAll(customSchemas.getSchemas())
            },
            includeAllCustomSchemas = mergeBoolean(base.includeAllCustomSchemas, customSchemas.includeAll),
            encoding = encodingConfig.build(base.encoding),
            specConfigs = mutableMapOf(),
            whenBuildOpenApiSpecs = whenBuildOpenApiSpecs,
        ).also {
            specConfigs.forEach { (specId, config) ->
                it.specConfigs[specId] = config.build(it)
            }
        }
    }
}
