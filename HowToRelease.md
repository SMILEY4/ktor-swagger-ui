# How to release

https://vanniktech.github.io/gradle-maven-publish-plugin/central/

1. Credentials should be configured in a gradle.properties file (in user home)

2. `./gradlew publishAllPublicationsToMavenCentral --no-configuration-cache`

3. `./gradlew closeAndReleaseRepository`