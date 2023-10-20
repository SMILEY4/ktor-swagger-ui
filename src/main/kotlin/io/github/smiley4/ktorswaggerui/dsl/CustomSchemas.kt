package io.github.smiley4.ktorswaggerui.dsl

import io.github.smiley4.ktorswaggerui.data.BaseCustomSchema
import io.github.smiley4.ktorswaggerui.data.CustomJsonSchema
import io.github.smiley4.ktorswaggerui.data.CustomOpenApiSchema
import io.github.smiley4.ktorswaggerui.data.RemoteSchema
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


