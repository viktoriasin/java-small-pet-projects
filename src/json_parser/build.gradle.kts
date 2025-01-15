import name.remal.gradle_plugins.sonarlint.SonarLintExtension
import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel

plugins {
    idea
    java
    id("name.remal.sonarlint")
    id("com.diffplug.spotless")
}

idea {
    project {
        languageLevel = IdeaLanguageLevel(21)
    }
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

group = "hometask.json"

repositories {
    mavenLocal()
    mavenCentral()
}



configurations.all {
    resolutionStrategy {
        failOnVersionConflict()
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:all,-serial,-processing"))

    dependsOn("spotlessApply")
}

apply<name.remal.gradle_plugins.sonarlint.SonarLintPlugin>()
configure<SonarLintExtension> {
    nodeJs {
        detectNodeJs = false
        logNodeJsNotFound = false
    }
}

apply<com.diffplug.gradle.spotless.SpotlessPlugin>()
configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    java {
        palantirJavaFormat("2.39.0")
    }
}

dependencies {
    testImplementation ( platform("org.junit:junit-bom:5.10.0") )
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.skyscreamer:jsonassert:1.5.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging.showExceptions = true
    reports {
        junitXml.required.set(true)
        html.required.set(true)
    }
}


