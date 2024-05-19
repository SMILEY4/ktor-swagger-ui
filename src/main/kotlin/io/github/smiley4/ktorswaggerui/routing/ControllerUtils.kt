package io.github.smiley4.ktorswaggerui.routing

import io.ktor.server.config.*

object ControllerUtils {

    var appConfig: ApplicationConfig? = null

    fun getRootPath(appConfig: ApplicationConfig): String {
        return appConfig.propertyOrNull("ktor.deployment.rootPath")?.getString()?.let { "/${dropSlashes(it)}" } ?: ""
    }

    fun dropSlashes(str: String): String {
        var value = str
        value = if (value.startsWith("/")) value.substring(1) else value
        value = if (value.endsWith("/")) value.substring(0, value.length - 1) else value
        return value
    }

}
