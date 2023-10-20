package io.github.smiley4.ktorswaggerui.data

import io.swagger.v3.oas.models.media.Schema

sealed class BaseCustomSchema

class CustomJsonSchema(val provider: () -> String) : BaseCustomSchema()

class CustomOpenApiSchema(val provider: () -> Schema<Any>) : BaseCustomSchema()

class RemoteSchema(val url: String) : BaseCustomSchema()
