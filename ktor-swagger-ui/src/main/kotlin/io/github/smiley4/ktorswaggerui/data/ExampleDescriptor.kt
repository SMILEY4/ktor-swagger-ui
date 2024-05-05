package io.github.smiley4.ktorswaggerui.data

sealed class ExampleDescriptor(
    val name: String,
)

class ValueExampleDescriptor(
    name: String,
    val value: Any?,
    val summary: String?,
    val description: String?,
    val inComponents: Boolean?,
) : ExampleDescriptor(name)

class RefExampleDescriptor(name: String, val refName: String): ExampleDescriptor(name)