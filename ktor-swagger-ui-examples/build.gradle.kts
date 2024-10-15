val projectGroupId: String by project
val projectVersion: String by project
group = projectGroupId
version = projectVersion

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.0.20"
}

repositories {
    mavenCentral()
}

dependencies {
    val versionKtor: String by project
    val versionSwaggerParser: String by project
    val versionSchemaKenerator: String by project
    val versionKotlinLogging: String by project
    val versionLogback: String by project

    implementation(project(":ktor-swagger-ui"))

    implementation("io.ktor:ktor-server-netty-jvm:$versionKtor")
    implementation("io.ktor:ktor-server-content-negotiation:$versionKtor")
    implementation("io.ktor:ktor-serialization-jackson:$versionKtor")
    implementation("io.ktor:ktor-server-auth:$versionKtor")
    implementation("io.ktor:ktor-server-call-logging:$versionKtor")
    implementation("io.ktor:ktor-server-test-host:$versionKtor")

    implementation("io.github.smiley4:schema-kenerator-core:$versionSchemaKenerator")
    implementation("io.github.smiley4:schema-kenerator-reflection:$versionSchemaKenerator")
    implementation("io.github.smiley4:schema-kenerator-serialization:$versionSchemaKenerator")
    implementation("io.github.smiley4:schema-kenerator-swagger:$versionSchemaKenerator")
    implementation("io.github.smiley4:schema-kenerator-jackson:$versionSchemaKenerator")

    implementation("io.swagger.parser.v3:swagger-parser:$versionSwaggerParser")
    implementation("io.github.microutils:kotlin-logging-jvm:$versionKotlinLogging")
    implementation("ch.qos.logback:logback-classic:$versionLogback")
}

kotlin {
    jvmToolchain(11)
}
