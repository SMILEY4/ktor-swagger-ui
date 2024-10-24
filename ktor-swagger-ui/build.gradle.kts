import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SonatypeHost
import io.gitlab.arturbosch.detekt.Detekt

val projectGroupId: String by project
val projectVersion: String by project
group = projectGroupId
version = projectVersion

plugins {
    kotlin("jvm")
    id("org.owasp.dependencycheck")
    id("io.gitlab.arturbosch.detekt")
    id("com.vanniktech.maven.publish")
    id("org.jetbrains.dokka")
}

repositories {
    mavenCentral()
}

dependencies {
    val versionKtor: String by project
    val versionSwaggerUI: String by project
    val versionSwaggerParser: String by project
    val versionSchemaKenerator: String by project
    val versionKotlinLogging: String by project
    val versionKotest: String by project
    val versionKotlinTest: String by project
    val versionMockk: String by project

    implementation("io.ktor:ktor-server-core-jvm:$versionKtor")
    implementation("io.ktor:ktor-server-auth:$versionKtor")
    implementation("io.ktor:ktor-server-resources:$versionKtor")

    implementation("org.webjars:swagger-ui:$versionSwaggerUI")

    implementation("io.swagger.parser.v3:swagger-parser:$versionSwaggerParser")

    implementation("io.github.smiley4:schema-kenerator-core:$versionSchemaKenerator")
    implementation("io.github.smiley4:schema-kenerator-reflection:$versionSchemaKenerator")
    implementation("io.github.smiley4:schema-kenerator-swagger:$versionSchemaKenerator")

    implementation("io.github.oshai:kotlin-logging-jvm:$versionKotlinLogging")

    testImplementation("io.ktor:ktor-server-netty-jvm:$versionKtor")
    testImplementation("io.ktor:ktor-server-content-negotiation:$versionKtor")
    testImplementation("io.ktor:ktor-serialization-jackson:$versionKtor")
    testImplementation("io.ktor:ktor-server-auth:$versionKtor")
    testImplementation("io.ktor:ktor-server-call-logging:$versionKtor")
    testImplementation("io.ktor:ktor-server-test-host:$versionKtor")
    testImplementation("io.kotest:kotest-runner-junit5:$versionKotest")
    testImplementation("io.kotest:kotest-assertions-core:$versionKotest")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$versionKotlinTest")
    testImplementation("io.mockk:mockk:$versionMockk")
}

kotlin {
    jvmToolchain(11)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

detekt {
    ignoreFailures = false
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom("$projectDir/../detekt/detekt.yml")
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
    val projectGroupId: String by project
    val projectVersion: String by project
    val projectArtifactIdBase: String by project
    val projectNameBase: String by project
    val projectDescriptionBase: String by project
    val projectScmUrl: String by project
    val projectScmConnection: String by project
    val projectLicenseName: String by project
    val projectLicenseUrl: String by project
    val projectDeveloperName: String by project
    val projectDeveloperUrl: String by project

    configure(KotlinJvm(JavadocJar.Dokka("dokkaHtml"), true))
    publishToMavenCentral(SonatypeHost.S01)
    signAllPublications()
    coordinates(projectGroupId, projectArtifactIdBase, projectVersion)
    pom {
        name.set(projectNameBase)
        description.set(projectDescriptionBase)
        url.set(projectScmUrl)
        licenses {
            license {
                name.set(projectLicenseName)
                url.set(projectLicenseUrl)
                distribution.set(projectLicenseUrl)
            }
        }
        scm {
            url.set(projectScmUrl)
            connection.set(projectScmConnection)
        }
        developers {
            developer {
                id.set(projectDeveloperName)
                name.set(projectDeveloperName)
                url.set(projectDeveloperUrl)
            }
        }
    }
}