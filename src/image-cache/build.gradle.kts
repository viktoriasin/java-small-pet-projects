plugins {
    id("java")
    id("checkstyle")
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

configure<CheckstyleExtension> {
    toolVersion = "10.3.1" // версия Checkstyle
    configFile = file("config/checkstyle/checkstyle.xml") // путь к файлу настроек
    maxWarnings = 0 // максимальное число предупреждений
    maxErrors = 0 // максимальное число ошибок
}
tasks.named<Checkstyle>("checkstyleMain") {
    source = fileTree("src/main/java")// источник основного кода
    isIgnoreFailures = false // теперь устанавливается на уровне задачи
}

tasks.named<Checkstyle>("checkstyleTest") {
    source = fileTree("src/test/java")// источник тестового кода
    isIgnoreFailures = false // аналогично устанавливаем на уровне задачи
}

tasks.named<Task>("check") {
    dependsOn(tasks.named("checkstyleMain"), tasks.named("checkstyleTest"))
}

tasks.named<Checkstyle>("checkstyleMain") {
    reports {
        html.required.set(true) // включение HTML отчёта
    }
}