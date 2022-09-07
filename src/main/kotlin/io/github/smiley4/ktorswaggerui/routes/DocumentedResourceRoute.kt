package io.github.smiley4.ktorswaggerui.routes

import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.ktor.server.application.ApplicationCall
import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.serializer
import org.checkerframework.checker.units.qual.K
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.superclasses
import kotlin.reflect.typeOf


inline fun <reified T : Any> Route.get(
    noinline builder: OpenApiRoute.() -> Unit = { },
    noinline body: suspend PipelineContext<Unit, ApplicationCall>.(T) -> Unit
): Route {
    val type = typeOf<T>()
    val serializer = serializer(type)

    println("=================================")
    println(type.classifier.toString() + "  " + serializer)
    println("=================================")

    val api = if ((type.classifier as KClass<*>).allSuperclasses.contains(OpenApiRouteDocumentation::class)) {
        ((type.classifier as KClass<*>).createInstance() as OpenApiRouteDocumentation).api
    } else {
        OpenApiRoute()
    }

    /**
     * TODO:
     * - check if improvements to documentation-merging is required
     * - if no explicit documentation is provided (e.g. for path parameters)
     *      -> use from route (might also be useful for non-"resources"-case)
     *             - ... either via reflection from class
     *             - ... or from route selectors (check if possible, i.e. enough info available)
     */

    return documentation(api) {
        documentation(builder) {
            get<T>(body)
        }
    }
}


private fun iterateReferencedClasses(type: K) {

}
