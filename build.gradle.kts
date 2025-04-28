plugins {
    id("java")
}

group = "io.github.minkik715.mkchatting"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
    }
    dependencies {
        // https://mvnrepository.com/artifact/io.netty/netty-all
        implementation("io.netty:netty-all:4.2.0.Final")
        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }

    tasks.test {
        useJUnitPlatform()
    }
}



