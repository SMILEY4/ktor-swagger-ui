package io.github.smiley4.ktorswaggerui.builder.route

import io.github.smiley4.ktorswaggerui.dsl.routes.OpenApiRoute

class RouteDocumentationMerger {

    /**
     * Merges "a" with "b" and returns the result as a new [OpenApiRoute]. "a" has priority over "b".
     */
    fun merge(a: OpenApiRoute, b: OpenApiRoute): OpenApiRoute {
        return OpenApiRoute().apply {
            specId = a.specId ?: b.specId
            tags = mutableListOf<String>().also {
                it.addAll(a.tags)
                it.addAll(b.tags)
            }
            summary = a.summary ?: b.summary
            description = a.description ?: b.description
            operationId = a.operationId ?: b.operationId
            securitySchemeNames = mutableSetOf<String>().also { merged ->
                a.securitySchemeNames?.let { merged.addAll(it) }
                b.securitySchemeNames?.let { merged.addAll(it) }
            }
            deprecated = a.deprecated || b.deprecated
            hidden = a.hidden || b.hidden
            protected = a.protected ?: b.protected
            request {
                (getParameters() as MutableList).also {
                    it.addAll(a.getRequest().getParameters())
                    it.addAll(b.getRequest().getParameters())
                }
                setBody(a.getRequest().getBody() ?: b.getRequest().getBody())
            }
            response {
                b.getResponses().getResponses().forEach { response -> addResponse(response) }
                a.getResponses().getResponses().forEach { response -> addResponse(response) }
            }
        }
    }

}
