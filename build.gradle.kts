plugins {
    kotlin("jvm") version "1.9.22"
    application
}

kotlin {
    jvmToolchain(21)
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

dependencies {
    implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-server-netty:2.3.7")
    implementation("io.ktor:ktor-server-pebble:2.3.7")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.7")

    implementation("com.h2database:h2:2.2.224")

    implementation("org.jetbrains.exposed:exposed-core:0.43.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.43.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.43.0")

    implementation("org.mindrot:jbcrypt:0.4")
}