package io.github.smiley4.ktorswaggerui.data

/**
 * Encoder to produce the final example value.
 * Return the unmodified example to fall back to the default encoder.
 */
typealias ExampleEncoder = (type: TypeDescriptor?, example: Any?) -> Any?

class ExampleConfigData(
    val sharedExamples: Map<String, ExampleDescriptor>,
    val securityExamples: List<ExampleDescriptor>,
    val exampleEncoder: ExampleEncoder?
) {

    companion object {
        val DEFAULT = ExampleConfigData(
            sharedExamples = emptyMap(),
            securityExamples = emptyList(),
            exampleEncoder = null
        )
    }

}
