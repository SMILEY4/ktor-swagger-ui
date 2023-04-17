package io.github.smiley4.ktorswaggerui.spec.openapi

import io.github.smiley4.ktorswaggerui.dsl.OpenApiBaseBody
import io.github.smiley4.ktorswaggerui.dsl.OpenApiMultipartBody
import io.github.smiley4.ktorswaggerui.dsl.OpenApiSimpleBody
import io.swagger.v3.oas.models.media.Content

class ContentBuilder {

    fun build(body: OpenApiBaseBody): Content =
        when (body) {
            is OpenApiSimpleBody -> buildSimpleBody(body)
            is OpenApiMultipartBody -> buildMultipartBody(body)
        }

    private fun buildSimpleBody(body: OpenApiSimpleBody): Content =
        Content().also {
            // TODO
        }

    private fun buildMultipartBody(body: OpenApiMultipartBody): Content =
        Content().also {
            // TODO
        }

}