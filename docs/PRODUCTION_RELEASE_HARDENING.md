# Production Release Hardening

Last updated: 2026-06-10

## Summary

Production hardening pass completed for the Android release configuration, manifest security posture, secret handling, generated-file storage, and release build tasks.

## Release Configuration

- `applicationId`: `com.pranksterlab`
- `namespace`: `com.pranksterlab`
- `versionCode`: `1`
- `versionName`: `1.0`
- `minSdk`: `26`
- `targetSdk`: `34`
- `compileSdk`: `34`
- Release minification: enabled
- Release resource shrinking: enabled
- Release ProGuard/R8 config: `app/proguard-rules.pro` plus Android default optimized rules
- Media no-compress rules: `mp3`, `ogg`, `oga`, `wav`, `m4a`, `aac`, `flac`, `opus`, `amr`, `mp4`
- Release ElevenLabs key: intentionally empty in `BuildConfig`

## Manifest Hardening

- Removed source manifest `package` attribute; Gradle namespace remains authoritative.
- `android:allowBackup` is now `false`.
- `android:usesCleartextTraffic` is now `false`.
- App permission declared directly by the project: `android.permission.INTERNET`.
- Merged release manifest also includes dependency-provided `android.permission.ACCESS_NETWORK_STATE`.
- Launcher `MainActivity` is exported because it owns the `MAIN`/`LAUNCHER` intent filter.
- AndroidX Startup provider is exported `false`.
- AndroidX ProfileInstaller receiver is exported by dependency with `android.permission.DUMP`; documented as a dependency exception.

## Secret Handling

- `local.properties` is ignored and was removed from Git tracking with `git rm --cached`.
- `.env*` is ignored while `.env.example` remains tracked.
- Debug builds may receive `ELEVENLABS_API_KEY` from Gradle property, environment, or local properties.
- Release builds always compile `BuildConfig.ELEVENLABS_API_KEY = ""`.
- Production ElevenLabs behavior is therefore a missing-key gate until a backend/proxy integration is approved.

## Storage And Generated Files

- Local Voice Lab WAV files are generated under the app files directory.
- ElevenLabs MP3 narration files are generated under `filesDir/generated/elevenlabs`.
- Sound Forge WAV files are generated under `filesDir/generated_sounds`.
- Generated voice clips can be deleted from Settings through `deleteGeneratedSounds`.
- Delete logic only removes generated files under `context.filesDir`.
- Generated/custom sound display names are now normalized before persistence.
- Generated WAV metadata remains `wav`; ElevenLabs metadata remains `mp3`.

## Crash / Analytics Prep

Crashlytics or equivalent is not integrated in the Android Gradle dependencies. Recommended follow-up:

1. Add Firebase Crashlytics and Analytics through approved dependencies.
2. Add consent-aware analytics events for generate, preview, save, playback failure, TTS missing key, and generated-file cleanup.
3. Update Play Data Safety and privacy disclosures before enabling analytics collection.
4. Add release runtime monitoring for WebView/media decoder warnings, TTS failures, generated-file growth, and playback errors.

## Android Vitals Risks

- Large packaged media assets produce a large APK/AAB and may affect install size.
- Multiple MP4/WebView-backed screens need device coverage across GPU/decoder combinations.
- TTS runtime behavior varies by device engine and locale.
- ElevenLabs network calls can fail by key state, rate limit, network state, or service response.
- Generated audio files can grow app storage if users do not use cleanup actions.
