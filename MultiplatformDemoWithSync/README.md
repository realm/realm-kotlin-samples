## Kotlin Multiplatform Sync demo App using a shared business logic:

The demo demonstrates [Device Sync](https://www.mongodb.com/atlas/app-services/device-sync) 
running between an Android, iOS, macOS and JVM app.

<img src="./Screenshots/kotlin-sync-demo.gif" width="800">

# Steps to build:

## 1 - Create a Realm Sync App on MongoDB Atlas

- Follow the tutorial at https://www.mongodb.com/docs/atlas/tutorial/create-atlas-account/ or watch 
  the screencast https://www.youtube.com/watch?v=lqo0Yf7lnyg.

- The App must be configured for Flexible Sync with Dev Mode enabled.

- A user must be created  under "App Users > Add New User". A fake email and password like "foo@bar.com" and "123456" can be used.

- Insert the app identifier and the created user/password in 
  [shared/src/commonMain/kotlin/io/realm/kotlin/demo/util/Constants.kt](./shared/src/commonMain/kotlin/io/realm/kotlin/demo/util/Constants.kt)

## 2 - Build and run for Android

```
 ./gradlew :androidApp:installDebug
```

## 3 - Build and run for iOS

```
./gradlew shared:podInstall
cd iosApp
pod install
open iosApp.xcworkspace
```

## 4 - Build and run for macOS

```
./gradlew shared:podInstall
cd macosApp
pod install
open macosApp.xcworkspace
```

## 5 - Build and run for JVM

```
./gradlew :jvmApp:run
```

