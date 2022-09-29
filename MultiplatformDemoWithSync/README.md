## Kotlin Multiplatform Sync demo App using a shared business logic:

The demo demonstrates [Sync](https://www.mongodb.com/realm/mobile/sync) capability between an Android, iOS, macOS and JVM (currently Mac & Linux).

<img src="./Screenshots/kotlin-sync-demo.gif" width="800">

# Steps to build:

## 1 - Create a Realm Sync App on MongoDB Atlas

- Follow the tutorial at https://www.mongodb.com/docs/atlas/tutorial/create-atlas-account/ or watch the screencast https://www.youtube.com/watch?v=lqo0Yf7lnyg

- Replace the App identifier and the created user/password in [shared/src/commonMain/kotlin/io/realm/kotlin/demo/util/Constants.kt](./shared/src/commonMain/kotlin/io/realm/kotlin/demo/util/Constants.kt)

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

If running on Apple Silicon, Rosetta 2 is currently required. It can be installed by running this
from the terminal:

```
softwareupdate -–install-rosetta -–agree-to-license
```

You will also need to change the architecture of the ios app. This is done this way:

1. Open `iosApp.xcworkspace`.
2. Select `iosApp` in the project view.
3. Select `Build Settings`
4. Change the `architectures` field from `$(ARCHS_STANDARD)` to `x86_64` 

It will also be impossible to run the iosApp from Android Studio. Instead it should be launched
from XCode. 

## 4 - Build and run for macOS

```
./gradlew shared:podInstall
cd macosApp
pod install
open macosApp.xcworkspace
```

If running on Apple Silicon, Rosetta 2 is currently required. It can be installed by running this
from the terminal:

```
softwareupdate -–install-rosetta -–agree-to-license
```

You will also need to select the `My Mac (Rosetta)` run target instead of the default `My Mac` in
XCode.


## 5 - Build and run for JVM

```
./gradlew :jvmApp:run
```

