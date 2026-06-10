# Tweakographic Narrator Feature

Last updated: 2026-06-10

## Scope

Tweakographic Narrator is an original local mock-documentary narration generator for Voice Lab / Joke Gen. It does not scrape, call cloud APIs, copy creators, impersonate real people, or require network access.

The user-facing name is `Tweakographic Narrator`. The Kotlin model names use `TweakerGeographic*` because the implementation request used that phrase.

## Files changed

- `app/src/main/java/com/pranksterlab/core/narration/TweakerGeographicTone.kt`
- `app/src/main/java/com/pranksterlab/core/narration/TweakerGeographicRequest.kt`
- `app/src/main/java/com/pranksterlab/core/narration/TweakerGeographicResult.kt`
- `app/src/main/java/com/pranksterlab/core/narration/TweakerGeographicNarrator.kt`
- `app/src/main/java/com/pranksterlab/screens/voice/VoiceJokeGeneratorScreen.kt`
- `app/src/main/java/com/pranksterlab/core/bot/PrankstarBotIntent.kt`
- `app/src/main/java/com/pranksterlab/core/bot/PrankstarBotCommandParser.kt`
- `app/src/main/java/com/pranksterlab/core/bot/PrankstarBotController.kt`
- `app/src/main/java/com/pranksterlab/core/bot/PrankstarBotResponseBuilder.kt`
- `docs/BOT_ASSISTANT_ACTIONS.md`
- `docs/PRANKSTAR_BOT_AI_INSPECTION.md`
- `docs/VOICE_LAB_STATIC_TEST_REPORT.md`
- `docs/REMAINING_BLOCKERS.md`
- `docs/TWEAKER_GEOGRAPHIC_FEATURE.md`

## Assets moved or copied

None. The feature uses existing Voice Lab UI, existing bot video integration, existing voice presets, and optional existing sound-search keywords only.

## Generator behavior

| Input | Behavior |
| --- | --- |
| Action | Required. Sanitized to one line and capped at 120 characters. |
| Setting | Optional. Sanitized to one line and capped at 80 characters. |
| Tone | `MILD`, `BALANCED`, `CHAOTIC`, `SUSPENSEFUL`, or `DRAMATIC`. |
| Sound cue | Optional text keyword for the existing sound stash search flow. No audio auto-plays. |
| Output | Deterministic local 2 to 4 sentence narration under the Voice Lab text limit. |
| Voice handoff | Suggests an existing synthetic preset such as `overly_serious_narrator`, `tiny_documentary_voice`, or `dramatic_whisper`. |

## Safety behavior

The generator refuses prompts involving:

- targeted real-person references or likely full names
- violence, weapons, threats, or swatting
- emergency, government, or official-alert themes
- impersonation or real-person voice copying
- stalking, harassment, hidden recording, doxxing, addresses, phone numbers, or email addresses

Refusals return a clean preview result with `safetyNote` instead of failing or filling Voice Lab with unsafe text.

## Voice Lab integration

The new panel appears inside the existing Voice Lab / Joke Gen screen after Bot Helper and before synthetic presets. It preserves:

- existing Prankstar bot video area
- existing status text
- existing synthetic voice presets
- existing Generate Voice Clip, Preview Clip, Stop Preview, and Save to Stash controls
- existing local Android TextToSpeech engine

The panel adds:

- Action input
- Setting input
- Tone chips
- Optional sound-search cue checkbox
- Generate Narration
- Output preview
- Send to Voice Lab

`Send to Voice Lab` only fills the existing text box and suggested preset. Voice generation remains manual and user-controlled.

## Bot integration

New intent:

```text
GenerateTweakerGeographic(action, setting?, tone)
```

Supported prompt shapes include:

- `make a tweaker geographic about looking for a lighter`
- `make a tweakographic field report about protecting the last slice near the fridge`
- `make a chaotic field report about hunting for a charger in the couch cushions`
- `mock documentary about checking every drawer`

The bot controller generates locally, returns a visible message, and fills Voice Lab through `FillVoiceLabText`. It does not auto-generate audio and does not auto-play sounds.

## Verification results

| Check | Result |
| --- | --- |
| Android SDK environment check | PASS. `sdk.dir`, `ANDROID_HOME`, `ANDROID_SDK_ROOT`, `platforms`, `platform-tools`, and `build-tools` are valid. |
| `git diff --check` | PASS with Git CRLF normalization warnings only. |
| `python tools\validate_sound_catalog.py` | PASS, 369 catalog entries. |
| `node tools\advanced_validate.cjs` | PASS, 369 files. |
| `npm run lint` | PASS. |
| `npm run build` | PASS. |
| `.\gradlew.bat testDebugUnitTest --stacktrace --console=plain` | PASS. No test sources exist, task reported `NO-SOURCE`. |
| `.\gradlew.bat lintDebug --stacktrace --console=plain` | PASS. |
| `.\gradlew.bat assembleDebug --stacktrace --console=plain` | PASS. APK generated at `app/build/outputs/apk/debug/app-debug.apk`. |
| `.\gradlew.bat clean assembleDebug --stacktrace --console=plain` | BLOCKED by Windows file lock during `:app:clean` on `app/build/.../R.jar`; plain `assembleDebug` passed immediately afterward. |
| `adb devices` | No attached devices or emulators. Runtime tap/screenshot QA not executed. |

## Known limitations

- No new JVM unit tests were added because the project currently has no `app/src/test` test sources and no test dependencies.
- Bot parser extracts simple settings after `in`, `at`, `near`, `inside`, or `outside`; complex grammar is intentionally out of scope.
- Optional sound pairing is a stash search suggestion only. It does not auto-select or auto-play audio.
- Manual device QA remains required to visually confirm the panel on real screen sizes.

## Manual QA checklist

- Open Voice Lab / Joke Gen and confirm the bot video, header, presets, and voice controls remain visible.
- Generate a mild narration for `looking for a lighter` and send it to Voice Lab.
- Generate a chaotic narration with setting `near the couch`.
- Confirm the suggested preset changes when sending to Voice Lab.
- Generate Voice Clip, Preview Clip, Stop Preview, and Save to Stash from the loaded narration.
- Try a blocked real-person or emergency prompt and confirm refusal text appears without filling Voice Lab.
- Ask the bot: `make a tweakographic field report about hunting for a charger near the couch`.
- Confirm the bot fills Voice Lab but does not auto-generate or auto-play.

## ElevenLabs British Narrator Addendum

Tweaker Geographic now has a dedicated narration path using voice ID `wV67xHKrIHTU0gtChZiQ`. The generated narration text remains local until the user explicitly taps **Generate British Narration**. MP3 output is written to `filesDir/generated/elevenlabs/` and saved to Stash through generated voice metadata; it is not added to bundled `sound_catalog.json`.

Android narration generation is now routed through `NarrationVoiceProvider`:

- `LOCAL_ONLY` keeps users on existing Android TextToSpeech generation.
- `DEBUG_ELEVENLABS_DIRECT` preserves the local/test ElevenLabs flow for debug builds with a locally configured key.
- `PRODUCTION_BACKEND` is the release-safe path and calls the future app backend instead of ElevenLabs directly. The Android request uses backend feature ID `twak_attacks`; saved local metadata can still tag the generated clip as `tweaker_geographic`.

Release builds do not embed the ElevenLabs key. Backend entitlement, credits, and app auth remain required before production premium narration can be enabled.

## Twak-Attacks Visual Addendum

The Tweaker Geographic / Tweakographic Narrator card now includes a dedicated Twak-Attacks header and feature-specific Twak Bot video avatar. The code resolves the expected asset names dynamically (`twak_attack_header`, `twakbot_idle`, `twakbot_searching`, `twakbot_generating`, `twakbot_excited`, and `twakbot_error`) and shows neon fallback UI until those binary resources are added by a separate asset-only PR.

The visual integration is scoped to Voice Lab / Joke Gen only. It does not replace the global Prankstar header, Prankstar Bot, existing robot videos, Sound Stash, Forge, Settings, boot sequence, or bundled audio catalog.

Twak Bot mood changes are driven by the existing user-controlled flow: prompt editing, local narration generation, ElevenLabs generation, preview, save, errors, and safety refusals.
