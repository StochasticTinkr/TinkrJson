plugins {
    kotlin("jvm") version "2.1.0"
    `maven-publish`
}

group = "com.stochastictinkr"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
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


publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("tinkr-json")
                description.set(" a lightweight and expressive Kotlin library for working with JSON.")
                url.set("https://github.com/StochasticTinkr/TinkrJson")

                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("StochasticTinkr")
                        name.set("Daniel Pitts")
                    }
                }
            }
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}
