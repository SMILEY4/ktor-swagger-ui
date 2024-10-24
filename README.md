# Ktor Swagger-UI

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.smiley4/ktor-swagger-ui/badge.svg)](https://search.maven.org/artifact/io.github.smiley4/ktor-swagger-ui)
[![Checks Passing](https://github.com/SMILEY4/ktor-swagger-ui/actions/workflows/checks.yml/badge.svg?branch=develop)](https://github.com/SMILEY4/ktor-swagger-ui/actions/workflows/checks.yml)

This library provides a Ktor plugin to document routes, generate an OpenApi Specification and serve a Swagger UI. It is meant to be  minimally invasive, meaning it can be plugged into existing application without requiring immediate changes to the code. Routes can then be gradually enhanced with documentation.


## Features

- minimally invasive (no immediate change to existing code required)
- provides swagger-ui and openapi-spec with minimal configuration
- supports most of the [OpenAPI 3.1.0 Specification](https://swagger.io/specification/)
- automatic [json-schema generation](https://github.com/SMILEY4/schema-kenerator) from arbitrary types/classes for bodies and parameters
  - supports generics, inheritance, collections, ... 
  - support for Jackson-annotations and swagger Schema-annotations (optional) 
  - use with reflection or kotlinx-serialization
  - customizable schema-generation


## Documentation

A wiki with a short documentation is available [here](https://github.com/SMILEY4/ktor-swagger-ui/wiki).


## Installation

```kotlin
dependencies {
    implementation "io.github.smiley4:ktor-swagger-ui:<VERSION>"
}
```


## Ktor compatibility

- Ktor 2.x: ktor-swagger-ui up to 3.x
- Ktor 3.x: ktor-swagger-ui starting with 4.0


## Examples

Runnable examples can be found in [ktor-swagger-ui-examples/src/main/kotlin/io/github/smiley4/ktorswaggerui/examples](https://github.com/SMILEY4/ktor-swagger-ui/tree/release/ktor-swagger-ui-examples/src/main/kotlin/io/github/smiley4/ktorswaggerui/examples).


### Configuration

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
routing {
    // Create a route for the openapi-spec file.
    route("api.json") {
      openApiSpec()
    }
    // Create a route for the swagger-ui using the openapi-spec at "/api.json".
    route("swagger") {
      swaggerUI("/api.json")
    }
}
```

### Routes

```kotlin
get("hello", {
    description = "Hello World Endpoint."
    response {
        HttpStatusCode.OK to {
            description = "Successful Request"
            body<String> { description = "the response" }
        }
        HttpStatusCode.InternalServerError to {
            description = "Something unexpected happened"
        }
    }
}) {
    call.respondText("Hello World!")
}
```

```kotlin
post("math/{operation}", {
    tags = listOf("test")
    description = "Performs the given operation on the given values and returns the result"
    request {
        pathParameter<String>("operation") {
            description = "the math operation to perform. Either 'add' or 'sub'"
        }
        body<MathRequest>()
    }
    response {
        HttpStatusCode.OK to {
            description = "The operation was successful"
            body<MathResult> {
                description = "The result of the operation"
            }
        }
        HttpStatusCode.BadRequest to {
            description = "An invalid operation was provided"
        }
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

