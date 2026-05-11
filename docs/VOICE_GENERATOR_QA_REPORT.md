# Voice Generator QA Report

Last updated: 2026-05-11

## Scope

This report covers Voice Lab / Joke Gen runtime hardening and current QA status. It does not claim live TTS success because no emulator/device was attached during the latest runtime QA pass.

## Voice Preset Count

Source:

- `app/src/main/java/com/pranksterlab/core/voice/VoicePreset.kt`

Static count:

- `52` `VoicePreset(...)` entries

## Generation Behavior

Voice generation uses Android `TextToSpeech` through:

- `app/src/main/java/com/pranksterlab/core/voice/AndroidTextToSpeechEngine.kt`

Current behavior:

- exposes readiness states: `Initializing`, `Ready`, `Unavailable`, `Error(message)`
- Generate is disabled until TTS is `Ready`
- synthesis waits for TTS initialization
- synthesis checks `synthesizeToFile` return code
- success is reported only after `UtteranceProgressListener.onDone`
- errors are reported from `onError`
- synthesis times out if completion never arrives
- output file is rejected if missing or zero bytes
- output label is honest: `WAV/PCM`

## Preview Behavior

Preview uses the local Android TTS engine. Preview is gated by TTS readiness and does not use cloud TTS.

Runtime preview was not verified in the latest pass because no emulator/device was attached.

## Save Behavior

Generated voice clips are saved through:

- `app/src/main/java/com/pranksterlab/core/voice/GeneratedVoiceRepository.kt`
- `app/src/main/java/com/pranksterlab/core/repository/SoundRepository.kt`

Saved generated voice clips use:

- category `VOICE_GENERATED`
- pack ID `voice_lab`
- `SoundSourceType.GENERATED`
- local file path in `assetPath` and `localUri`
- tags `generated`, `voice`, `joke`, `custom`
- source text and voice preset metadata when available

The UI status is `GENERATED` after synthesis and changes to `SAVED TO LIBRARY` only after repository save succeeds.

## Known TTS Limitations

- Android TTS engine availability varies by device.
- Language support varies by installed engine and user settings.
- Output quality and exact file container are engine-dependent.
- TTS cannot be verified without a device/emulator.
- No cloud TTS has been added.
- No MP3 export is claimed.

## Safety Guardrails

- Voice presets are synthetic styling presets.
- The UI warns against real-person or official-alert impersonation.
- Generation blocks obvious emergency/official-alert text such as `police` and `emergency`.
- The app does not impersonate copyrighted or real voices.

## QA Status

- Static source audit: PASS
- Debug build through PowerShell wrapper: PASS
- Catalog validators: PASS
- Live TTS generation/preview/save: BLOCKED, no device/emulator available

## Remaining Voice Lab Work

- Run on a physical device or emulator with a working TTS engine.
- Confirm generation creates non-empty audio files.
- Confirm preview starts/stops cleanly.
- Confirm saved generated clips appear in Library after app restart.
- Confirm unavailable TTS displays the error panel rather than crashing.
