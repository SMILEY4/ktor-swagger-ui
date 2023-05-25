package io.github.smiley4.ktorswaggerui.dsl

/**
 * Annotation to add an example value to the field of an object.
 */
@Target(AnnotationTarget.FIELD)
annotation class Example(
    val value: String
)
