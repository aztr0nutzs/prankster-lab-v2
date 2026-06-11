# Remaining Blockers

Last updated: 2026-06-11

## Critical Production Blockers

### 1. Current device runtime QA is not complete

Severity: Critical

Affected areas: whole app, WebView home, Library, Voice Lab, Sound Forge, Bot AI, Twak-Attacks, Settings.

Evidence: `adb version` succeeds, but `adb devices -l` returned no attached devices during the final audit. No fresh screenshots or logcat could be captured.

Fix required: connect an unlocked Android device or emulator, install `app/build/outputs/apk/debug/app-debug.apk`, launch `com.pranksterlab`, complete the runtime checklist, and save screenshots plus logcat.

Test to close: fresh screenshots for Home, Library, Voice Lab, Twak-Attacks, Sound Forge, Bot, and Settings; logcat with no launch/playback/generation crashes; verified install and relaunch.

### 2. Twak-Attacks visual assets are not packaged under app resource names

Severity: Critical

Affected areas: `TwakAttackHeader`, `TwakBotVideo`, `TwakBotMood`, Voice Lab Twak-Attacks section.

Evidence: these expected resources are missing:

- `app/src/main/res/drawable/twak_attack_header.png`
- `app/src/main/res/raw/twakbot_idle.mp4`
- `app/src/main/res/raw/twakbot_searching.mp4`
- `app/src/main/res/raw/twakbot_generating.mp4`
- `app/src/main/res/raw/twakbot_excited.mp4`
- `app/src/main/res/raw/twakbot_error.mp4`

Root-level source files exist (`twak_attack_header.png`, `twakbot1.mp4` through `twakbot5.mp4`), but they are not imported into Android resources under the names the app resolves.

Fix required: map the root Twak assets into the expected Android resource paths, rebuild, and verify the Twak UI renders each mood video instead of fallback content.

Test to close: exact path check passes, `TwakBotMoodTest` resolves nonzero raw resource IDs, and device screenshots show the Twak header plus idle/searching/generating/excited/error states.

### 3. Release signing is not configured/proven

Severity: Critical

Affected areas: Play upload, internal testing, closed testing, production release.

Evidence: `assembleRelease` and `bundleRelease` pass, but `app-release-unsigned.apk` is unsigned and no signed Play-ready AAB was proven.

Fix required: configure release signing or Play App Signing upload key flow, produce a signed AAB, and verify signature metadata.

Test to close: signed release AAB is generated and accepted by Play Console internal testing.

### 4. Production ElevenLabs backend/auth/credits are not configured

Severity: Critical

Affected areas: premium British narrator, Twak-Attacks ElevenLabs narration, voice credits.

Evidence: release builds use `VOICE_GENERATION_MODE = "PRODUCTION_BACKEND"`, but `VOICE_BACKEND_BASE_URL` is empty/not proven and `VoiceJokeGeneratorScreen` passes `authTokenProvider = { null }`.

Fix required: deploy the backend proxy, configure `VOICE_BACKEND_BASE_URL`, wire authenticated app tokens, enforce entitlement and credits server-side, and keep provider keys server-only.

Test to close: backend integration tests and device QA prove authorized success, unauthenticated refusal, not-entitled refusal, out-of-credits refusal, rate limiting, and no Android-embedded provider key.

### 5. Google Play Billing and server purchase verification are not implemented

Severity: Critical

Affected areas: Pro, lifetime unlock, credit packs, restore purchases, entitlement state, refunds/revocations.

Evidence: product IDs and feature gates exist, but the entitlement repository is unconfigured and no real BillingClient purchase/restore/verification flow is implemented.

Fix required: add Play Billing, configure Play Console products, verify purchase tokens on the backend, persist server entitlements/credits, and handle restore/refund/revocation.

Test to close: sandbox purchase, restore, refund/revocation, credit spend, and entitlement refresh pass on device and backend.

## High Blockers

### 6. Release artifact size is high

Severity: High

Affected areas: Play delivery, install reliability, startup, media decoding, device storage.

Evidence: debug APK is about 238.4 MiB, release unsigned APK is about 196.3 MiB, and release AAB is about 190.7 MiB.

Fix required: optimize/transcode media, remove duplicate bundled media where safe, and consider Play Asset Delivery or dynamic delivery for large optional media.

Test to close: Play Console upload/pre-launch report accepts artifact size and internal testers can install/update on target devices.

### 7. `reactor4.mp4` is missing from the exact packaged asset path

Severity: High

Affected areas: Stable V9 reactor auditability and media packaging.

Evidence: `app/src/main/assets/prankstar/assets/reactor4.mp4` is missing. The HTML embeds reactor 4 as base64 video, but the exact asset requested for packaging is absent.

Fix required: either package `reactor4.mp4` at the expected path and reference it normally, or document and test the intentional base64 exception.

Test to close: path check passes or explicit exception is approved, and device QA proves reactor 4 playback.

### 8. npm dependency vulnerabilities remain

Severity: High

Affected areas: JavaScript tooling/backend-adjacent dependency graph.

Evidence: `npm audit --omit=dev --audit-level=moderate` reports 7 vulnerabilities: 3 high and 4 moderate. High findings include `google-tts-api -> axios` and `protobufjs`.

Fix required: run a dependency compatibility pass. The `google-tts-api` remediation path may be semver-major and should be tested carefully.

Test to close: `npm audit --omit=dev --audit-level=moderate` exits cleanly or remaining findings are documented as non-runtime/non-shipped risk.

## Medium Blockers

### 9. Store/legal checklist is not production-final

Severity: Medium

Affected areas: Play Console listing, privacy policy, Data Safety, content rating, pre-launch review.

Evidence: store listing, privacy inventory, privacy policy draft, and Play checklist exist, but the privacy policy still uses `privacy@example.com`, no live privacy URL is proven, and Play Data Safety/content rating/pre-launch review are not complete.

Fix required: publish reviewed privacy policy, complete Data Safety and content rating based on the final build, and review the Play pre-launch report.

Test to close: Play Console internal test artifact has completed Data Safety, content rating, privacy URL, and pre-launch report review.

### 10. Bot AI unit and runtime coverage are incomplete

Severity: Medium

Affected areas: native bot parser, safety, recommender, joke generation, action routing, Voice Lab handoff.

Evidence: static wiring exists, but dedicated tests are missing for safety refusals, recommender output, joke generation, and full action routing. Runtime bot commands were not tested in this audit because no device was attached.

Fix required: add focused unit tests and run a device checklist for the representative bot commands.

Test to close: unit tests cover safe commands and refusals, and device QA proves playback, stop-all, navigation, recommendations, joke generation, and Twak handoff.

### 11. Requested historic audit reports are absent

Severity: Medium

Affected areas: audit traceability.

Evidence: the following requested files were not present:

- `docs/CODEX_BROWSER_NO_DEVICE_QA_REPORT.md`
- `docs/SCREEN_FUNCTION_STATIC_TEST_MATRIX.md`
- `docs/STABLE_V9_WEBVIEW_BRIDGE_REPORT.md`
- `docs/LOCAL_WINDOWS_BUILD_RUNTIME_QA.md`

Fix required: recreate them or mark them as intentionally superseded by `docs/FINAL_PRODUCTION_READINESS_AUDIT.md`.

Test to close: documentation index or audit packet clearly points to current replacements.

## Non-Blocking Polish

- Align app label `PranksterLab` with final public brand naming.
- Remove deprecated Material icon usages.
- Replace deprecated WebView file URL access APIs where possible.
- Clean unused parameters and unnecessary null assertion warnings.
- Review the `panic_llama` sample line containing `Emergency!` before store submission.
- Add crash reporting only after privacy/Data Safety disclosures are updated.
- Continue monitoring generated file storage growth and cleanup behavior during device QA.

## Checks Passed In Final Audit

- Android SDK environment check passed before Gradle.
- `local.properties` contains a valid `sdk.dir`.
- `ANDROID_HOME` and `ANDROID_SDK_ROOT` point to the SDK.
- `gradlew.bat clean assembleDebug --stacktrace --console=plain` passed.
- `gradlew.bat testDebugUnitTest --stacktrace --console=plain` passed.
- `python tools\validate_sound_catalog.py` passed with 369 entries.
- `node tools\advanced_validate.cjs` passed with 369 files.
- `gradlew.bat assembleRelease --stacktrace --console=plain` passed.
- `gradlew.bat bundleRelease --stacktrace --console=plain` passed.
- Targeted secret scan found no real committed provider key.
- `npm ci` passed.
- `npm run lint` passed after dependency install.
- `npm run build` passed after dependency install.
- `git diff --check` passed with CRLF warnings only in existing `.omx` files.
