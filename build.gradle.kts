@file:Suppress("SpellCheckingInspection")

import org.beryx.jlink.JlinkZipTask
import org.gradle.jvm.tasks.Jar

plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "1.8.12"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "2.25.0"
}

evaluationDependsOn(":decompiler-api:vineflower-impl")

group = "lol.koblizek"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainModule = "lol.koblizek.bytelens"
    mainClass = "lol.koblizek.bytelens.core.ByteLens"
}

javafx {
    version = "22.0.1"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.swing")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Jar> {
    from(project(":decompiler-api:vineflower-impl").layout.buildDirectory.file("libs").get()) {
        into("libs")
        include("vineflower-impl-*.jar")
    }
}

allprojects {
    apply(plugin = "java")
    dependencies {
        implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.23.1")
        implementation("dev.mccue:resolve:2024.05.26")
        implementation("org.slf4j:slf4j-api:2.0.13")
        implementation("org.apache.xmlgraphics:batik-transcoder:1.17") {
            exclude("xml-apis", "xml-apis")
        }
        implementation("org.apache.xmlgraphics:batik-codec:1.17") {
            exclude("xml-apis", "xml-apis")
        }
        implementation("org.apache.xmlgraphics:batik-dom:1.17") {
            exclude("xml-apis", "xml-apis")
        }
        implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
        implementation("org.fxmisc.richtext:richtextfx:0.11.2")
        compileOnly("org.jetbrains:annotations:24.1.0")
    }
}

dependencies {
    implementation(project(":decompiler-api"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

jlink {
    imageZip = project.file("${layout.buildDirectory}/distributions/app-${javafx.platform.classifier}.zip")
    options = listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
    launcher {
        name = "app"
    }
}

tasks.withType<JlinkZipTask> {
    group = "distribution"
}