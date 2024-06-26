plugins {
    java
    application
}

group = "com.mongodb.devicesync"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(project(":kotlin-lib"))
}

application {
    mainClass.set("com.mongodb.devicesync.javainterop.gradle.JavaGradleApp")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}
