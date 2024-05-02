import io.gitlab.arturbosch.detekt.Detekt

plugins {
    kotlin("jvm") version "1.8.21"
    id("org.owasp.dependencycheck") version "8.2.1"
    id("io.gitlab.arturbosch.detekt") version "1.23.0"
}

subprojects {

    val ktorSwaggerUiVersion: String by project
    val ktorSwaggerUiGroupId: String by project
    group = ktorSwaggerUiGroupId
    version = ktorSwaggerUiVersion

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.owasp.dependencycheck")

    repositories {
        mavenLocal()
        mavenCentral()
    }

    dependencies {}

    kotlin {
        jvmToolchain(11)
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    tasks.withType<Detekt>().configureEach {
        ignoreFailures = true // todo: temporary
        buildUponDefaultConfig = true
        allRules = false
        config.setFrom("$projectDir/config/detekt.yml")
        baseline.set(file("$projectDir/config/baseline.xml"))
        reports {
            html.required.set(true)
            md.required.set(true)
            xml.required.set(false)
            txt.required.set(false)
            sarif.required.set(false)
        }
    }

}

//mavenPublishing {
//    publishToMavenCentral(SonatypeHost.S01)
//    signAllPublications()
//    coordinates(Meta.groupId, Meta.artifactId, Meta.version)
//    pom {
//        name.set(Meta.name)
//        description.set(Meta.description)
//        url.set(Meta.scmUrl)
//        licenses {
//            license {
//                name.set(Meta.licenseName)
//                url.set(Meta.licenseUrl)
//                distribution.set(Meta.licenseUrl)
//            }
//        }
//        scm {
//            url.set(Meta.scmUrl)
//            connection.set(Meta.scmConnection)
//        }
//        developers {
//            developer {
//                id.set(Meta.developerName)
//                name.set(Meta.developerName)
//                url.set(Meta.developerUrl)
//            }
//        }
//    }
//}
