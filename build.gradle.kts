import com.vanniktech.maven.publish.SonatypeHost
import io.gitlab.arturbosch.detekt.Detekt

object Meta {
    const val groupId = "io.github.smiley4"
    const val artifactId = "ktor-swagger-ui"
    const val version = "2.5.0"
    const val name = "Ktor Swagger-UI"
    const val description = "Ktor plugin to document routes and provide Swagger UI"
    const val licenseName = "The Apache License, Version 2.0"
    const val licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0.txt"
    const val scmUrl = "https://github.com/SMILEY4/ktor-swagger-ui"
    const val scmConnection = "scm:git:git://github.com/SMILEY4/ktor-swagger-ui.git"
    const val developerName = "smiley4"
    const val developerUrl = "https://github.com/SMILEY4"
}

group = Meta.groupId
version = Meta.version

plugins {
    kotlin("jvm") version "1.8.21"
    kotlin("plugin.serialization") version "1.8.21"
    id("org.owasp.dependencycheck") version "8.2.1"
    id("com.vanniktech.maven.publish") version "0.25.2"
    id("io.gitlab.arturbosch.detekt") version "1.23.0"
}

repositories {
    mavenCentral()
    maven(url = "https://raw.githubusercontent.com/glureau/json-schema-serialization/mvn-repo")
}

dependencies {

    val ktorVersion = "2.3.3"
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

    val jacksonVersion = "2.14.2"
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${jacksonVersion}")

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

    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    testImplementation("com.github.Ricky12Awesome:json-schema-serialization:0.9.9")
    testImplementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
}

kotlin {
    jvmToolchain(11)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom("$projectDir/config/detekt.yml")
    baseline = file("$projectDir/config/baseline.xml")
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        md.required.set(true)
        xml.required.set(false)
        txt.required.set(false)
        sarif.required.set(false)
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.S01)
    signAllPublications()
    coordinates(Meta.groupId, Meta.artifactId, Meta.version)
    pom {
        name.set(Meta.name)
        description.set(Meta.description)
        url.set(Meta.scmUrl)
        licenses {
            license {
                name.set(Meta.licenseName)
                url.set(Meta.licenseUrl)
                distribution.set(Meta.licenseUrl)
            }
        }
        scm {
            url.set(Meta.scmUrl)
            connection.set(Meta.scmConnection)
        }
        developers {
            developer {
                id.set(Meta.developerName)
                name.set(Meta.developerName)
                url.set(Meta.developerUrl)
            }
        }
    }
}
