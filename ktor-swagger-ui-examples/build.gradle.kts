plugins {
    kotlin("jvm")
}

group = "io.github.smiley4"
version = "3.0.0-indev"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}