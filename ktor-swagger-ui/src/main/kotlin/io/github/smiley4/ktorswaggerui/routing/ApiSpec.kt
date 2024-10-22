package io.github.smiley4.ktorswaggerui.routing

import io.github.smiley4.ktorswaggerui.data.OutputFormat
import io.github.smiley4.ktorswaggerui.data.SwaggerUIData

object ApiSpec {

    var swaggerUiConfig: SwaggerUIData = SwaggerUIData.DEFAULT

    private val apiSpecs = mutableMapOf<String, Pair<String, OutputFormat>>()

    fun setAll(specs: Map<String, Pair<String, OutputFormat>>) {
        apiSpecs.clear()
        apiSpecs.putAll(specs)
    }

    fun set(name: String, spec: Pair<String, OutputFormat>) {
        apiSpecs[name] = spec
    }

    fun get(name: String): String {
        return apiSpecs[name]?.first ?: throw NoSuchElementException("No api-spec with name '$name' registered.")
    }

    fun getFormat(name: String): OutputFormat {
        return apiSpecs[name]?.second ?: throw NoSuchElementException("No api-spec with name '$name' registered.")
    }

    fun getAll(): Map<String, String> {
        return apiSpecs.mapValues { it.value.first }
    }

}
