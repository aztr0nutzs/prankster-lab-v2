# VOICE_LAB_STATUS.md

Last updated: 2026-06-01

## Current Role

Voice Lab is now presented through the Jokes dock tab as the main voice meme clip generator.

## Implemented

- Synthetic voice preset selection remains intact with more than 40 presets.
- Text input is capped at 300 characters and labeled as "Type a joke".
- Pitch, speed, volume, effect amount, and echo/reverb controls remain available.
- Generation is disabled until Android TTS reports ready.
- TTS unavailable state is shown in the header/status area.
- Generated output is treated as local WAV/PCM.
- Save is separated from generation. Status reaches `GENERATED` after synthesis and `SAVED` only after repository save succeeds.
- Empty or missing generated files cannot be saved.
- File preview stops and releases any previous MediaPlayer before starting a new preview.
- Preview player is released on completion, stop, error, and screen dispose.
- Saved clips use `packId = voice_lab`, category `VOICE_GENERATED`, generated/custom source metadata, and tags `generated`, `voice`, `joke`, `custom`.

## Remaining Runtime Checks

- Generate a spoken line on a physical device/emulator with TTS installed.
- Preview and stop preview repeatedly to confirm no leaks or overlapping playback.
- Save to Stash and confirm the clip appears immediately.
- Restart app and confirm the saved clip persists.
