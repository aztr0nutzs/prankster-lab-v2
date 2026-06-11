# Twak-Attacks Visual Integration

## Asset handling

The Twak-Attacks binary assets are now packaged under the Android resource names resolved by the feature code. The root originals remain in place for source traceability and were not deleted.

Packaged mapping:

| Source asset | Final Android resource |
| --- | --- |
| `twak_attack_header.png` | `app/src/main/res/drawable/twak_attack_header.png` |
| `twakbot1.mp4` | `app/src/main/res/raw/twakbot_idle.mp4` |
| `twakbot2.mp4` | `app/src/main/res/raw/twakbot_searching.mp4` |
| `twakbot3.mp4` | `app/src/main/res/raw/twakbot_generating.mp4` |
| `twakbot4.mp4` | `app/src/main/res/raw/twakbot_excited.mp4` |
| `twakbot5.mp4` | `app/src/main/res/raw/twakbot_error.mp4` |

The default mapping is retained in code by resource name because the supplied filenames did not include more specific mood metadata beyond their numeric order.

## Header sizing decision

`TwakAttackHeader` renders `twak_attack_header` in a 104dp high, full-width rounded neon frame using `ContentScale.Fit`. The fallback UI remains in code as a defensive path, but the packaged resource is now present.

## UI location

The feature is integrated inside `VoiceJokeGeneratorScreen`, at the top of the existing Tweaker Geographic / Tweakographic Narrator card. It does not replace Stable V9 Home, Prankstar Bot / NEO, Library / Sound Stash, Sound Forge, Settings, boot sequence, bundled sounds, or the existing audio catalog.

## Twak Bot mood interactions

`TwakBotMood` maps feature states to MP4 clips:

| Mood | Clip |
| --- | --- |
| `IDLE` | `twakbot_idle` |
| `SEARCHING` | `twakbot_searching` |
| `GENERATING` | `twakbot_generating` |
| `EXCITED` | `twakbot_excited` |
| `ERROR` | `twakbot_error` |
| `REFUSAL` | `twakbot_error` |
| `SAVED` | `twakbot_excited` |
| `PREVIEWING` | `twakbot_excited` |

The Voice Lab flow updates the Twak Bot when the user edits prompt fields, generates local narration, calls ElevenLabs, previews audio, saves audio, hits validation refusal, or encounters API/TTS/save errors.

## ElevenLabs interaction

The dedicated Tweaker Geographic ElevenLabs voice remains `wV67xHKrIHTU0gtChZiQ`. The app still generates local narration text first. ElevenLabs is called only after the user taps **Generate British Narration**. API keys are read from existing configuration; no key is hardcoded. Generated MP3 files remain generated user content and are not added to bundled `sound_catalog.json`.

## Tests

Targeted unit tests cover:

- Twak Bot mood-to-resource mapping.
- Non-empty Tweaker Geographic narration generation.
- Empty action refusal.
- Unsafe impersonation refusal.
- Twak-Attacks and Tweaker Geographic bot parser commands.
- Dedicated ElevenLabs voice ID constant.

## Build result

Static and build checks completed on 2026-06-11:

- `app/build/intermediates/runtime_symbol_list/debug/processDebugResources/R.txt` contains `drawable twak_attack_header`.
- `app/build/intermediates/runtime_symbol_list/debug/processDebugResources/R.txt` contains `raw twakbot_idle`, `twakbot_searching`, `twakbot_generating`, `twakbot_excited`, and `twakbot_error`.
- `git diff --check`: passed with only pre-existing `.omx` CRLF warnings.
- `.\gradlew.bat clean assembleDebug --stacktrace --console=plain`: passed.
- `.\gradlew.bat testDebugUnitTest --stacktrace --console=plain`: passed.
- `python tools\validate_sound_catalog.py`: passed.
- `node tools\advanced_validate.cjs`: passed.

## Runtime QA result

Runtime device QA remains blocked because `adb devices -l` returned no attached devices on 2026-06-11. ElevenLabs end-to-end runtime QA still requires a device/emulator plus backend/API configuration. No screenshots or logcat captures were faked.
