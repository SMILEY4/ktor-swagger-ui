package io.github.smiley4.ktorswaggerui.data

import io.swagger.v3.oas.models.OpenAPI

/**
 * Function executed after building the openapi-spec.
 * @author <a href=mailto:mcxinyu@foxmail.com>yuefeng</a> in 2024/3/25.
 */
typealias PostBuild = (openApi: OpenAPI, specId: String) -> Unit
