# Voice Lab Runtime Fix Report

## A. Files changed
- app/src/main/java/com/pranksterlab/core/voice/VoiceSynthesisEngine.kt
- app/src/main/java/com/pranksterlab/core/voice/AndroidTextToSpeechEngine.kt
- app/src/main/java/com/pranksterlab/screens/voice/VoiceJokeGeneratorScreen.kt

## B. TTS readiness behavior
- Readiness states now use explicit required names:
  - `INITIALIZING`
  - `READY`
  - `UNAVAILABLE`
  - `ERROR`
- Voice Lab Generate remains disabled unless readiness is `READY`.
- UI status transitions from initializing to ready/error based on readiness updates.

## C. Generation validation behavior
- Synthesis now requires readiness `READY` before starting.
- Synthesis start checks return code from `synthesizeToFile` and fails if not `SUCCESS`.
- Success is only reported after `onDone` callback and output validation.
- Failure is returned on `onError` callback.
- Timeout returns explicit failure when callback never arrives.
- Output file is validated for existence and byte length > 0 before reporting `GENERATED`.

## D. Preview player behavior
- Replaced unmanaged inline preview `MediaPlayer` usage with a managed preview player.
- Managed preview behavior:
  - stops any existing preview before new playback,
  - releases on completion,
  - releases on stop,
  - releases on screen dispose,
  - surfaces playback failure to UI status.

## E. Save behavior
- Save is blocked unless:
  - generated file exists,
  - generated file size > 0,
  - generation result marked success.
- Status becomes `SAVED` only after `GeneratedVoiceRepository.saveGeneratedVoice(...)` succeeds.
- Save failures route to `ERROR` with message.

## F. Build result
- `./gradlew assembleDebug --stacktrace` failed in this environment due to missing Android SDK path.
- Key failure:
  - `SDK location not found... set ANDROID_HOME or sdk.dir`

## G. Runtime test result
- Device/emulator runtime flow not executed in this environment (adb/sdk unavailable).

## H. Remaining limitations
- Full runtime verification (Generate/Preview/Stop/Save and Library playback of saved clip) requires Android SDK + adb + device/emulator.
