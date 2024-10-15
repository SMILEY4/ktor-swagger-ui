rootProject.name = "ktor-swagger-ui"

include("ktor-swagger-ui-examples")
include("ktor-swagger-ui")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}