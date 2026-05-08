# Android Build Agent Instructions

This project is an Android project. Before attempting any Gradle build, the agent MUST verify the Android SDK environment.

## Required build process

1. Do not run Gradle blindly.
2. First run the Android environment check script.
3. Confirm `local.properties` exists and contains a valid `sdk.dir`.
4. Confirm `ANDROID_HOME` or `ANDROID_SDK_ROOT` points to a real SDK directory.
5. Confirm the SDK contains:
   - `platforms/`
   - `platform-tools/`
   - `build-tools/`
6. Only then run the Gradle build.

## Linux/macOS command

```bash
chmod +x scripts/build-android-debug.sh
./scripts/build-android-debug.sh