## Kotlin Multiplatform demo App using a shared business logic:

The demo works on Android, iOS, macOS and JVM (currently Mac & Linux).

<img src="./Screenshots/Overview.png" width="800">

## Requirements

- JDK 11
- Android Studio [Bumblebee (2021.1.1) Beta 5](https://developer.android.com/studio/preview)
- NOTE: The SDK doesn't currently support `x86` - Please use an `x86_64` or `arm64` emulator/device

# Build and run for iOS

```
./gradlew shared:podInstall
cd iosApp
pod install
open iosApp.xcworkspace
```

# Build and run for macOS

```
./gradlew shared:podInstall
cd macosApp
pod install
open macosApp.xcworkspace
```

# Build and run for JVM

```
./gradlew :jvmApp:run
```

# Build and run for Android

```
 ./gradlew :androidApp:installDebug
```
