package io.github.smiley4.ktorswaggerui.tests

import io.github.smiley4.ktorswaggerui.specbuilder.OApiComponentsBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiContentBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiExampleBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiInfoBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiPathBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiPathsBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiSchemaBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiSecuritySchemesBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiServersBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.OApiTagsBuilder
import io.github.smiley4.ktorswaggerui.specbuilder.RouteCollector

fun getOApiInfoBuilder() = OApiInfoBuilder()

fun getOApiComponentsBuilder() = OApiComponentsBuilder()

fun getOApiSchemaBuilder() = OApiSchemaBuilder()

fun getOApiExampleBuilder() = OApiExampleBuilder()

fun getOApiContentBuilder() = OApiContentBuilder()

fun getOApiPathBuilder() = OApiPathBuilder()

fun getOApiPathsBuilder(routeCollector: RouteCollector) = OApiPathsBuilder(routeCollector)

fun getOApiSecuritySchemesBuilder() = OApiSecuritySchemesBuilder()

fun getOApiServersBuilder() = OApiServersBuilder()

fun getOApiTagsBuilder() = OApiTagsBuilder()