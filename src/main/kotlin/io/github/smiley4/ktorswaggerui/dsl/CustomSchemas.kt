package io.github.smiley4.ktorswaggerui.dsl

import io.swagger.v3.oas.models.media.Schema

@OpenApiDslMarker
class CustomSchemas {

    private val schemas = mutableMapOf<String, BaseCustomSchema>()

    fun getSchema(id: String): BaseCustomSchema? = schemas[id]

    fun getSchemas() = schemas

    /**
     * Define the json-schema for an object/body with the given id
     */
    fun json(id: String, provider: () -> String) {
        schemas[id] = CustomJsonSchema(provider)
    }


    /**
     * Define the [Schema] for an object/body with the given id
     */
    fun openApi(id: String, provider: () -> Schema<Any>) {
        schemas[id] = CustomOpenApiSchema(provider)
    }


    /**
     * Define the external url for an object/body with the given id
     */
    fun remote(id: String, url: String) {
        schemas[id] = RemoteSchema(url)
    }


    /**
     * Whether to include all custom-schemas or only the ones directly used in any route-documentation
     */
    var includeAll = false

}


sealed class BaseCustomSchema

class CustomJsonSchema(val provider: () -> String) : BaseCustomSchema()

class CustomOpenApiSchema(val provider: () -> Schema<Any>) : BaseCustomSchema()

class RemoteSchema(val url: String) : BaseCustomSchema()
