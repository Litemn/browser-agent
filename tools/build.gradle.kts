plugins {
    id("buildsrc.convention.kotlin-jvm")
    kotlin("jvm")
    `java-library`
}

group = "com.open-tool"
val koogVersion: String by project
dependencies {
    implementation("ai.koog:koog-agents:${koogVersion}")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
}
