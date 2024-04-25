package io.github.smiley4.ktorswaggerui.routing

object ApiSpec {

    private val apiSpecs = mutableMapOf<String, String>()

    fun setAll(specs: Map<String, String>) {
        apiSpecs.clear()
        apiSpecs.putAll(specs)
    }

    fun set(name: String, spec: String) {
        apiSpecs[name] = spec
    }

    fun get(name: String): String {
        return apiSpecs[name] ?: throw NoSuchElementException("No api-spec with name $name registered.")
    }

    fun getAll(): Map<String, String> {
        return apiSpecs
    }

}
