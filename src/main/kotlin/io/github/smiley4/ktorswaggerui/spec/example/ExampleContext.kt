package io.github.smiley4.ktorswaggerui.spec.example

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRequestParameter
import io.github.smiley4.ktorswaggerui.dsl.OpenApiSimpleBody
import io.github.smiley4.ktorswaggerui.dsl.getTypeName
import io.swagger.v3.oas.models.examples.Example
import kotlin.reflect.KClass

class ExampleContext(
    private val config: SwaggerUIPluginConfig
) {

    data class ExampleEntry(
        val name: String,
        val example: Example,
        val body: OpenApiSimpleBody,
    )

    class ExamplesByName(
        val entries: MutableMap<String, ExamplesByType>
    )

    class ExamplesByType(
        val entries: MutableMap<String, ExamplesByContent>
    )

    class ExamplesByContent(
        val entries: MutableMap<String, MutableList<ExampleEntry>>
    )


    private val examplesParameters = mutableMapOf<OpenApiRequestParameter, String>()
    private val examplesBody = mutableMapOf<Pair<OpenApiSimpleBody, String>, Example>()

    private val componentsSection = mutableMapOf<String, Example>()
    private val inlineExamples = mutableMapOf<Pair<OpenApiSimpleBody, String>, Example>()


    fun addExample(parameter: OpenApiRequestParameter, value: String) {
        examplesParameters[parameter] = value
    }

    fun addExample(body: OpenApiSimpleBody, name: String, value: Example) {
        examplesBody[body to name] = value
    }

    fun getComponentsSection(): Map<String, Example> = componentsSection

    fun getExample(parameter: OpenApiRequestParameter): String? = examplesParameters[parameter]

    fun getExample(body: OpenApiSimpleBody, name: String): Example? = inlineExamples[body to name]

    fun finalize() {
        examplesBody
            .mapNotNull { (key, example) ->
                if (shouldInline(key.first)) {
                    inlineExamples[key.first to key.second] = example
                    null
                } else {
                    ExampleEntry(
                        name = key.second,
                        body = key.first,
                        example = example
                    )
                }
            }
            .let { groupExamples(it) }
            .let { generateUniqueNames(it) }
            .forEach { (name, entry) ->
                inlineExamples[entry.body to entry.name] = Example().also {
                    it.`$ref` = "#/components/examples/$name"
                }
                componentsSection[name] = entry.example
            }
    }

    private fun shouldInline(body: OpenApiSimpleBody): Boolean {
        if (config.inlineExamples) {
            return true
        }
        return body.type?.classifier?.let {
            setOf<KClass<*>>(
                Byte::class,
                UByte::class,
                Short::class,
                UShort::class,
                Int::class,
                UInt::class,
                Long::class,
                ULong::class,
                Float::class,
                Double::class,
                Boolean::class,
                String::class
            ).contains(it)
        } ?: true
    }

    private fun groupExamples(entries: List<ExampleEntry>): ExamplesByName {
        val byName = ExamplesByName(mutableMapOf())
        entries.forEach { entry ->
            if (!byName.entries.containsKey(entry.name)) {
                byName.entries[entry.name] = ExamplesByType(mutableMapOf())
            }
            val byType = byName.entries[entry.name]!!

            val typeName = entry.body.type?.getTypeName() ?: "null"
            if (!byType.entries.containsKey(typeName)) {
                byType.entries[typeName] = ExamplesByContent(mutableMapOf())
            }
            val byContent = byType.entries[typeName]!!

            val strContent = entry.example.value.toString() + entry.example.summary + entry.example.description
            if (!byContent.entries.containsKey(strContent)) {
                byContent.entries[strContent] = mutableListOf()
            }
            val byContentEntries = byContent.entries[strContent]!!
            byContentEntries.add(entry)
        }
        return byName
    }


    private fun generateUniqueNames(groupedEntries: ExamplesByName): List<Pair<String, ExampleEntry>> {
        return mutableListOf<Pair<String, ExampleEntry>>().also { out ->
            groupedEntries.entries.forEach { (name, entriesByName) ->
                entriesByName.entries.forEach { (type, entriesByType) ->
                    entriesByType.entries.forEachIndexed { index, _, entriesByContent ->
                        entriesByContent.first { entry ->
                            val appendType = entriesByName.entries.size > 1
                            val appendIndex = entriesByType.entries.size > 1
                            var componentName = name
                            if (appendType) {
                                componentName += "#$type"
                            }
                            if (appendIndex) {
                                componentName += "#$index"
                            }
                            out.add(componentName to entry)
                        }
                    }
                }
            }
        }
    }


    private inline fun <K, V> Map<out K, V>.forEachIndexed(action: (index: Int, key: K, value: V) -> Unit) {
        this.entries.forEachIndexed { index, entry ->
            action(index, entry.key, entry.value)
        }
    }

}