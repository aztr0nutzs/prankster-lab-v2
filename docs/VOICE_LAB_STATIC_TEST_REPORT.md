# Voice Lab Static Test Report

Date: 2026-06-09

Scope: Static inspection only. Android TextToSpeech runtime, generated file playback, and MediaPlayer decode were not verified without device/emulator.

## Files Inspected

- `app/src/main/java/com/pranksterlab/screens/voice/VoiceJokeGeneratorScreen.kt`
- `app/src/main/java/com/pranksterlab/core/voice/AndroidTextToSpeechEngine.kt`
- `app/src/main/java/com/pranksterlab/core/voice/GeneratedVoiceRepository.kt`
- `app/src/main/java/com/pranksterlab/core/voice/VoiceGeneratorSettings.kt`
- `app/src/main/java/com/pranksterlab/core/voice/VoicePreset.kt`
- `app/src/main/java/com/pranksterlab/core/repository/SoundRepository.kt`
- `app/src/main/java/com/pranksterlab/screens/LibraryScreen.kt`

## TTS Readiness Status

Static status: PASS.

- `AndroidTextToSpeechEngine` exposes readiness states.
- `VoiceJokeGeneratorScreen` collects readiness.
- Generate is enabled only when:
  - readiness is `READY`,
  - text is non-blank,
  - status is not `GENERATING`.
- UI status reports initializing, ready, unavailable, and error states.

Device-only limitation:

- Actual TextToSpeech engine availability depends on the Android device image and installed TTS engine.

## Generation Validation Status

Static status: PASS.

- Voice Lab blocks blank text and restricted emergency/official-alert terms before generation.
- Output file path uses `context.filesDir` and `.wav` extension.
- `AndroidTextToSpeechEngine.synthesizeToFile()` waits for `UtteranceProgressListener` completion/error with timeout.
- Output validation requires file exists and length > 0.
- Result format label is `WAV/PCM`, avoiding false MP3 labeling.
- Duration is read through `MediaMetadataRetriever` when possible.
- Failure states keep generated file unusable.

## Preview Player Status

Static status: PASS.

- Generated clip preview uses `ManagedPreviewPlayer` with file existence/size preflight.
- It uses `MediaPlayer.prepareAsync()`.
- It releases on stop, completion, error, and screen disposal.
- Stop Preview stops both generated preview player and TextToSpeech preview.
- Voice style preview uses TTS `speak()` only when readiness is `READY`.

Device-only limitation:

- Actual decode/playback quality requires Android runtime QA.

## Save-to-Stash Status

Static status: PASS.

- Save button requires valid generated file and successful generation result.
- `GeneratedVoiceRepository.saveGeneratedVoice()` rejects missing/empty files.
- Saved `PrankSound` uses:
  - `id = voice_...`
  - `category = VOICE_GENERATED`
  - `packId = voice_lab`
  - `assetPath/localUri = file.absolutePath`
  - `sourceType = GENERATED`
  - `isCustom = true`
  - `createdByUser = true`
  - generated metadata including source text and voice settings.
- `SoundRepository.saveCustomSound()` persists the generated sound metadata in DataStore.
- Library reads custom/generated sounds through repository custom sounds flow and has Generated/Voice Lab filters.

## Bot Handoff Status

Static status: PASS.

- `PrankstarBotVoiceLabBridge.pendingDraft` exists.
- Home bot can submit text and suggested preset ID.
- Voice Lab consumes pending draft, fills the text field, applies the preset, and updates status.
- Voice Lab also has local Bot Helper buttons for “Make Line” and “Robot”.

## Known Weaknesses / Device-Only Limitations

- No unit tests found for generated metadata shape or repository save/load.
- TTS engine behavior cannot be verified without Android runtime.
- TTS output acoustic quality cannot be verified statically.
- MediaPlayer preview cannot be verified without device/emulator.
- Permission/storage edge cases were not exercised; current implementation writes to app-private files.

## Recommended Tests

1. Generated metadata unit test for `GeneratedVoiceRepository` with a temp non-empty `.wav` fixture.
2. Voice preset safety test ensuring real-person voice clone wording is absent from presets/UI.
3. TTS readiness UI test with fake `VoiceSynthesisEngine` abstraction if refactored.
4. Preview player instrumentation/Robolectric test for missing/empty file rejection.
5. Library generated filter test.
