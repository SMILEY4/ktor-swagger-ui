package io.github.smiley4.ktorswaggerui.documentation

import io.ktor.server.application.ApplicationCall
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingResolveContext
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.head
import io.ktor.server.routing.options
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.util.KtorDsl
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.pipeline.PipelineInterceptor

class DocumentedRouteSelector(val documentation: RouteDocumentation) : RouteSelector() {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int) = RouteSelectorEvaluation.Transparent
}


fun Route.documentation(
    documentation: RouteDocumentation.() -> Unit = { },
    build: Route.() -> Unit
): Route {
    val documentedRoute = createChild(DocumentedRouteSelector(RouteDocumentation().apply(documentation)))
    documentedRoute.build()
    return documentedRoute
}

//============================//
//             GET            //
//============================//

fun Route.get(
    path: String,
    builder: RouteDocumentation.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { get(path, body) }
}

fun Route.get(
    builder: RouteDocumentation.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { get(body) }
}

//============================//
//            POST            //
//============================//

fun Route.post(
    path: String,
    builder: RouteDocumentation.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { post(path, body) }
}


@JvmName("postTyped")
inline fun <reified R : Any> Route.post(
    noinline builder: RouteDocumentation.() -> Unit = { },
    crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(R) -> Unit
): Route {
    return documentation(builder) { post(body) }
}


@JvmName("postTypedPath")
inline fun <reified R : Any> Route.post(
    path: String,
    noinline builder: RouteDocumentation.() -> Unit = { },
    crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(R) -> Unit
): Route {
    return documentation(builder) { post(path, body) }
}


fun Route.post(
    builder: RouteDocumentation.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { post(body) }
}

//============================//
//             PUT            //
//============================//

fun Route.put(
    path: String,
    builder: RouteDocumentation.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { put(path, body) }
}

fun Route.put(
    builder: RouteDocumentation.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { put(body) }
}


@JvmName("putTyped")
inline fun <reified R : Any> Route.put(
    noinline builder: RouteDocumentation.() -> Unit = { },
    crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(R) -> Unit
): Route {
    return documentation(builder) { put(body) }
}


@JvmName("putTypedPath")
inline fun <reified R : Any> Route.put(
    path: String,
    noinline builder: RouteDocumentation.() -> Unit = { },
    crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(R) -> Unit
): Route {
    return documentation(builder) { put(path, body) }
}

//============================//
//           DELETE           //
//============================//

fun Route.delete(
    path: String,
    builder: RouteDocumentation.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { delete(path, body) }
}

fun Route.delete(
    builder: RouteDocumentation.() -> Unit = { },
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
    builder: RouteDocumentation.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { patch(path, body) }
}


@KtorDsl
fun Route.patch(
    builder: RouteDocumentation.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { patch(body) }
}


@JvmName("patchTyped")
inline fun <reified R : Any> Route.patch(
    noinline builder: RouteDocumentation.() -> Unit = { },
    crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(R) -> Unit
): Route {
    return documentation(builder) { patch(body) }

}


@JvmName("patchTypedPath")
inline fun <reified R : Any> Route.patch(
    path: String,
    noinline builder: RouteDocumentation.() -> Unit = { },
    crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(R) -> Unit
): Route {
    return documentation(builder) { patch(path, body) }
}

//============================//
//           OPTIONS          //
//============================//

fun Route.options(
    path: String,
    builder: RouteDocumentation.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { options(path, body) }
}

fun Route.options(
    builder: RouteDocumentation.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { options(body) }
}

//============================//
//            HEAD            //
//============================//

fun Route.head(
    path: String,
    builder: RouteDocumentation.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { head(path, body) }
}

fun Route.head(
    builder: RouteDocumentation.() -> Unit = { },
    body: PipelineInterceptor<Unit, ApplicationCall>
): Route {
    return documentation(builder) { head(body) }
}
