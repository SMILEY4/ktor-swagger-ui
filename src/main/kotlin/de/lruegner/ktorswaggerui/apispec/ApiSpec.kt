package de.lruegner.ktorswaggerui.apispec

import de.lruegner.ktorswaggerui.SwaggerUIPluginConfig
import de.lruegner.ktorswaggerui.apispec.ParameterDocumentation.Companion.ParamType.PATH
import de.lruegner.ktorswaggerui.apispec.ParameterDocumentation.Companion.ParamType.QUERY
import de.lruegner.ktorswaggerui.apispec.ParameterDocumentation.Companion.ParameterDataType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.swagger.v3.core.util.Json
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import java.lang.reflect.Type


class ResponseDocumentation(
    val statusCode: HttpStatusCode,
    val descriptions: MutableList<String>,
    val body: Type? = null
)

class ParameterDocumentation(
    val type: ParamType,
    val name: String,
    var description: String? = null,
    var dataType: ParameterDataType = ParameterDataType.STRING
) {
    companion object {
        enum class ParamType {
            PATH, QUERY
        }

        enum class ParameterDataType {
            STRING, INT, NUMBER, BOOL, OBJECT,
            STRING_ARRAY, INT_ARRAY, NUMBER_ARRAY, BOOL_ARRAY, OBJECT_ARRAY
        }
    }
}

sealed class BodyDocumentation(val description: String? = null)

class JsonBodyDocumentation(
    description: String? = null,
    val schema: Type,
) : BodyDocumentation(description)

class PlainTextBodyDocumentation(description: String? = null) : BodyDocumentation(description)



object ApiSpec {

    var jsonSpec: String = ""

    fun build(application: Application, config: SwaggerUIPluginConfig) {
        val openAPI = OpenAPI().apply {
            info = OApiInfoGenerator().generate(config.getInfo())
            servers = OApiServersGenerator().generate(config.getServers())
            if (config.getSecuritySchemes().isNotEmpty()) {
                components = Components().apply {
                    securitySchemes = OApiSecuritySchemesGenerator().generate(config.getSecuritySchemes())
                }
            }
            tags = OApiTagsGenerator().generate(config.getTags())
            paths = OApiPathsGenerator().generate(config, application)
        }
        jsonSpec = Json.pretty(openAPI)
    }

}
