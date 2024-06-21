package io.github.smiley4.ktorswaggerui.data

class ExampleConfigData(
    val sharedExamples: Map<String, ExampleDescriptor>,
    val securityExamples: List<ExampleDescriptor>
) {

    companion object {
        val DEFAULT = ExampleConfigData(
            sharedExamples = emptyMap(),
            securityExamples = emptyList()
        )
    }

}
