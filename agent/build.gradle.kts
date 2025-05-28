plugins {
    kotlin("jvm")
}

group = "com.open-tool"

repositories {
    mavenCentral()
}
val koogVersion: String by project
dependencies {
    implementation("ai.koog:koog-agents:$koogVersion")
    implementation(project(":tools"))
    implementation(project(":playwright"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}