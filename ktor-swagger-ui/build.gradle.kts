object Meta {
    const val artifactId = "ktor-swagger-ui"
}

dependencies {
    val ktorVersion = "2.3.7"
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-webjars:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    testImplementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    testImplementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    testImplementation("io.ktor:ktor-server-auth:$ktorVersion")
    testImplementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")

    val swaggerUiVersion = "5.9.0" // this version must match the version declared in the code (SwaggerPlugin#SWAGGER_UI_WEBJARS_VERSION)
    implementation("org.webjars:swagger-ui:$swaggerUiVersion")

    val swaggerParserVersion = "2.1.19"
    implementation("io.swagger.parser.v3:swagger-parser:$swaggerParserVersion")

    val schemaKeneratorVersion = "0.1"
    implementation("io.github.smiley4:schema-kenerator-core:$schemaKeneratorVersion")
    implementation("io.github.smiley4:schema-kenerator-reflection:$schemaKeneratorVersion")
    implementation("io.github.smiley4:schema-kenerator-swagger:$schemaKeneratorVersion")

    val kotlinLoggingVersion = "3.0.5"
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")

    val versionMockk = "1.13.8"
    testImplementation("io.mockk:mockk:$versionMockk")

    val versionKotest = "5.8.0"
    testImplementation("io.kotest:kotest-runner-junit5:$versionKotest")
    testImplementation("io.kotest:kotest-assertions-core:$versionKotest")

    val versionKotlinTest = "1.8.21"
    testImplementation("org.jetbrains.kotlin:kotlin-test:$versionKotlinTest")

}
