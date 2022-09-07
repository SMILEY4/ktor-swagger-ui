package io.github.smiley4.ktorswaggerui.routes

import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import kotlin.reflect.KClass


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Tag(
    val tag: String
)


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Description(
    val description: String
)


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class QueryParameter(
    val name: String,
    val schema: KClass<*>,
)


abstract class OpenApiRouteDocumentation(@Transient val api: OpenApiRoute) {

    constructor() : this(OpenApiRoute())

}

