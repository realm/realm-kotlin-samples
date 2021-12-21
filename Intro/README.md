# Realm KMM Sample

A _Kotlin (KMM) Project_ showing usage of Realm Kotlin in a shared module of a
multiplatform project for both Android and iOS.

The example is based on the Kotlin Multiplatform Mobile Project from
https://github.com/Kotlin/kmm-sample/blob/master/README.md

## Requirements

- JDK 11
- Android Studio [Bumblebee (2021.1.1) Beta 5](https://developer.android.com/studio/preview)
- NOTE: The SDK doesn't currently support `x86` - Please use an `x86_64` or `arm64` emulator/device

## Overview

The Realm Kotlin Multiplatform SDK is used to provide a common implementation of an
`ExpressionRepository` for storing a computation history of calculations performed in the respective
apps. The repository is implemented once in the `commonMain` source set of the `shared`-module and
is triggered by the shared `Calculator`-implementation from the original KMM-sample project.

## References

For instructions on developing Kotlin Multiplatform Mobile Project, visit
[Kotlin Multiplatform Mobile Developer Portal](https://kotlinlang.org/lp/mobile/).
