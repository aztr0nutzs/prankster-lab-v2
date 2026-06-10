# Voice Lab Static Test Report

Last updated: 2026-06-10

## Scope

Static verification for the Voice Lab / Joke Gen Tweakographic Narrator integration.

## UI preservation

Preserved:

- `PrankstarHeader`
- `PrankstarBotVideo`
- Bot Helper
- synthetic preset list
- status card
- Android TextToSpeech generation path
- generated clip preview and stop controls
- Save to Stash flow
- cyberpunk glass/neon styling

Added:

- `Tweakographic Narrator` card between Bot Helper and Synthetic Presets
- Action and optional Setting inputs
- tone chips
- optional sound-search cue checkbox
- local narration preview
- `Send to Voice Lab`

## Static behavior verification

| Requirement | Static result |
| --- | --- |
| User can enter action | `fieldAction` state and `OutlinedTextField` added. |
| User can enter setting | `fieldSetting` state and `OutlinedTextField` added. |
| User can select tone | `TweakerGeographicTone.entries` chips added in wrapped rows. |
| User can generate narration | `TweakerGeographicNarrator.generate` called from `Generate Narration`. |
| Output preview visible | `fieldResult` preview card added. |
| Send to Voice Lab works | Sets existing `text`, `outputName`, and suggested voice preset. |
| Existing Generate Voice/Preview/Save remain | Existing controls are unchanged and still use `settings()`. |
| Unsafe prompts refuse cleanly | Refusal shows preview and status detail, does not enable handoff. |
| Bot video not blocked | New panel is in LazyColumn below existing video; no overlay added. |
| Mobile tappability | Tone chips wrap into rows; example actions are full-width buttons. |

## Build and validation

- Android SDK environment check: PASS.
- `git diff --check`: PASS with CRLF warnings only.
- `.\gradlew.bat testDebugUnitTest --stacktrace --console=plain`: PASS, no test sources.
- `.\gradlew.bat lintDebug --stacktrace --console=plain`: PASS.
- `.\gradlew.bat assembleDebug --stacktrace --console=plain`: PASS.

## Runtime QA gap

`adb devices` returned no attached devices, so tap, visual overlap, and generated-audio runtime checks still need a device or emulator.

## ElevenLabs Tweaker Geographic Static Check

- Dedicated voice ID constant added: `wV67xHKrIHTU0gtChZiQ`.
- API key is read via `BuildConfig.ELEVENLABS_API_KEY` from local Gradle/environment inputs; no key is stored in source.
- Voice Lab preserves Android TTS WAV generation and adds a user-confirmed ElevenLabs MP3 path for Tweaker Geographic.
- Preview uses local `MediaPlayer` file playback, which supports local MP3 paths in addition to existing WAV output.
- Save to Stash uses generated metadata with source, voice ID, format, and feature fields.
