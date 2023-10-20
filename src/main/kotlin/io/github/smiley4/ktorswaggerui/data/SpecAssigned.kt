package io.github.smiley4.ktorswaggerui.data

/**
 * url - the parts of the route-url split at all `/`.
 * tags - the tags assigned to the route
 */
typealias SpecAssigner = (url: String, tags: List<String>) -> String
