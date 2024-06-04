import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libsx.plugins.kotlinJvm)
    application
}

// Explicitly adding the plugin to the classpath as it makes it easier to control the version
// centrally (don't need version in the 'plugins' block). Further, snapshots are not published with
// marker interface so would need to be added to the classpath manually anyway.
buildscript {
    dependencies {
        classpath(libsx.realm.plugin)
    }
}

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
    implementation(libsx.realm.base)
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
