import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.20"
    `maven-publish`
}

group = "io.github.smiley4"
version = "0.5.0"

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = "2.1.0"
    val swaggerUiVersion = "4.14.0" // this version must match the version declared in the code (SwaggerPlugin#SWAGGER_UI_WEBJARS_VERSION)
    val swaggerParserVersion = "2.1.2"
    val jsonSchemaGeneratorVersion = "4.26.0"
    val kotlinLoggingVersion = "2.1.23"
    val logbackVersion = "1.2.11"
    val versionKotest = "5.4.2"
    val versionMockk = "1.12.7"

    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-webjars:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("org.webjars:swagger-ui:$swaggerUiVersion")
    implementation("io.swagger.parser.v3:swagger-parser:$swaggerParserVersion")
    implementation("com.github.victools:jsonschema-generator:$jsonSchemaGeneratorVersion")
    implementation("com.github.victools:jsonschema-module-jackson:$jsonSchemaGeneratorVersion")
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")

    testImplementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    testImplementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    testImplementation("io.ktor:ktor-server-auth:$ktorVersion")
    testImplementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation("io.mockk:mockk:$versionMockk")
    testImplementation("io.kotest:kotest-runner-junit5:$versionKotest")
    testImplementation("io.kotest:kotest-assertions-core:$versionKotest")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.20")

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "ktor-swagger-ui"
            from(components["java"])
            pom {
                name.set("Ktor Swagger-UI")
                description.set("Ktor plugin to document routes and enable Swagger UI ")
                url.set("https://github.com/SMILEY4/ktor-swagger-ui")
            }
        }
    }
}