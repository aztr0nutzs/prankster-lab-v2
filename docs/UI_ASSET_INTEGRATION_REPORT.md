# UI Asset Integration Report

## Preserved assets and identity

The Prankstar dark neon cyberpunk identity is preserved. This feature keeps:

- Existing Prankstar Bot MP4 mood system.
- Existing `PrankstarBotVideo` mascot component.
- Existing mood clips and static fallback behavior.
- Existing Sound Stash / Library concepts.
- Bundled sound catalog and audio assets.
- Voice Lab / Joke Gen.
- Sound Forge.
- Settings/System.
- Enhanced reactor components.
- Existing background/header assets.

No audio assets or `sound_catalog.json` were intentionally modified.

## New UI surfaces

- Home now shows a compact `PrankstarBotPanel` with:
  - robot video
  - message bubble
  - input field
  - send button
  - quick chips
  - real recommendation cards
  - generated joke preview
  - harmless prank plan preview
- Voice Lab includes a Bot Helper card that fills text locally while keeping Generate user-controlled.
- Settings includes Bot Assistant and Bot Suggestions toggles alongside Animated Bot.

## Accessibility basics

- Bot input and send actions expose content descriptions.
- Recommendation buttons expose sound-specific content descriptions.
- Touch targets use Material button/icon sizes where practical.

## Asset safety

The feature adds no WebView assistant UI, no cloud calls, no new audio files, and no fake sound rows.
