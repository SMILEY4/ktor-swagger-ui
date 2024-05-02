package io.github.smiley4.ktorswaggerui.data

import io.ktor.http.ContentType

sealed class OpenApiBaseBodyData(
    val description: String?,
    val required: Boolean,
    val mediaTypes: Set<ContentType>,
)

class OpenApiSimpleBodyData(
    description: String?,
    required: Boolean,
    mediaTypes: Set<ContentType>,
    val type: TypeDescriptor,
    val examples: Map<String, OpenApiExampleData>
) : OpenApiBaseBodyData(description, required, mediaTypes)

class OpenApiMultipartBodyData(
    description: String?,
    required: Boolean,
    mediaTypes: Set<ContentType>,
    val parts: List<OpenApiMultipartPartData>
) : OpenApiBaseBodyData(description, required, mediaTypes)
