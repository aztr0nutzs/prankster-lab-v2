# BUILD_REQUIREMENTS.md

## BUILD TARGET

Required successful command:
./gradlew assembleDebug

## REQUIRED TOOLCHAIN

Preferred versions:
- AGP 8.5.2
- Kotlin 1.9.24
- Compose Compiler 1.5.14
- Gradle 8.11.1

## REQUIRED REPOSITORIES

settings.gradle.kts must contain:
- google()
- mavenCentral()
- gradlePluginPortal()

## REQUIRED VALIDATION

Before claiming success:
- clean build
- assembleDebug
- verify APK generated
- verify assets packaged
- verify catalog integrity
