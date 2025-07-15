plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "ru.sinvic"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register("printClasspath") {
    doLast {
        println("Current classpath:")
        configurations["compileClasspath"].files.forEach { file ->
            println(" - $file")
        }
    }
}