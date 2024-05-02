package io.github.smiley4.ktorswaggerui.data

import io.ktor.http.ContentType

data class OpenApiMultipartPartData(
    val name: String,
    val type: TypeDescriptor,
    val mediaTypes: Set<ContentType>,
    val headers: Map<String, OpenApiHeaderData>,
)