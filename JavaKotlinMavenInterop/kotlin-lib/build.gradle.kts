import java.net.URI

plugins {
    alias(libsx.plugins.kotlinJvm)
    // For some reason libsx.plugins.realm does not resolve directly so go through the provider
    // alias(libsx.plugins.realm)
    id(libsx.plugins.realm.get().pluginId)
    `maven-publish`
    signing
}

group = "com.mongodb.devicesync"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
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
    api("io.realm.kotlin:library-sync-jvm:${libsx.realm.sync.get().version}")
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
