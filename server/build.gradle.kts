plugins {
    id("java")
    application

    id("com.github.johnrengelman.shadow") version "8.1.1" // ✅ 추가

}

group = "io.github.minkik715.mkchatting.server"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("io.netty:netty-all:4.2.0.Final")

    implementation(project(":interface"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    // ✅ 여기에 메인 클래스 지정 (패키지 전체 경로 포함)
    mainClass.set("io.github.minkik715.mkchatting.server.ChatServer")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "io.github.minkik715.mkchatting.client.ChatClient"
    }
}

tasks.test {
    useJUnitPlatform()
}