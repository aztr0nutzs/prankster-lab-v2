# Remaining Blockers

Date: 2026-06-09

This file separates verified static/cloud blockers from device-only blockers. No runtime/device success is claimed.

## Build Blockers

1. **Android SDK missing in current environment**
   - Evidence: `./scripts/android-env-check.sh` failed with `ERROR: No Android SDK found via ANDROID_HOME/ANDROID_SDK_ROOT or common paths.`
   - `local.properties` points to a Windows SDK path: `C:\Users\Aztr0nutZs\AppData\Local\Android\Sdk`.
   - `ANDROID_HOME` and `ANDROID_SDK_ROOT` are empty.
   - Required SDK folders could not be confirmed.
   - Recommendation: install Android SDK in the container or run on a host with valid SDK, then rerun `./scripts/build-android-debug.sh`.

2. **Gradle build not run after failed SDK check**
   - This follows project `AGENTS.md`: do not run Gradle unless SDK validation passes.
   - Recommendation: after SDK validation passes, run `./gradlew assembleDebug --stacktrace --console=plain` or the project build script.

3. **No APK produced in this pass**
   - Expected path: `app/build/outputs/apk/debug/app-debug.apk`.
   - Recommendation: rerun build after SDK setup and record APK size.

## Runtime / Device Blockers

1. **ADB unavailable**
   - Evidence: `adb devices` returned `adb: command not found`.
   - Recommendation: install platform-tools or run on a machine with `adb`, then install/launch debug APK.

2. **No physical device/emulator attached**
   - No install, launch, tapping, logcat, screenshots, WebView rendering, or audio playback runtime QA was performed.
   - Recommendation after build: `adb install -r app/build/outputs/apk/debug/app-debug.apk`, launch package `com.pranksterlab`, capture logcat, and perform smoke flows.

## Screen / UI Blockers

1. **Primary Stable V9 WebView does not expose full native bot assistant input statically**
   - Native `PrankstarBotPanel` exists on fallback `home_native`.
   - Stable V9 HTML has bot videos/visual controls, but no full native bot command bridge/input was found.
   - Recommendation: if required, add a narrow WebView bridge for bot command submission and render results in existing Stable V9 cyberpunk UI; do not replace Stable V9 UI.

2. **Secondary routes are not all primary-dock reachable**
   - `lab`, `randomizer`, and `messages` are defined but not on the 5-tab dock.
   - Recommendation: confirm intended reachability; add small in-app links only if product requires, preserving UI.

3. **Runtime touch/scroll/a11y not verified**
   - Recommendation: device/emulator QA pass.

## Audio Blockers

1. **Runtime playback not verified**
   - Validators passed, but actual MediaPlayer decode/loudness/audio focus behavior needs device QA.

2. **No automated audio tests found**
   - Recommendation: add catalog count/path/duplicate tests and generated sound metadata tests.

## Stable V9 Bridge Blockers

1. **Missing checklist external reactor assets**
   - Missing files:
     - `app/src/main/assets/prankstar/assets/reactor1.mp4`
     - `app/src/main/assets/prankstar/assets/reactor3.mp4`
     - `app/src/main/assets/prankstar/assets/reactor4.mp4`
   - Current HTML did not parse references to those external files, likely due to embedded/base64 or omitted external slots.
   - Recommendation: product decision. If checklist requires all seven files regardless of embedding, restore the missing external files without modifying HTML design.

2. **Runtime bridge calls not verified**
   - Recommendation: device WebView smoke test and/or add bridge unit tests with fake controller/repository.

## Bot AI Blockers

1. **No bot automated tests found**
   - Recommended files/tests:
     - `PrankstarBotCommandParserTest`
     - `PrankstarBotSafetyTest`
     - `PrankstarBotSoundRecommenderTest`
     - `PrankstarBotMoodResourceTest`

2. **Recommendations are not strict safe-only in every path**
   - Recommender gives safe sounds a score bonus but can return unsafe sounds if matching strongly.
   - Recommendation: if safe mode should govern bot recommendations, inject/read safe mode and filter where appropriate.

3. **Stable V9 bot/native bot feature split**
   - Default home has WebView visual bot, fallback native home has richer text bot.
   - Recommendation: clarify intended UX and bridge only the missing command input/result path if needed.

4. **No runtime persistence/history verification**
   - Recommendation: add state persistence or document that bot chat is session-only.

## Voice Lab Blockers

1. **Android TextToSpeech runtime not verified**
   - Requires device/emulator with TTS engine.

2. **Generated clip preview/save not runtime verified**
   - Static checks are strong, but device QA must confirm `synthesizeToFile`, file decode, preview, and Library appearance.

3. **No Voice Lab automated tests found**
   - Recommendation: generated metadata save/load test and fake-engine generation state test.

## Exact File-Level Recommendations

- `app/src/main/assets/prankstar/assets/reactor1.mp4`: restore if external seven-reactor checklist is mandatory.
- `app/src/main/assets/prankstar/assets/reactor3.mp4`: restore if external seven-reactor checklist is mandatory.
- `app/src/main/assets/prankstar/assets/reactor4.mp4`: restore if external seven-reactor checklist is mandatory.
- `app/src/main/java/com/pranksterlab/bridge/PrankstarWebBridge.kt`: add tests/fakes around route and deploy behavior; no code change required from this static pass.
- `app/src/main/java/com/pranksterlab/core/bot/PrankstarBotSoundRecommender.kt`: consider safe-mode filtering for bot recommendations.
- `app/src/main/java/com/pranksterlab/screens/PrankstarStableHomeWebViewScreen.kt` and Stable V9 HTML: only if required, add native bot command bridge while preserving current UI.
- `app/src/test/...`: add parser/safety/recommender/catalog/generated metadata tests once test framework is confirmed.
