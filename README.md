# Ktor Swagger-UI

[![Release](https://jitpack.io/v/SMILEY4/ktor-swagger-ui.svg)]
(https://jitpack.io/#SMILEY4/ktor-swagger-ui)

This library provides Ktor plugin to document routes and enable Swagger UI. It is meant to be  minimally invasive, meaning it can be plugged into existing ktor application without requiring direct changes to the code. Routes can then be gradually enhanced with documentation.

## Installation

Step 1. Add the JitPack repository

```kotlin
   repositories {
        ...
        maven { url "https://jitpack.io" }
   }
```

Step 2. Add the dependency

```kotlin
dependencies {
    implementation 'io.github.smiley4:ktor-swagger-ui:<VERSION>'
}
```


## Example

```kotlin
install(SwaggerUI) {
    swagger {
        swaggerUrl = "swagger-ui"
        forwardRoot = true
    }
    info {
        title = "Example API"
        version = "latest"
        description = "Example API for testing and demonstration purposes."
    }
    server {
        url = "http://localhost:8080"
        description = "Development Server"
    }
}
```

```kotlin
get("hello", {
    description = "Hello World Endpoint."
    response(HttpStatusCode.OK) {
        description = "Successful Request"
        textBody { description = "the response" }
    }
    response(HttpStatusCode.InternalServerError) {
        description = "Something unexpected happened"
    }
}) {
    call.respondText("Hello World!")
}
```

```kotlin
post("math/{operation}", {
    description = "Performs the given operation on the given values and returns the result"
    pathParameter {
        name = "operation"
        description = "the math operation to perform. Either 'add' or 'sub'"
        schema(RouteParameter.Type.STRING)
    }
    typedRequestBody(MathRequest::class.java) {}
    response(HttpStatusCode.OK) {
        description = "The operation was successful"
        typedBody(MathResult::class.java) {
            description = "The result of the operation"
        }
    }
    response(HttpStatusCode.BadRequest) {
        description = "An invalid operation was provided"
    }
}) {
    val operation = call.parameters["operation"]!!
    call.receive<MathRequest>().let { request ->
        when (operation) {
            "add" -> call.respond(HttpStatusCode.OK, MathResult(request.a + request.b))
            "sub" -> call.respond(HttpStatusCode.OK, MathResult(request.a - request.b))
            else -> call.respond(HttpStatusCode.BadRequest, Unit)
        }
    }
}


data class MathRequest(
    val a: Int,
    val b: Int
)

data class MathResult(
    val value: Int
)
```

