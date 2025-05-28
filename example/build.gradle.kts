plugins {
    kotlin("jvm")
}

group = "com.open-tool"

repositories {
    mavenCentral()
}
val koogVersion: String by project
dependencies {
    implementation("ai.koog:koog-agents:${koogVersion}")
    implementation(project(":agent"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}