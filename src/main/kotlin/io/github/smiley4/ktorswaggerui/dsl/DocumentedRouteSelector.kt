package io.github.smiley4.ktorswaggerui.dsl

import io.ktor.http.HttpMethod
import io.ktor.server.application.ApplicationCall
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingResolveContext
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.head
import io.ktor.server.routing.method
import io.ktor.server.routing.options
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.util.KtorDsl
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.pipeline.PipelineInterceptor

class DocumentedRouteSelector(val documentation: OpenApiRoute) : RouteSelector() {

    companion object {
        private var includeDocumentedRouteInRouteToString = false
        fun setIncludeDocumentedRouteInRouteToString(include: Boolean) {
            includeDocumentedRouteInRouteToString = include
        }
    }

    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int) = RouteSelectorEvaluation.Transparent

    override fun toString() = if (includeDocumentedRouteInRouteToString) super.toString() else ""
}

fun Route.documentation(
    documentation: OpenApiRoute.() -> Unit = { },
    build: Route.() -> Unit
): Route {
    val documentedRoute = createChild(DocumentedRouteSelector(OpenApiRoute().apply(documentation)))
    documentedRoute.build()
    return documentedRoute
}

//============================//
//           ROUTING          //
//============================//

fun Route.route(
    builder: OpenApiRoute.() -> Unit = { },
    build: Route.() -> Unit
): Route {
    return documentation(builder) { route("", build) }
}

fun Route.route(
    method: HttpMethod,
    builder: OpenApiRoute.() -> Unit = { },
    build: Route.() -> Unit
): Route {
    return documentation(builder) { route("", method, build) }
}

fun Route.route(
    path: String,
    builder: OpenApiRoute.() -> Unit = { },
    build: Route.() -> Unit
): Route {
    return documentation(builder) { route(path, build) }
}

fun Route.route(
    path: String,
    method: HttpMethod,
    builder: OpenApiRoute.() -> Unit = { },
    build: Route.() -> Unit
): Route {
    return documentation(builder) { route(path, method, build) }
}

fun Route.method(
    method: HttpMethod,
    builder: OpenApiRoute.() -> Unit = { },
    body: Route.() -> Unit
): Route {
    return documentation(builder) { method(method, body) }
}

//============================//
//             GET            //
//============================//

fun Route.get(
    path: String,
    builder: OpenApiRoute.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { get(path, body) }
}

fun Route.get(
    builder: OpenApiRoute.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { get(body) }
}


//============================//
//            POST            //
//============================//

fun Route.post(
    path: String,
    builder: OpenApiRoute.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { post(path, body) }
}

@JvmName("postTyped")
inline fun <reified R : Any> Route.post(
    noinline builder: OpenApiRoute.() -> Unit = { },
    crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(R) -> Unit
): Route {
    return documentation(builder) { post(body) }
}

@JvmName("postTypedPath")
inline fun <reified R : Any> Route.post(
    path: String,
    noinline builder: OpenApiRoute.() -> Unit = { },
    crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(R) -> Unit
): Route {
    return documentation(builder) { post(path, body) }
}


fun Route.post(
    builder: OpenApiRoute.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { post(body) }
}


//============================//
//             PUT            //
//============================//

fun Route.put(
    path: String,
    builder: OpenApiRoute.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { put(path, body) }
}

fun Route.put(
    builder: OpenApiRoute.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { put(body) }
}

@JvmName("putTyped")
inline fun <reified R : Any> Route.put(
    noinline builder: OpenApiRoute.() -> Unit = { },
    crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(R) -> Unit
): Route {
    return documentation(builder) { put(body) }
}

@JvmName("putTypedPath")
inline fun <reified R : Any> Route.put(
    path: String,
    noinline builder: OpenApiRoute.() -> Unit = { },
    crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(R) -> Unit
): Route {
    return documentation(builder) { put(path, body) }
}


//============================//
//           DELETE           //
//============================//

fun Route.delete(
    path: String,
    builder: OpenApiRoute.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { delete(path, body) }
}

fun Route.delete(
    builder: OpenApiRoute.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { delete(body) }
}


//============================//
//            PATCH           //
//============================//

@KtorDsl
fun Route.patch(
    path: String,
    builder: OpenApiRoute.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { patch(path, body) }
}

@KtorDsl
fun Route.patch(
    builder: OpenApiRoute.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { patch(body) }
}

@JvmName("patchTyped")
inline fun <reified R : Any> Route.patch(
    noinline builder: OpenApiRoute.() -> Unit = { },
    crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(R) -> Unit
): Route {
    return documentation(builder) { patch(body) }

}

@JvmName("patchTypedPath")
inline fun <reified R : Any> Route.patch(
    path: String,
    noinline builder: OpenApiRoute.() -> Unit = { },
    crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(R) -> Unit
): Route {
    return documentation(builder) { patch(path, body) }
}


//============================//
//           OPTIONS          //
//============================//

fun Route.options(
    path: String,
    builder: OpenApiRoute.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { options(path, body) }
}

fun Route.options(
    builder: OpenApiRoute.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { options(body) }
}


//============================//
//            HEAD            //
//============================//

fun Route.head(
    path: String,
    builder: OpenApiRoute.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { head(path, body) }
}

fun Route.head(
    builder: OpenApiRoute.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { head(body) }
}
