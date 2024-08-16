@file:Suppress("SpellCheckingInspection")

import org.beryx.jlink.JlinkZipTask
import org.gradle.jvm.tasks.Jar
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

defaultTasks("licenseFormat")

plugins {
    `java-library`
    application
    id("org.javamodularity.moduleplugin") version "1.8.12"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.cadixdev.licenser") version "0.6.1"
    id("org.beryx.jlink") version "2.25.0"
    id("org.sonarqube") version "5.1.0.4882"
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

tasks.compileJava {
    dependsOn(subprojects.map { it.tasks.named("compileJava") })
}

javafx {
    version = "22.0.1"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.swing")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Jar> {
    outputs.upToDateWhen { false }
    dependsOn(":decompiler-api:vineflower-impl:jar")
    from(project(":decompiler-api:vineflower-impl").layout.buildDirectory.file("libs").get()) {
        into("libs")
        include("vineflower-impl.jar")
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "org.cadixdev.licenser")

    license {
        header(rootProject.file("HEADER"))
        include("**/*.java")
        newLine = true
    }

    dependencies {
        implementation(libs.javaparser.core)
        implementation(libs.log4j.slf4j2.impl)
        implementation(libs.resolve)
        implementation(libs.slf4j.api)
        implementation(libs.asm)
        implementation(libs.asm.util)
        implementation(libs.commons.io)
        implementation(libs.log4j.core)
        implementation(libs.batik.transcoder) {
            exclude("xml-apis", "xml-apis")
        }
        implementation(libs.batik.codec) {
            exclude("xml-apis", "xml-apis")
        }
        implementation(libs.batik.dom) {
            exclude("xml-apis", "xml-apis")
        }
        implementation("org.apache.xmlgraphics:batik-script:1.17") {
            exclude("xml-apis", "xml-apis")
        }
        implementation(libs.jackson.databind)
        implementation(libs.richtextfx)
        implementation(libs.annotations)
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

configurations.matching { it.name.contains("DownloadSources") || it.name.contains("downloadSources") }
    .configureEach {
        attributes {
            val os = DefaultNativePlatform.getCurrentOperatingSystem().toFamilyName()
            val arch = DefaultNativePlatform.getCurrentArchitecture().name
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class, Usage.JAVA_RUNTIME))
            attribute(OperatingSystemFamily.OPERATING_SYSTEM_ATTRIBUTE, objects.named(OperatingSystemFamily::class, os))
            attribute(MachineArchitecture.ARCHITECTURE_ATTRIBUTE, objects.named(MachineArchitecture::class, arch))
        }
    }