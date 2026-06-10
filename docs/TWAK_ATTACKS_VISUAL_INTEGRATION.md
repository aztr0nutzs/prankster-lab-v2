# Twak-Attacks Visual Integration

## Asset handling

This PR intentionally does **not** include binary Twak-Attacks assets. The feature code resolves the expected Android resource names dynamically and falls back to neon static UI when the resources are not packaged. This keeps the code PR reviewable and lets the binary files ship in a separate asset-only PR.

Expected asset-only PR mapping:

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

When `twak_attack_header` is packaged by the asset PR, `TwakAttackHeader` renders it in a 104dp high, full-width rounded neon frame using `ContentScale.Fit`. Until then, the component shows a neon text fallback so the code-only PR remains buildable without binary resources.

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

Static checks completed in this environment:

- `git diff --check`: passed.
- `python tools/validate_sound_catalog.py`: passed.
- `node tools/advanced_validate.cjs`: passed.

Gradle unit tests and `assembleDebug` were blocked by the container Android SDK environment. `scripts/android-env-check.sh` reported no SDK from `ANDROID_HOME`, `ANDROID_SDK_ROOT`, or common Linux paths, while `local.properties` points to a Windows SDK path not present in the container.

## Runtime QA result

Runtime device QA was blocked because `adb` is not installed in the container. ElevenLabs end-to-end runtime QA still requires a device/emulator plus a locally configured `ELEVENLABS_API_KEY`. No screenshots or logcat captures were faked.
