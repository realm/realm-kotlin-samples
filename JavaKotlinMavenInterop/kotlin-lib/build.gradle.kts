import java.net.URI

plugins {
    kotlin("jvm") version "1.9.21"
    id("io.realm.kotlin") version "1.13.0"
    `maven-publish`
    signing
}

group = "com.mongodb.devicesync"
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
        maven {
            this.name = "CustomNexus"
            credentials {
                username = "<nexusUserName>"
                password = "<nexusPassword>"
            }
            if((project.version as String).endsWith("-SNAPSHOT")) {
                url = URI("https://pathToNexus.com/relativePathToSnaphots")
            } else {
                url = URI("https://pathToNexus.com/relativePathToReleases")
            }
        }
    }
}

dependencies {
    api("io.realm.kotlin:library-sync-jvm:1.13.0")
    api("io.reactivex.rxjava3:rxjava:3.0.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.3.9")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

kotlin {
    jvmToolchain(11)
}

tasks.test {
    useJUnitPlatform()
}