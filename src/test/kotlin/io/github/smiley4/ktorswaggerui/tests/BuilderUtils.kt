package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.specbuilder.ApiSpecBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.JsonToOpenApiSchemaConverter
import io.github.smiley4.ktorswaggerui.specbuilder.OApiComponentsBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiContentBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiExampleBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiInfoBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiJsonSchemaBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiOAuthFlowsBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiParametersBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiPathBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiPathsBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiRequestBodyBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiResponsesBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiSchemaBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiSecuritySchemesBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiServersBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiTagsBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.RouteCollector

fun getApiSpecBuilder(): ApiSpecBuilder {
    return ApiSpecBuilder(
        OApiInfoBuilder(),
        OApiServersBuilder(),
        OApiTagsBuilder(),
        OApiPathsBuilder(
            RouteCollector(),
            OApiPathBuilder(
                OApiParametersBuilder(
                    OApiSchemaBuilder(
                        OApiJsonSchemaBuilder(
                            JsonToOpenApiSchemaConverter()
                        )
                    )
                ),
                OApiRequestBodyBuilder(
                    OApiContentBuilder(
                        OApiSchemaBuilder(
                            OApiJsonSchemaBuilder(
                                JsonToOpenApiSchemaConverter()
                            )
                        ),
                        OApiExampleBuilder()
                    )
                ),
                OApiResponsesBuilder(
                    OApiContentBuilder(
                        OApiSchemaBuilder(
                            OApiJsonSchemaBuilder(
                                JsonToOpenApiSchemaConverter()
                            )
                        ),
                        OApiExampleBuilder()
                    ),
                    OApiSchemaBuilder(
                        OApiJsonSchemaBuilder(
                            JsonToOpenApiSchemaConverter()
                        )
                    )
                ),
            ),
        ),
        OApiComponentsBuilder(
            OApiExampleBuilder(),
            OApiSecuritySchemesBuilder(
                OApiOAuthFlowsBuilder()
            ),
        ),
    )
}


fun getOApiInfoBuilder(): OApiInfoBuilder {
    return OApiInfoBuilder()
}

fun getOApiComponentsBuilder(): OApiComponentsBuilder {
    return OApiComponentsBuilder(
        OApiExampleBuilder(),
        OApiSecuritySchemesBuilder(
            OApiOAuthFlowsBuilder()
        )
    )
}


fun getOApiSchemaBuilder(): OApiSchemaBuilder {
    return OApiSchemaBuilder(
        OApiJsonSchemaBuilder(
            JsonToOpenApiSchemaConverter()
        )
    )
}

fun getOApiExampleBuilder(): OApiExampleBuilder {
    return OApiExampleBuilder()
}

fun getOApiContentBuilder(): OApiContentBuilder {
    return OApiContentBuilder(
        OApiSchemaBuilder(
            OApiJsonSchemaBuilder(
                JsonToOpenApiSchemaConverter()
            )
        ),
        OApiExampleBuilder()
    )
}

fun getOApiPathBuilder(): OApiPathBuilder {
    return OApiPathBuilder(
        OApiParametersBuilder(
            OApiSchemaBuilder(
                OApiJsonSchemaBuilder(
                    JsonToOpenApiSchemaConverter()
                )
            )
        ),
        OApiRequestBodyBuilder(
            OApiContentBuilder(
                OApiSchemaBuilder(
                    OApiJsonSchemaBuilder(
                        JsonToOpenApiSchemaConverter()
                    )
                ),
                OApiExampleBuilder()
            )
        ),
        OApiResponsesBuilder(
            OApiContentBuilder(
                OApiSchemaBuilder(
                    OApiJsonSchemaBuilder(
                        JsonToOpenApiSchemaConverter()
                    )
                ),
                OApiExampleBuilder()
            ),
            OApiSchemaBuilder(
                OApiJsonSchemaBuilder(
                    JsonToOpenApiSchemaConverter()
                )
            )
        ),
    )
}


fun getOApiPathsBuilder(routeCollector: RouteCollector): OApiPathsBuilder {
    return OApiPathsBuilder(
        routeCollector,
        OApiPathBuilder(
            OApiParametersBuilder(
                OApiSchemaBuilder(
                    OApiJsonSchemaBuilder(
                        JsonToOpenApiSchemaConverter()
                    )
                )
            ),
            OApiRequestBodyBuilder(
                OApiContentBuilder(
                    OApiSchemaBuilder(
                        OApiJsonSchemaBuilder(
                            JsonToOpenApiSchemaConverter()
                        )
                    ),
                    OApiExampleBuilder()
                )
            ),
            OApiResponsesBuilder(
                OApiContentBuilder(
                    OApiSchemaBuilder(
                        OApiJsonSchemaBuilder(
                            JsonToOpenApiSchemaConverter()
                        )
                    ),
                    OApiExampleBuilder()
                ),
                OApiSchemaBuilder(
                    OApiJsonSchemaBuilder(
                        JsonToOpenApiSchemaConverter()
                    )
                )
            ),
        ),
    )
}

fun getOApiSecuritySchemesBuilder(): OApiSecuritySchemesBuilder {
    return OApiSecuritySchemesBuilder(
        OApiOAuthFlowsBuilder()
    )
}

fun getOApiServersBuilder(): OApiServersBuilder {
    return OApiServersBuilder()
}

fun getOApiTagsBuilder(): OApiTagsBuilder {
    return OApiTagsBuilder()
}