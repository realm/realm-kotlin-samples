buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
        classpath("com.android.tools.build:gradle:4.1.0")
    }
}
group = "io.realm.example"
version = "0.4.0"

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter() // Required by detekt
    }
}
