plugins {
    id("java")
    id("org.javamodularity.moduleplugin") version "1.8.12"
}

group = parent?.group ?: "lol.koblizek"
version = parent?.version ?: "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.vineflower:vineflower:1.10.1")
    parent?.let { implementation(it.project(":decompiler-api")) }
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}