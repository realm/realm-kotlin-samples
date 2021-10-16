buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        classpath("com.android.tools.build:gradle:4.1.3")
    }
}
group = "io.realm.example"
version = "0.4.1"

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
