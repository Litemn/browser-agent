plugins {
    kotlin("jvm")
    `java-library`
}

group = "com.open-tool"
dependencies {
    implementation(libs.koog)
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
}
