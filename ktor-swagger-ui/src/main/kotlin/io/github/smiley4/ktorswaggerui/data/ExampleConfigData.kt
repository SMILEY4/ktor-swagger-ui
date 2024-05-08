package io.github.smiley4.ktorswaggerui.data

class ExampleConfigData(
    val sharedExamples: Map<String, ExampleDescriptor>
) {

    companion object {
        val DEFAULT = ExampleConfigData(
            sharedExamples = emptyMap()
        )
    }

}