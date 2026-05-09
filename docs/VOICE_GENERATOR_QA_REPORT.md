# Voice Generator QA Report

Date: 2026-05-09 (UTC)
Project: Prankster Lab Android
Scope: Final QA pass for Voice Joke Generator replacing Sequencer

## Build result
- **Command**: `./gradlew clean assembleDebug --stacktrace`
- **Result**: **FAIL**
- **Root cause**: Android SDK is not available in this environment.
  - `scripts/build-android-debug.sh` failed with: `ERROR: No Android SDK found via ANDROID_HOME/ANDROID_SDK_ROOT or common paths.`
  - Gradle failed with: `SDK location not found` and `sdk.dir ... Directory does not exist`.

## Voice preset count
- Source: `app/src/main/java/com/pranksterlab/core/voice/VoicePreset.kt`
- **Total presets**: 52
- **Preset ID uniqueness**: PASS (no duplicate IDs found by static check)
- **Category coverage**: PASS (all `VoiceCategory` enum values represented in `VoicePresetLibrary.presets`)

## Generation test result
- **Status**: BLOCKED (runtime/device/emulator required)
- **Static observations**:
  - Voice generation route exists and points to `VoiceJokeGeneratorScreen`.
  - Engine class exists (`AndroidTextToSpeechEngine`) and returns `WAV/PCM` format label.
- **Limitation**: No Android runtime / no SDK / no emulator in this environment, so TTS initialization and file generation could not be executed.

## Preview test result
- **Status**: BLOCKED (runtime/device/emulator required)
- **Limitation**: Media playback lifecycle behavior (play/stop/repeat leak checks) requires device/emulator execution.

## Save test result
- **Status**: BLOCKED (runtime/device/emulator required)
- **Static observations**:
  - Generated voice persistence path exists via `GeneratedVoiceRepository` into `SoundRepository`.
- **Limitation**: Could not verify actual file existence post-save or persistence after app restart without runtime execution.

## Library integration result
- **Status**: PARTIAL (static PASS, runtime BLOCKED)
- **Static observations**:
  - Library screen contains generated voice metadata rendering hooks.
- **Limitation**: Could not validate interactive playback/visibility in running app.

## Cross-feature integration result
- **Status**: PARTIAL (static PASS, runtime BLOCKED)
- **Static observations**:
  - Randomizer has generated-voice include toggle logic.
  - App navigation includes `voice_lab` destination.
- **Limitation**: Timer, Reactor, Packs, and diagnostics integration require runtime verification.

## Safety result
- **Status**: PARTIAL PASS
- **Static observations**:
  - Warning text present in Voice Lab screen indicating synthetic styling presets.
  - Preset names/descriptions do not reference real-person or celebrity voice cloning presets.
- **Not verified at runtime**:
  - Full UX visibility on all screen sizes/state paths.

## Validator results
- `python3 tools/validate_sound_catalog.py`: PASS (369 entries validated)
- `node tools/advanced_validate.cjs`: PASS (369 files checked)

## Known limitations
1. No Android SDK available (`ANDROID_HOME`/`ANDROID_SDK_ROOT` unset, local `sdk.dir` points to non-existent Windows path).
2. No emulator/device runtime available in this environment.
3. Therefore, required manual/runtime QA checks (navigation tap flow, TTS generation, preview stop/replay behavior, save-and-restart persistence, Reactor/Timer end-to-end usage) are blocked.
