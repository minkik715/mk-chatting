plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1" // ✅ 추가

}

group = "io.github.minkik715.mkchatting.server"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":interface"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}