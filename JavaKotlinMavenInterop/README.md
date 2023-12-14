# Java-Kotlin-Maven Interop Sample

This sample shows how a Java project using either Gradle or Maven can use Realm Kotlin.

This can be a problem for multiple reasons:

- The main app is written in Java and do not want to introduce Kotlin code directly into 
  that codebase.

- The main app is using Maven as a build system, which is incompatible with Kotlin 
  Multiplatform as it only supports Gradle.

In order to work around this we introduce a seperate Gradle Module containing the Realm code,
and then that module will expose an interface that is consumable from Java. This module will
then be publish to either MavenLocal or some other Nexus as this will allow the Java app to
find it regardless of it using Gradle or Maven.

In this sample project we call the abstraction `RealmRepository` which expose a CRUD like 
interface, but it should be modified to fit the context of the project.


## Requirements

- JDK 11
- The project can be opened in IntelliJ, which will support both Gradle and Maven in the same project, making it possible
  to edit all modules from there.

## How to Run

1. Deploy the Kotlin library to Maven Local using:

   ```
   >./gradlew :kotlin-lib:publishToMavenLocal

   ```

2. Run the Java App using Gradle:

   ```
   >./gradlew :java-app-gradle:run

   ```

3. Run the Java App using Maven:

   ```
   > cd java-app-maven
   > mvn compile exec:java

   ```


## Publish The Kotlin library

The Kotlin library contains a skeleton setup for deploying both to MavenLocal as well as a custom Nexus. You deply to 
each by using:

```shell
# Publish to Maven Local
> ./gradlew :kotlin-lib:publishToMavenLocal

# Publish to a custom nexus
> ./gradlew :kotlin-lib:publishAllPublicationsToCustomNexusRepository
```

Note, you need to setup credentials and URLs for the custom nexus inside the the [build.gradle.kts](kotlin-lib/build.gradkle.kts) file.
The artifacts are not signed, and this will need to be added if required by the Nexus. 


## Consume from Maven

Once deployed, the Kotlin library can be consumed like any other Maven artifact:

```xml
<project>
	<!-- ... -->
    <dependencies>
        <dependency>
            <groupId>com.mongodb.devicesync</groupId>
            <artifactId>kotlin-lib</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
	<!-- ... -->
</project>
```

or Gradle:

```gradle
dependencies {
    implementation("com.mongodb.devicesync:kotlin-lib:1.0-SNAPSHOT")
}
```



