import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    `maven-publish`
    id("org.owasp.dependencycheck") version "8.2.1"
}

group = "io.github.smiley4"
version = "1.6.1"

repositories {
    mavenCentral()
}

dependencies {

    val ktorVersion = "2.2.4"
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-webjars:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    testImplementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    testImplementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    testImplementation("io.ktor:ktor-server-auth:$ktorVersion")
    testImplementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")

    val swaggerUiVersion = "4.15.0" // this version must match the version declared in the code (SwaggerPlugin#SWAGGER_UI_WEBJARS_VERSION)
    implementation("org.webjars:swagger-ui:$swaggerUiVersion")

    val swaggerParserVersion = "2.1.13"
    implementation("io.swagger.parser.v3:swagger-parser:$swaggerParserVersion")

    val jsonSchemaGeneratorVersion = "4.28.0"
    implementation("com.github.victools:jsonschema-generator:$jsonSchemaGeneratorVersion")
    implementation("com.github.victools:jsonschema-module-jackson:$jsonSchemaGeneratorVersion")
    implementation("com.github.victools:jsonschema-module-swagger-2:$jsonSchemaGeneratorVersion")

    val kotlinLoggingVersion = "2.1.23"
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")

    val logbackVersion = "1.4.5"
    testImplementation("ch.qos.logback:logback-classic:$logbackVersion")

    val versionMockk = "1.12.7"
    testImplementation("io.mockk:mockk:$versionMockk")

    val versionKotest = "5.4.2"
    testImplementation("io.kotest:kotest-runner-junit5:$versionKotest")
    testImplementation("io.kotest:kotest-assertions-core:$versionKotest")

    val versionKotlinTest = "1.7.21"
    testImplementation("org.jetbrains.kotlin:kotlin-test:$versionKotlinTest")

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
                description.set("Ktor plugin to document routes and provide Swagger UI ")
                url.set("https://github.com/SMILEY4/ktor-swagger-ui")
            }
        }
    }
}
