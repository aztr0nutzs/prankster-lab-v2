# Production Blocker Closure Plan

Last updated: 2026-06-11

Source audits: `docs/FINAL_PRODUCTION_READINESS_AUDIT.md` and `docs/REMAINING_BLOCKERS.md`.

## Current Critical Blockers Extracted From Audit

### Missing packaged Twak-Attacks header and Twak Bot video resources

Severity: Critical in the source audit.

Evidence: the audit listed `app/src/main/res/drawable/twak_attack_header.png` and `app/src/main/res/raw/twakbot_idle.mp4`, `twakbot_searching.mp4`, `twakbot_generating.mp4`, `twakbot_excited.mp4`, and `twakbot_error.mp4` as missing while root source files existed.

Exact files affected:

- `app/src/main/res/drawable/twak_attack_header.png`
- `app/src/main/res/raw/twakbot_idle.mp4`
- `app/src/main/res/raw/twakbot_searching.mp4`
- `app/src/main/res/raw/twakbot_generating.mp4`
- `app/src/main/res/raw/twakbot_excited.mp4`
- `app/src/main/res/raw/twakbot_error.mp4`
- `app/src/main/java/com/pranksterlab/components/twak/TwakAttackHeader.kt`
- `app/src/main/java/com/pranksterlab/components/twak/TwakBotVideo.kt`
- `app/src/main/java/com/pranksterlab/core/narration/TwakBotMood.kt`

Fix plan: copy the root Twak assets into the expected Android resource names without deleting the root originals or replacing existing Prankstar Bot clips.

Test required to close: verify resource symbols in the generated `R.txt`, run clean debug build, run unit tests, and run device QA for visible Twak header and bot moods.

Status: Packaged and statically closed. Device visual proof remains blocked because no device is attached.

### Missing exact packaged `reactor4.mp4` asset path

Severity: High in the source audit, treated as a production blocker because Stable V9 reactor auditability depends on the exact packaged path.

Evidence: `app/src/main/assets/prankstar/assets/reactor4.mp4` was missing while the HTML had a base64 embedded reactor 4 video and the repo root had `reactor4.mp4`.

Exact files affected:

- `app/src/main/assets/prankstar/assets/reactor4.mp4`
- `app/src/main/assets/prankstar/prankstar_new_home_bot_screen.html`

Fix plan: copy the real root `reactor4.mp4` into `app/src/main/assets/prankstar/assets/reactor4.mp4`; do not create placeholders.

Test required to close: exact path exists, clean debug build passes, and device QA proves reactor 4 playback.

Status: Exact packaged path fixed. Runtime reactor playback proof remains blocked because no device is attached.

### No current connected-device final runtime QA

Severity: Critical.

Evidence: `adb devices -l` returned only `List of devices attached` with no device rows on 2026-06-11. The captured output is `qa/blockerfix_device_status.txt`.

Exact files affected:

- `qa/screenshots/blockerfix_*.png` expected but not produced.
- `qa/blockerfix_runtime_logcat.txt` expected but not produced.
- `qa/blockerfix_runtime_summary.txt` expected but not produced.

Fix plan: connect an unlocked Android device or emulator, install `app/build/outputs/apk/debug/app-debug.apk`, execute the final runtime checklist, capture screenshots, and capture logcat.

Test required to close: successful install, launch, Home, Library, Voice Lab, Forge, Bot, Twak-Attacks, ElevenLabs missing-backend state, screenshots, and logcat summary with no fatal runtime failures.

Status: Still blocked; runtime QA was attempted but no device was attached.

### Release artifact unsigned/not Play-ready

Severity: Critical.

Evidence: `app/build/outputs/apk/release/app-release-unsigned.apk` is explicitly unsigned. `keytool -printcert -jarfile app/build/outputs/bundle/release/app-release.aab` reports `Not a signed jar file`.

Exact files affected:

- `app/build.gradle.kts`
- `gradle.properties`
- `local.properties`
- `app/build/outputs/apk/release/app-release-unsigned.apk`
- `app/build/outputs/bundle/release/app-release.aab`

Fix plan: add release signing configuration sourced from untracked local properties or environment variables and produce a signed upload AAB.

Test required to close: signed AAB generated, signature metadata verifies, and Play Console internal testing accepts the artifact.

Status: Still blocked; no signing secrets were invented or hardcoded.

### npm audit vulnerabilities unresolved or not risk-classified

Severity: High.

Evidence: initial audit reported 7 vulnerabilities, 3 high and 4 moderate. Non-force `npm audit fix` reduced this to 2 high vulnerabilities in `google-tts-api -> axios`; the remaining fix requires `npm audit fix --force` and a breaking install of `google-tts-api@0.0.6`.

Exact files affected:

- `package-lock.json`
- `qa/npm_audit.json`
- `qa/npm_audit.txt`
- `docs/NPM_AUDIT_RISK_REPORT.md`

Fix plan: apply safe non-breaking audit fixes; document remaining forced breaking remediation separately.

Test required to close: `npm audit` exits cleanly or the remaining dependency is removed from shipped/runtime scope and formally risk-accepted.

Status: Partially remediated and risk-classified. Remaining high vulnerability is still open.

### High APK/AAB media size risk not measured/actioned

Severity: High.

Evidence: after packaging Twak assets, the debug APK is 268,866,295 bytes, the release unsigned APK is 224,211,386 bytes, and the release AAB is 217,569,217 bytes.

Exact files affected:

- `app/src/main/assets/**`
- `app/src/main/res/**`
- `app/build/outputs/apk/debug/app-debug.apk`
- `app/build/outputs/apk/release/app-release-unsigned.apk`
- `app/build/outputs/bundle/release/app-release.aab`
- `docs/APP_SIZE_MEDIA_AUDIT.md`

Fix plan: measure largest assets and totals, then plan compression, duplicate removal, optional packs, or Play Asset Delivery. Do not delete or compress media in this task without explicit approval.

Test required to close: Play Console upload/pre-launch accepts artifact size and target devices can install/update reliably.

Status: Measured and documented. Optimization remains open.

### Remaining Bot AI runtime and test gaps

Severity: Medium in `docs/REMAINING_BLOCKERS.md`, but still relevant to production confidence.

Evidence: static wiring exists, but final runtime bot commands were not exercised because no device was attached; deeper unit coverage remains incomplete.

Exact files affected:

- `app/src/main/java/com/pranksterlab/components/bot/**`
- `app/src/main/java/com/pranksterlab/core/bot/**`
- `app/src/main/java/com/pranksterlab/screens/voice/VoiceJokeGeneratorScreen.kt`
- `app/src/test/java/com/pranksterlab/**`

Fix plan: add focused unit tests for safety refusals, recommendations, joke generation, and action routing; run device bot command checklist.

Test required to close: unit coverage for representative safe/refusal paths and device QA for playback, stop-all, navigation, recommendations, joke generation, and Twak handoff.

Status: Still open.
