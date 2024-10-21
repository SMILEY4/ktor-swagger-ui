plugins {
    kotlin("jvm") version "1.9.25"
    id("org.jetbrains.dokka") version "1.9.20" apply false
    id("org.owasp.dependencycheck") version "8.2.1" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.0" apply false
    id("com.vanniktech.maven.publish") version "0.28.0" apply false
}

repositories {
    mavenCentral()
}