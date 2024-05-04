plugins {
    kotlin("jvm")
}

group = "io.github.smiley4"
version = "3.0.0-indev"

repositories {
    mavenCentral()
}

dependencies {

    implementation(project(":ktor-swagger-ui"))

    val ktorVersion = "2.3.7"
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-test-host:$ktorVersion")

    val kotlinLoggingVersion = "3.0.5"
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")

    val logbackVersion = "1.4.11"
    implementation("ch.qos.logback:logback-classic:$logbackVersion")



}
