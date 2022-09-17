package io.github.smiley4.ktorswaggerui.dsl

@OpenApiDslMarker
class CustomSchemas {

    private val externalSchemas = mutableMapOf<String, ExternalSchema>()

    fun custom(id: String, provider: () -> String) {
        externalSchemas[id] = CustomSchema(id, provider)
    }

    fun remote(id: String, url: String) {
        externalSchemas[id] = RemoteSchema(id, url)
    }

    fun get(id: String): ExternalSchema? = externalSchemas[id]

}


open class ExternalSchema(val id: String)

class CustomSchema(id: String, val provider: () -> String) : ExternalSchema(id)

class RemoteSchema(id: String, val url: String) : ExternalSchema(id)