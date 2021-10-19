import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    id("io.realm.kotlin") version "0.6.0"
    application
}
group = "io.realm.example"
version = "1.0"

repositories {
    mavenCentral()
}
dependencies {
    implementation("com.jakewharton.fliptables:fliptables:1.1.0")
    implementation("io.realm.kotlin:library-base:0.6.0")
    testImplementation(kotlin("test-junit"))

    // only needed to build fatjar (by running ./gradlew jar)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
    implementation("org.jetbrains.kotlinx:atomicfu:0.16.3")

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
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}