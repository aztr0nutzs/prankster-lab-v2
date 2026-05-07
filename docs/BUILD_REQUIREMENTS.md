# BUILD_REQUIREMENTS.md

Last updated: 2026-05-07

## REQUIRED BUILD COMMAND

Release candidate debug validation must run:

```powershell
.\gradlew.bat clean assembleDebug --stacktrace --console=plain
```

Unix/macOS equivalent:

```bash
./gradlew clean assembleDebug --stacktrace --console=plain
```

## CURRENT BUILD RESULT

Date:
2026-05-07

Result:
PASS

Artifact:
`app/build/outputs/apk/debug/app-debug.apk`

## REQUIRED TOOLCHAIN

Current project configuration:
- Android Gradle Plugin from root Gradle plugin management
- Kotlin Android plugin
- Compose Compiler: 1.5.14
- Compile SDK: 34
- Minimum SDK: 26
- Target SDK: 34
- DataStore Preferences: 1.0.0
- Gson: 2.10.1

Local QA environment:
- JDK 21 detected
- Gradle wrapper functional

## REQUIRED REPOSITORIES

`settings.gradle.kts` must contain:
- `google()`
- `mavenCentral()`
- `gradlePluginPortal()`

## CURRENT WARNINGS

Warnings that do not fail debug build but should be triaged:
- `AndroidManifest.xml` still contains `package="com.pranksterlab"` although Gradle namespace is authoritative.
- Java source/target 8 emits deprecation warnings under JDK 21.
- Some Kotlin warnings remain for unused parameters and deprecated auto-mirrored icons.

## RELEASE BUILD CHECKS

Before release:
- run clean debug build
- run audio validators
- confirm APK exists
- install APK on emulator or physical device
- run manual flow checklist
- capture crash-free smoke test notes
