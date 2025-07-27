plugins {
    kotlin("jvm")
}

group = "com.open-tool"

repositories {
    mavenCentral()
}
dependencies {
    implementation(libs.koog)
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