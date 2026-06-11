# Release Signing Readiness

Last updated: 2026-06-11

## Current Status

Release signing is not configured/proven. The project can assemble release artifacts, but they are not Play-ready.

## Files Inspected

- `app/build.gradle.kts`
- `gradle.properties`
- `local.properties`
- `README.md`
- `docs/RELEASE_BUILD_REPORT.md`
- `docs/PRODUCTION_RELEASE_HARDENING.md`

## Evidence

- `app/build.gradle.kts` defines `debug` and `release` build types but no release `signingConfig`.
- `gradle.properties` contains no upload keystore variables.
- `local.properties` contains only `sdk.dir`.
- `app/build/outputs/apk/release/app-release-unsigned.apk` exists and is explicitly unsigned.
- `keytool -printcert -jarfile app/build/outputs/bundle/release/app-release.aab` reports `Not a signed jar file`.

## Current Artifacts

| Artifact | Status | Size |
| --- | --- | ---: |
| `app/build/outputs/apk/debug/app-debug.apk` | Debug signed | 268,866,295 bytes |
| `app/build/outputs/apk/release/app-release-unsigned.apk` | Unsigned | 224,211,386 bytes |
| `app/build/outputs/bundle/release/app-release.aab` | Signing not proven; `keytool` reports not signed | 217,569,217 bytes |

## Recommended Keystore Configuration

Use untracked local properties, Gradle properties, or environment variables. Do not commit secrets.

Recommended variable names:

- `PRANKSTER_RELEASE_STORE_FILE`
- `PRANKSTER_RELEASE_STORE_PASSWORD`
- `PRANKSTER_RELEASE_KEY_ALIAS`
- `PRANKSTER_RELEASE_KEY_PASSWORD`

Recommended Gradle behavior:

- Load variables from environment first, then untracked local Gradle properties if needed.
- Only attach the release `signingConfig` when all variables are present.
- Fail a Play upload/release task clearly when signing variables are missing.

## Play Upload Blocker Status

Blocked. A signed upload AAB has not been generated or accepted by Play Console internal testing.
