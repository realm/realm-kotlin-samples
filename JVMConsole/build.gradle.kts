import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
}

buildscript {
    dependencies {
        classpath("io.realm.kotlin:gradle-plugin:1.0.0")
    }
}
rootProject.extra["realmVersion"] = "1.0.0"

apply(plugin = "io.realm.kotlin")

group = "io.realm.example"
version = "1.0.0"

repositories {
    mavenCentral()
    // Only required for realm-kotlin snapshots
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}
dependencies {
    implementation("com.jakewharton.fliptables:fliptables:1.1.0")
    implementation("io.realm.kotlin:library-base:1.0.0")
    testImplementation(kotlin("test-junit"))
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}
application {
    mainClassName = "io.realm.example.MainKt"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "io.realm.example.MainKt"
    }
    configurations["runtimeClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
