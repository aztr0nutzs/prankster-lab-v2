# Prankstar Bot AI Inspection

Last updated: 2026-06-10

## Current status

The bot remains a local command parser and local response generator. It does not use cloud AI, scraping, creator imitation, or external media APIs.

## New Tweakographic route

Files:

- `app/src/main/java/com/pranksterlab/core/bot/PrankstarBotIntent.kt`
- `app/src/main/java/com/pranksterlab/core/bot/PrankstarBotCommandParser.kt`
- `app/src/main/java/com/pranksterlab/core/bot/PrankstarBotController.kt`
- `app/src/main/java/com/pranksterlab/core/narration/TweakerGeographicNarrator.kt`

Flow:

1. Parser detects `tweaker geographic`, `tweakographic`, `field report`, `mock documentary`, `documentary narration`, or `urban wildlife narration`.
2. Parser extracts tone words and simple settings after `in`, `at`, `near`, `inside`, or `outside`.
3. Controller sends a `TweakerGeographicRequest` to the local narrator.
4. Safety refusal returns `Refuse` plus `ShowMessage`.
5. Allowed output returns `ShowMessage` plus `FillVoiceLabText`; audio generation remains manual in Voice Lab.

## Safety inspection

The existing `PrankstarBotSafety` still runs before intent handling. The Tweakographic narrator adds local prompt filtering for:

- violence and weapons
- stalking, harassment, hidden recording, and doxxing
- emergency or government themes
- impersonation or voice-copying
- likely private full names
- phone numbers, emails, and street addresses

## UI action inspection

The bot route does not create fake buttons or dead actions. It uses existing action types:

- `ShowMessage`
- `FillVoiceLabText`
- `Refuse` for blocked requests

It does not auto-navigate to Voice Lab, auto-play sounds, or auto-generate TTS audio.

## Verification

- `.\gradlew.bat testDebugUnitTest --stacktrace --console=plain`: PASS, no test sources.
- `.\gradlew.bat lintDebug --stacktrace --console=plain`: PASS.
- `.\gradlew.bat assembleDebug --stacktrace --console=plain`: PASS.
- Runtime bot tap QA is pending because `adb devices` returned no attached devices.

## ElevenLabs Tweaker Geographic Bot Handoff

Bot Tweaker Geographic commands now prefer a Voice Lab handoff with the British Narrator path flagged. The bot response can suggest **Generate with British Narrator**, but it only fills/routes the narration text; ElevenLabs generation remains user-confirmed in Voice Lab.

## Twak-Attacks command aliases

The native Bot AI parser now treats Twak-Attacks commands as Tweaker Geographic feature requests. Supported aliases include `twak attack`, `twak attacks`, `twak-attack`, `twak-attacks`, and `twak attack narration`, in addition to the existing Tweakographic / field report / urban wildlife phrasing.

The Twak Bot remains feature-specific inside Voice Lab. Prankstar Bot remains the general app assistant and only routes text or navigation actions; it does not automatically call ElevenLabs.
