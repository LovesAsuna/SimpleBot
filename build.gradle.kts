plugins {
    kotlin("jvm") version "1.7.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("kapt") version "1.6.10"
}

description = "一个基于Mirai的机器人"
group = "com.hyosakura"
version = "4.1.0"

repositories {
    mavenCentral()
}

val workDir = "E:\\work\\Mirai-Console\\plugins"
val kotlinVersion = "1.7.0"
val miraiVersion = "2.11.1"

// mirai
dependencies {
    compileOnly("net.mamoe:mirai-console:$miraiVersion")
    compileOnly("net.mamoe:mirai-core:$miraiVersion")
    testImplementation("net.mamoe:mirai-console:$miraiVersion")
    testImplementation("net.mamoe:mirai-core:$miraiVersion")
}

// kotlin
dependencies {
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
}

dependencies {
    implementation("io.ktor:ktor-client-core:1.6.7")
    implementation("io.ktor:ktor-client-okhttp:1.6.7")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
    implementation("com.h2database:h2:2.1.212")
    implementation("org.jetbrains.exposed:exposed-core:0.38.2")
    implementation("org.jetbrains.exposed:exposed-dao:0.38.2")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.38.2")
    implementation("org.jetbrains.exposed:exposed-java-time:0.38.2")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.charleskorn.kaml:kaml:0.45.0")
    implementation("com.github.oshi:oshi-core:6.1.6")
    implementation("net.java.dev.jna:jna:5.11.0")
    implementation("net.java.dev.jna:jna-platform:5.11.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

tasks.compileJava {
    sourceCompatibility = "11"
    targetCompatibility = "11"
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Copy>("copyFile") {
    dependsOn("shadowJar")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from("build/libs/")
    into(workDir)
    include("Mirai-Bot*.jar")
    rename {
        "Bot.jar"
    }
}