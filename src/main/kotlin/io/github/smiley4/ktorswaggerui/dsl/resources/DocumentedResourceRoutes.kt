package io.github.smiley4.ktorswaggerui.dsl.resources

import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.github.smiley4.ktorswaggerui.dsl.documentation
import io.github.smiley4.ktorswaggerui.dsl.method
import io.ktor.http.HttpMethod
import io.ktor.server.application.ApplicationCall
import io.ktor.server.resources.handle
import io.ktor.server.resources.resource
import io.ktor.server.routing.Route
import io.ktor.util.pipeline.PipelineContext

//============================//
//             GET            //
//============================//

inline fun <reified T : Any> Route.get(
    noinline builder: OpenApiRoute.() -> Unit = { },
    noinline body: suspend PipelineContext<Unit, ApplicationCall>.(T) -> Unit
): Route {
    return documentation(builder) {
        resource<T> {
            method(HttpMethod.Get) {
                handle(body)
            }
        }
    }
}

//============================//
//            POST            //
//============================//

inline fun <reified T : Any> Route.post(
    noinline builder: OpenApiRoute.() -> Unit = { },
    noinline body: suspend PipelineContext<Unit, ApplicationCall>.(T) -> Unit
): Route {
    return documentation(builder) {
        resource<T> {
            method(HttpMethod.Post) {
                handle(body)
            }
        }
    }
}

//============================//
//             PUT            //
//============================//

inline fun <reified T : Any> Route.put(
    noinline builder: OpenApiRoute.() -> Unit = { },
    noinline body: suspend PipelineContext<Unit, ApplicationCall>.(T) -> Unit
): Route {
    return documentation(builder) {
        resource<T> {
            method(HttpMethod.Put) {
                handle(body)
            }
        }
    }
}

//============================//
//           DELETE           //
//============================//

inline fun <reified T : Any> Route.delete(
    noinline builder: OpenApiRoute.() -> Unit = { },
    noinline body: suspend PipelineContext<Unit, ApplicationCall>.(T) -> Unit
): Route {
    return documentation(builder) {
        resource<T> {
            method(HttpMethod.Delete) {
                handle(body)
            }
        }
    }
}

//============================//
//            PATCH           //
//============================//

inline fun <reified T : Any> Route.patch(
    noinline builder: OpenApiRoute.() -> Unit = { },
    noinline body: suspend PipelineContext<Unit, ApplicationCall>.(T) -> Unit
): Route {
    return documentation(builder) {
        resource<T> {
            method(HttpMethod.Patch) {
                handle(body)
            }
        }
    }
}

//============================//
//           OPTIONS          //
//============================//

inline fun <reified T : Any> Route.options(
    noinline builder: OpenApiRoute.() -> Unit = { },
    noinline body: suspend PipelineContext<Unit, ApplicationCall>.(T) -> Unit
): Route {
    return documentation(builder) {
        resource<T> {
            method(HttpMethod.Options) {
                handle(body)
            }
        }
    }
}

//============================//
//            HEAD            //
//============================//

inline fun <reified T : Any> Route.head(
    noinline builder: OpenApiRoute.() -> Unit = { },
    noinline body: suspend PipelineContext<Unit, ApplicationCall>.(T) -> Unit
): Route {
    return documentation(builder) {
        resource<T> {
            method(HttpMethod.Head) {
                handle(body)
            }
        }
    }
}
