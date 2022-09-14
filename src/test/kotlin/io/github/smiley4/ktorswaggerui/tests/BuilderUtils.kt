package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.specbuilder.ApiSpecBuilder
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
                        OApiJsonSchemaBuilder()
                    )
                ),
                OApiRequestBodyBuilder(
                    OApiContentBuilder(
                        OApiSchemaBuilder(
                            OApiJsonSchemaBuilder()
                        ),
                        OApiExampleBuilder()
                    )
                ),
                OApiResponsesBuilder(
                    OApiContentBuilder(
                        OApiSchemaBuilder(
                            OApiJsonSchemaBuilder()
                        ),
                        OApiExampleBuilder()
                    ),
                    OApiSchemaBuilder(
                        OApiJsonSchemaBuilder()
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
        OApiJsonSchemaBuilder()
    )
}

fun getOApiExampleBuilder(): OApiExampleBuilder {
    return OApiExampleBuilder()
}

fun getOApiContentBuilder(): OApiContentBuilder {
    return OApiContentBuilder(
        OApiSchemaBuilder(
            OApiJsonSchemaBuilder()
        ),
        OApiExampleBuilder()
    )
}

fun getOApiPathBuilder(): OApiPathBuilder {
    return OApiPathBuilder(
        OApiParametersBuilder(
            OApiSchemaBuilder(
                OApiJsonSchemaBuilder()
            )
        ),
        OApiRequestBodyBuilder(
            OApiContentBuilder(
                OApiSchemaBuilder(
                    OApiJsonSchemaBuilder()
                ),
                OApiExampleBuilder()
            )
        ),
        OApiResponsesBuilder(
            OApiContentBuilder(
                OApiSchemaBuilder(
                    OApiJsonSchemaBuilder()
                ),
                OApiExampleBuilder()
            ),
            OApiSchemaBuilder(
                OApiJsonSchemaBuilder()
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
                    OApiJsonSchemaBuilder()
                )
            ),
            OApiRequestBodyBuilder(
                OApiContentBuilder(
                    OApiSchemaBuilder(
                        OApiJsonSchemaBuilder()
                    ),
                    OApiExampleBuilder()
                )
            ),
            OApiResponsesBuilder(
                OApiContentBuilder(
                    OApiSchemaBuilder(
                        OApiJsonSchemaBuilder()
                    ),
                    OApiExampleBuilder()
                ),
                OApiSchemaBuilder(
                    OApiJsonSchemaBuilder()
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