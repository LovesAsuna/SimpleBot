import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.21"
}

description = "一个基于Mirai的机器人"
group = "com.hyosakura"
version = "4.1.0"

repositories {
    mavenCentral()
}

val workDir = "E:\\work\\Mirai-Console\\plugins"
val kotlinVersion = "1.5.30"
val miraiVersion = "2.7.1"

// mirai
dependencies {
    compileOnly("net.mamoe:mirai-console:$miraiVersion")
    compileOnly("net.mamoe:mirai-core:$miraiVersion")
    testImplementation("net.mamoe:mirai-console:$miraiVersion")
    testImplementation("net.mamoe:mirai-core:$miraiVersion")
}

// kotlin
dependencies {
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
}

dependencies {
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    implementation("com.h2database:h2:1.4.200")
    implementation("org.hibernate.orm:hibernate-core:6.0.0.Alpha8")
    implementation("com.charleskorn.kaml:kaml:0.36.0")
    implementation("com.github.oshi:oshi-core:5.8.3")
    implementation("net.java.dev.jna:jna:5.9.0")
    implementation("net.java.dev.jna:jna-platform:5.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.2")
    implementation("com.github.theholywaffle:teamspeak3-api:1.3.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

tasks.withType<JavaCompile>() {
    sourceCompatibility = "11"
    targetCompatibility = "11"
}

tasks.withType<KotlinCompile>() {
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