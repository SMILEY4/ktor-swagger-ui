import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    `maven-publish`
    id("org.owasp.dependencycheck") version "8.2.1"
    kotlin("plugin.serialization") version "1.8.21" // TODO: remove!!!!
}

group = "io.github.smiley4"
version = "2.0.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven(url = "https://raw.githubusercontent.com/glureau/json-schema-serialization/mvn-repo")
}

dependencies {

    implementation(project(":core"))

    val ktorVersion = "2.3.0"
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-webjars:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-test-host:$ktorVersion")

    val swaggerUiVersion = "4.15.0" // this version must match the version declared in the code (SwaggerPlugin#SWAGGER_UI_WEBJARS_VERSION)
    implementation("org.webjars:swagger-ui:$swaggerUiVersion")

    val swaggerParserVersion = "2.1.13"
    implementation("io.swagger.parser.v3:swagger-parser:$swaggerParserVersion")

    val jsonSchemaGeneratorVersion = "4.28.0"
    implementation("com.github.victools:jsonschema-generator:$jsonSchemaGeneratorVersion")
    implementation("com.github.victools:jsonschema-module-jackson:$jsonSchemaGeneratorVersion")
    implementation("com.github.victools:jsonschema-module-swagger-2:$jsonSchemaGeneratorVersion")

    val jacksonVersion = "2.14.2"
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${jacksonVersion}")

    val kotlinLoggingVersion = "2.1.23"
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")

    val logbackVersion = "1.4.5"
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("com.github.Ricky12Awesome:json-schema-serialization:0.9.9")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}
tasks.withType<JavaCompile> {
    targetCompatibility = "17"
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "ktor-swagger-ui-examples"
            from(components["java"])
            pom {
                name.set("Ktor Swagger-UI Examples")
                description.set("Examples for the ktor swagger-ui plugin")
                url.set("https://github.com/SMILEY4/ktor-swagger-ui")
            }
        }
    }
}