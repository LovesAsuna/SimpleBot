import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
    id("com.github.johnrengelman.shadow") version "6.1.0"
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

// log
dependencies {
    compileOnly("org.apache.logging.log4j:log4j-api:2.14.1")
    compileOnly("org.apache.logging.log4j:log4j-core:2.14.1")
    compileOnly("org.slf4j:slf4j-api:1.7.32")
    compileOnly("org.apache.logging.log4j:log4j-slf4j-impl:2.14.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.5")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.5")
}

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
    implementation("org.jsoup:jsoup:1.14.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.5")
    implementation("com.h2database:h2:1.4.200")
    implementation("org.hibernate.orm:hibernate-core:6.0.0.Alpha8")
    implementation("com.charleskorn.kaml:kaml:0.35.3")
    implementation("com.github.oshi:oshi-core:5.8.2")
    implementation("net.java.dev.jna:jna:5.9.0")
    implementation("net.java.dev.jna:jna-platform:5.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.github.theholywaffle:teamspeak3-api:1.3.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.0")

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