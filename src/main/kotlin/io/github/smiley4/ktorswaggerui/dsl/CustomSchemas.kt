package io.github.smiley4.ktorswaggerui.dsl

import io.swagger.v3.oas.models.media.Schema

@OpenApiDslMarker
class CustomSchemas {

    private val externalSchemas = mutableMapOf<String, BaseCustomSchema>()

    fun customJson(id: String, provider: () -> String) {
        externalSchemas[id] = CustomJsonSchema(provider)
    }

    fun customOpenApi(id: String, provider: () -> Schema<Any>) {
        externalSchemas[id] = CustomOpenApiSchema(provider)
    }

    fun remote(id: String, url: String) {
        externalSchemas[id] = RemoteSchema(url)
    }

    fun get(id: String): BaseCustomSchema? = externalSchemas[id]

}


sealed class BaseCustomSchema

class CustomJsonSchema(val provider: () -> String) : BaseCustomSchema()

class CustomOpenApiSchema(val provider: () -> Schema<Any>) : BaseCustomSchema()

class RemoteSchema(val url: String) : BaseCustomSchema()