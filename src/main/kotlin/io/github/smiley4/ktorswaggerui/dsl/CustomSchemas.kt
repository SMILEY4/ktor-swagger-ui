package io.github.smiley4.ktorswaggerui.dsl

import io.swagger.v3.oas.models.media.Schema

@OpenApiDslMarker
class CustomSchemas {

    private val customSchemas = mutableMapOf<String, BaseCustomSchema>()

    fun getSchema(id: String): BaseCustomSchema? = customSchemas[id]


    /**
     * Define the json-schema for an object/body with the given id
     */
    fun json(id: String, provider: () -> String) {
        customSchemas[id] = CustomJsonSchema(provider)
    }


    /**
     * Define the [Schema] for an object/body with the given id
     */
    fun openApi(id: String, provider: () -> Schema<Any>) {
        customSchemas[id] = CustomOpenApiSchema(provider)
    }


    /**
     * Define the external url for an object/body with the given id
     */
    fun remote(id: String, url: String) {
        customSchemas[id] = RemoteSchema(url)
    }

}


sealed class BaseCustomSchema

class CustomJsonSchema(val provider: () -> String) : BaseCustomSchema()

class CustomOpenApiSchema(val provider: () -> Schema<Any>) : BaseCustomSchema()

class RemoteSchema(val url: String) : BaseCustomSchema()