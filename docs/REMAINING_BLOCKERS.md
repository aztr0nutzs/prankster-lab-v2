# Remaining Blockers

Last updated: 2026-06-11

## Critical Production Blockers

### 1. Current device runtime QA is not complete

Severity: Critical

Affected areas: whole app, WebView home, Library, Voice Lab, Sound Forge, Bot AI, Twak-Attacks, Settings.

Evidence: `adb devices -l` returned no attached devices during the final audit. Captured output: `qa/blockerfix_device_status.txt`. No fresh screenshots or logcat could be captured.

Fix required: connect an unlocked Android device or emulator, install `app/build/outputs/apk/debug/app-debug.apk`, launch `com.pranksterlab`, complete the runtime checklist, and save screenshots plus logcat.

Test to close: fresh screenshots for Home, Library, Voice Lab, Twak-Attacks, Sound Forge, Bot, and Settings; logcat with no launch/playback/generation crashes; verified install and relaunch.

### 2. Release signing is not configured/proven

Severity: Critical

Affected areas: Play upload, internal testing, closed testing, production release.

Evidence: `assembleRelease` and `bundleRelease` pass, but `app-release-unsigned.apk` is unsigned and no signed Play-ready AAB was proven.

Fix required: configure release signing or Play App Signing upload key flow, produce a signed AAB, and verify signature metadata.

Test to close: signed release AAB is generated and accepted by Play Console internal testing.

### 3. Production ElevenLabs backend/auth/credits are not configured

Severity: Critical

Affected areas: premium British narrator, Twak-Attacks ElevenLabs narration, voice credits.

Evidence: release builds use `VOICE_GENERATION_MODE = "PRODUCTION_BACKEND"`, but `VOICE_BACKEND_BASE_URL` is empty/not proven and `VoiceJokeGeneratorScreen` passes `authTokenProvider = { null }`.

Fix required: deploy the backend proxy, configure `VOICE_BACKEND_BASE_URL`, wire authenticated app tokens, enforce entitlement and credits server-side, and keep provider keys server-only.

Test to close: backend integration tests and device QA prove authorized success, unauthenticated refusal, not-entitled refusal, out-of-credits refusal, rate limiting, and no Android-embedded provider key.

### 4. Google Play Billing and server purchase verification are not implemented

Severity: Critical

Affected areas: Pro, lifetime unlock, credit packs, restore purchases, entitlement state, refunds/revocations.

Evidence: product IDs and feature gates exist, but the entitlement repository is unconfigured and no real BillingClient purchase/restore/verification flow is implemented.

Fix required: add Play Billing, configure Play Console products, verify purchase tokens on the backend, persist server entitlements/credits, and handle restore/refund/revocation.

Test to close: sandbox purchase, restore, refund/revocation, credit spend, and entitlement refresh pass on device and backend.

## High Blockers

### 5. Release artifact size is high

Severity: High

Affected areas: Play delivery, install reliability, startup, media decoding, device storage.

Evidence: after packaging Twak assets, debug APK is 268,866,295 bytes, release unsigned APK is 224,211,386 bytes, and release AAB is 217,569,217 bytes.

Fix required: optimize/transcode media, remove duplicate bundled media where safe, and consider Play Asset Delivery or dynamic delivery for large optional media.

Test to close: Play Console upload/pre-launch report accepts artifact size and internal testers can install/update on target devices.

### 6. npm dependency vulnerabilities remain

Severity: High

Affected areas: JavaScript tooling/backend-adjacent dependency graph.

Evidence: initial `npm audit` reported 7 vulnerabilities: 3 high and 4 moderate. Non-force `npm audit fix` reduced this to 2 high vulnerabilities through `google-tts-api -> axios`. The remaining remediation requires `npm audit fix --force`, which would install `google-tts-api@0.0.6` and is a breaking change.

Fix required: replace/remove `google-tts-api` or run a dedicated dependency compatibility pass for the forced breaking remediation.

Test to close: `npm audit --omit=dev --audit-level=moderate` exits cleanly or remaining findings are documented as non-runtime/non-shipped risk.

## Medium Blockers

### 7. Store/legal checklist is not production-final

Severity: Medium

Affected areas: Play Console listing, privacy policy, Data Safety, content rating, pre-launch review.

Evidence: store listing, privacy inventory, privacy policy draft, and Play checklist exist, but the privacy policy still uses `privacy@example.com`, no live privacy URL is proven, and Play Data Safety/content rating/pre-launch review are not complete.

Fix required: publish reviewed privacy policy, complete Data Safety and content rating based on the final build, and review the Play pre-launch report.

Test to close: Play Console internal test artifact has completed Data Safety, content rating, privacy URL, and pre-launch report review.

### 8. Bot AI unit and runtime coverage are incomplete

Severity: Medium

Affected areas: native bot parser, safety, recommender, joke generation, action routing, Voice Lab handoff.

Evidence: static wiring exists, but dedicated tests are missing for safety refusals, recommender output, joke generation, and full action routing. Runtime bot commands were not tested in this audit because no device was attached.

Fix required: add focused unit tests and run a device checklist for the representative bot commands.

Test to close: unit tests cover safe commands and refusals, and device QA proves playback, stop-all, navigation, recommendations, joke generation, and Twak handoff.

### 9. Requested historic audit reports are absent

Severity: Medium

Affected areas: audit traceability.

Evidence: the following requested files were not present:

- `docs/CODEX_BROWSER_NO_DEVICE_QA_REPORT.md`
- `docs/SCREEN_FUNCTION_STATIC_TEST_MATRIX.md`
- `docs/STABLE_V9_WEBVIEW_BRIDGE_REPORT.md`
- `docs/LOCAL_WINDOWS_BUILD_RUNTIME_QA.md`

Fix required: recreate the remaining absent reports or mark them as intentionally superseded by `docs/FINAL_PRODUCTION_READINESS_AUDIT.md`. `docs/STABLE_V9_WEBVIEW_BRIDGE_REPORT.md` has been recreated.

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
- Twak-Attacks header and bot videos are packaged under expected Android resource names.
- `app/src/main/assets/prankstar/assets/reactor4.mp4` exists at the exact requested path.
- Targeted secret scan found no real committed provider key.
- `npm ci` passed.
- `npm run lint` passed after dependency install.
- `npm run build` passed after dependency install.
- Non-force `npm audit fix` removed the moderate npm findings.
- `git diff --check` passed with CRLF warnings only in existing `.omx` files.

## Blockers Closed In Latest Pass

### Twak-Attacks visual assets are now packaged

The following resources now exist:

- `app/src/main/res/drawable/twak_attack_header.png`
- `app/src/main/res/raw/twakbot_idle.mp4`
- `app/src/main/res/raw/twakbot_searching.mp4`
- `app/src/main/res/raw/twakbot_generating.mp4`
- `app/src/main/res/raw/twakbot_excited.mp4`
- `app/src/main/res/raw/twakbot_error.mp4`

Generated debug `R.txt` includes the corresponding `drawable` and `raw` symbols. Device screenshot proof remains part of the runtime QA blocker.

### `reactor4.mp4` exact packaged path is now fixed

`app/src/main/assets/prankstar/assets/reactor4.mp4` exists and was copied from the real root `reactor4.mp4`. Device playback proof remains part of the runtime QA blocker.
