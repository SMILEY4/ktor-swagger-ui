package io.github.smiley4.ktorswaggerui.data

import io.ktor.http.HttpMethod

/**
 * Filters paths to determine which to include (return 'true') in the spec and which to hide (return 'true').
 */
typealias PathFilter = (method: HttpMethod, url: List<String>) -> Boolean
