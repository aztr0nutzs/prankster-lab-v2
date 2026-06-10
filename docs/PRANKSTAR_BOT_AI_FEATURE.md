# Prankstar Bot AI Feature

Last updated: 2026-06-07

## Summary

Prankstar Bot is a local deterministic assistant layer. It does not call a cloud model and does not auto-send messages. It parses simple commands, searches real `PrankSound` objects from `SoundRepository`, generates safe template-based joke text, and returns explicit UI actions for playback, navigation, Voice Lab handoff, and stop-all.

## Supported commands

Examples now supported:

- `find creepy sounds`
- `show animal sounds`
- `play something funny`
- `make a joke about being late`
- `create a robot announcement`
- `open stash`
- `open jokes`
- `open forge`
- `open system`
- `stop all`

The parser also recognizes `recommend`, `random`, `surprise me`, `voice`, `say`, `plan`, `prank`, and vibe words including `creepy`, `funny`, `animal`, `voice`, `chaos`, `office`, `robot`, `scary`, and `prank`.

## Sound recommendations

`PrankstarBotSoundRecommender` scores real sounds only. It searches:

- name
- category
- tags
- pack ID
- description
- preview label
- prank style

The recommender expands vibe keywords. For example, `creepy` expands to horror/ghost/scary-style tokens, while `robot` expands to sci-fi/glitch/machine/bot tokens. Results are de-duplicated by sound ID and shown as playable cards.

## Joke and comment generation

`PrankstarBotJokeGenerator` uses local templates only. It sanitizes mild insults and generates harmless lines such as robot announcements, office announcements, dramatic narrator bits, creepy whispers, and light roasts.

Generated text is never auto-spoken or auto-sent. Voice Lab generation remains user-controlled.

## Voice Lab handoff

The handoff uses `PrankstarBotVoiceLabBridge`, a shared in-process pending draft store:

1. Bot action `FillVoiceLabText(text, suggestedVoicePresetId)` stores a pending draft.
2. `VoiceJokeGeneratorScreen` collects `pendingDraft`.
3. When a draft appears, the screen fills the input text and applies the suggested preset if present.
4. The screen consumes the draft.
5. The user must still tap Generate.

The Home bot panel also exposes a `Voice Lab` button for generated text, and command-driven `ChooseVoice` actions can navigate to Voice Lab with the draft already queued.

## Safe actions

Supported safe actions:

- play a real recommended sound through `AudioPlayerController`
- stop all audio through `AudioPlayerController.stopAll()`
- navigate to Stash/Library
- navigate to Jokes/Voice Lab
- navigate to Forge
- navigate to System
- show generated text
- show a harmless prank plan

## Safety behavior

The bot refuses threats, harassment, emergency or government impersonation, real-person voice impersonation, phone-number spoofing, automatic or secret message sending, illegal activity, non-consensual recording, and dangerous pranks. Refusals use `PrankstarBotMood.WARNING` and redirect toward safe sound or joke requests.

## UI integration

The native Home/Core screen shows `PrankstarBotPanel` with:

- input box
- send button
- quick chips
- real sound recommendation cards with Play buttons
- generated joke card with Voice Lab handoff
- stop-all behavior

Voice Lab exposes a compact bot helper for local line generation. Stable V9 WebView Home was preserved and not covered by a giant native overlay.

## Validation

- `.\gradlew.bat assembleDebug --stacktrace --console=plain`: passed.
- `python tools\validate_sound_catalog.py`: passed, 369 entries.
- `node tools\advanced_validate.cjs`: passed, 369 files checked.

## Runtime QA

No device was attached through ADB, so runtime bot interaction screenshots were not captured.
