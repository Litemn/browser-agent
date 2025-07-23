plugins {
    kotlin("jvm")
}

group = "com.open-tool"
repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":tools"))
    implementation(libs.koog)
    implementation("com.microsoft.playwright:driver:1.52.0")
    implementation("com.microsoft.playwright:driver-bundle:1.52.0")
    implementation("com.microsoft.playwright:playwright:1.52.0")
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.13.8")
}

tasks.test {
    useJUnitPlatform()
}
