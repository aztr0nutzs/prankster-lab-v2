# Release Build Report

Last updated: 2026-06-10

## Android Environment

- `android-env-check.ps1` completed.
- Repo `local.properties` contains `sdk.dir=C:\\Users\\Aztr0nutZs\\AppData\\Local\\Android\\Sdk`.
- `ANDROID_HOME` and `ANDROID_SDK_ROOT` resolve to `C:\\Users\\Aztr0nutZs\\AppData\\Local\\Android\\Sdk`.
- SDK directories verified: `platforms`, `platform-tools`, and `build-tools`.
- Note: the environment script also writes `C:\\Users\\Aztr0nutZs\\Desktop\\local.properties`; that is outside the repo and should be fixed in the script separately.

## Release Tasks

### assembleRelease

Command:

```powershell
.\gradlew.bat assembleRelease --stacktrace --console=plain
```

Result: PASS

Release APK:

- `app/build/outputs/apk/release/app-release-unsigned.apk`
- Size: 205,834,556 bytes / 196.30 MB

### bundleRelease

Command:

```powershell
.\gradlew.bat bundleRelease --stacktrace --console=plain
```

Result: PASS

Release AAB:

- `app/build/outputs/bundle/release/app-release.aab`
- Size: 199,976,779 bytes / 190.71 MB

## Signing Status

Release signing is not configured.

- APK output is explicitly unsigned.
- `keytool -printcert -jarfile app/build/outputs/bundle/release/app-release.aab` reported `Not a signed jar file`.

Before Play testing, configure an upload keystore and release signing config, preferably sourced from environment variables or local untracked properties.

## Build Warnings

- Java 8 source/target warning under JDK 21.
- Deprecated Compose auto-mirrored icons.
- Deprecated WebView file URL setters.
- Unused parameters in UI/generator code.

These did not block release assembly.

## Release BuildConfig Verification

Generated release BuildConfig contains:

```java
public static final boolean DEBUG = false;
public static final String ELEVENLABS_API_KEY = "";
```
