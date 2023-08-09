import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    id("io.realm.kotlin") version "1.10.2"
    `maven-publish`
}

group = "com.mongodb"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("RealmKotlinInterface") {
            from(components["kotlin"])
            groupId = project.group as String
            artifactId = "kotlin-lib"
            version = project.version as String
            artifact(tasks["javadocJar"])
            artifact(tasks["sourcesJar"])
        }
    }
    repositories {
        mavenLocal()
    }
}

dependencies {
    api("io.realm.kotlin:library-sync-jvm:1.10.2")
    api("io.reactivex.rxjava3:rxjava:3.0.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.3.9")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.test {
    useJUnitPlatform()
}