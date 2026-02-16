plugins {
    kotlin("jvm") version "2.2.21"
    id ("io.ktor.plugin") version "3.3.1"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    testImplementation(libs.ktor.server.testHost)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.runner.junit5)
    runtimeOnly(libs.ktor.server.configYaml)
    runtimeOnly(libs.logback.classic)
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

application {
    mainClass = "ApplicationKt"
}
