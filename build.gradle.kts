plugins {
    kotlin("jvm") version "2.1.0"
}

group = "com.stochastictinkr"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.3")
    compileOnly("org.jetbrains:annotations:24.1.0")
    testImplementation("io.mockk:mockk:1.13.12")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}