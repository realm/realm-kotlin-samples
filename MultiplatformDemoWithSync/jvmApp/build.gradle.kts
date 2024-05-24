plugins {
    alias(libsx.plugins.kotlinJvm)
    alias(libsx.plugins.jetbrainsCompose)
    alias(libsx.plugins.compose.compiler)
    application
}

repositories {
    mavenCentral()
    google()
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(project(":shared"))
}

application {
    mainClass.set("io.realm.kotlin.demo.MainKt")
}
