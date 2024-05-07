package io.github.smiley4.ktorswaggerui.data

class ExampleConfigData(
    val examples: Map<String, ExampleDescriptor>
) {

    companion object {
        val DEFAULT = ExampleConfigData(
            examples = emptyMap()
        )
    }

}