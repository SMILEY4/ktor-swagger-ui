package io.github.smiley4.ktorswaggerui.data

import io.ktor.http.HttpMethod

typealias PathFilter = (method: HttpMethod, url: List<String>) -> Boolean
