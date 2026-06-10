# Prankstar Bot AI Inspection

Last updated: 2026-06-10

## Scope

Static inspection and local build verification of the native bot parser, safety layer, recommender, action routing, Voice Lab exposure, and Tweaker Geographic handoff.

## Current status

The bot remains a local command parser and local response generator. It does not use cloud AI, scraping, creator imitation, or external media APIs. Voice generation is still user-controlled in Voice Lab.

## Native bot route

- Native bot parser/controller/recommender/safety code exists in `app/src/main/java/com/pranksterlab/core/bot/`.
- Voice Lab hosts `PrankstarBotPanel` backed by `PrankstarBotController`.
- Recommended sounds can be played from Voice Lab via the shared `AudioPlayerController`.
- Stop All routes to `audioPlayerController.stopAll()` and stops Voice Lab preview/TTS preview.
- Generated bot text can be sent into the Voice Lab text field with the suggested preset.
- Navigation actions route to Stash and Forge through the app nav controller.
- Unsafe requests continue to produce refusal actions.

## Twak / Tweakographic routes

- `twak attack` remains a harmless local Voice Lab text generation command.
- Tweaker Geographic commands route through `TweakerGeographicNarrator` and return a Voice Lab handoff.
- Supported aliases include `twak attack`, `twak attacks`, `twak-attack`, `twak-attacks`, `twak attack narration`, `tweaker geographic`, `tweakographic`, `field report`, `mock documentary`, and `urban wildlife narration`.
- The bot can suggest **Generate with British Narrator**, but it only fills/routes narration text; ElevenLabs generation remains user-confirmed in Voice Lab.

## Safety inspection

The existing `PrankstarBotSafety` runs before intent handling. The Tweakographic narrator adds local prompt filtering for violence, weapons, stalking, harassment, hidden recording, doxxing, emergency/government themes, impersonation, voice-copying, likely private full names, phone numbers, emails, and street addresses.

## Command coverage

- `find creepy sounds`: recommendation action.
- `show animal sounds`: recommendation action.
- `play something funny`: recommendation and optional playback action.
- `make a joke about being late`: generated text action.
- `make a twak attack about fixing a bike`: generated local draft action.
- `make a tweakographic field report`: Tweaker Geographic handoff action.
- `open stash`: navigation action.
- `open forge`: navigation action.
- `stop all`: stop action.
- Emergency impersonation request: refusal action.

## Verification

- Previous recorded `testDebugUnitTest`, `lintDebug`, and `assembleDebug` checks passed.
- Runtime bot tap QA remains pending because no device/emulator was attached in the recorded environment.
