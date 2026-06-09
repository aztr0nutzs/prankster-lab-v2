# Prankstar Bot AI Inspection

Date: 2026-06-09

Scope: Static inspection only. No runtime Compose interaction, WebView interaction, ADB, or screenshots were available.

## Files / Classes Present

| Expected component | Status | File |
|---|---|---|
| `PrankstarBotIntent` | Present | `app/src/main/java/com/pranksterlab/core/bot/PrankstarBotIntent.kt` |
| `PrankstarBotAction` | Present | `app/src/main/java/com/pranksterlab/core/bot/PrankstarBotAction.kt` |
| `PrankstarBotState` | Present | `app/src/main/java/com/pranksterlab/core/bot/PrankstarBotState.kt` |
| `PrankstarBotMessage` | Present | `app/src/main/java/com/pranksterlab/core/bot/PrankstarBotMessage.kt` |
| `PrankstarBotController` | Present | `app/src/main/java/com/pranksterlab/core/bot/PrankstarBotController.kt` |
| `PrankstarBotCommandParser` | Present | `app/src/main/java/com/pranksterlab/core/bot/PrankstarBotCommandParser.kt` |
| `PrankstarBotResponseBuilder` | Present | `app/src/main/java/com/pranksterlab/core/bot/PrankstarBotResponseBuilder.kt` |
| `PrankstarBotSafety` | Present | `app/src/main/java/com/pranksterlab/core/bot/PrankstarBotSafety.kt` |
| `PrankstarBotSoundRecommender` | Present | `app/src/main/java/com/pranksterlab/core/bot/PrankstarBotSoundRecommender.kt` |
| `PrankstarBotJokeGenerator` | Present | `app/src/main/java/com/pranksterlab/core/bot/PrankstarBotJokeGenerator.kt` |
| `PrankstarBotVoiceLabBridge` | Present | `app/src/main/java/com/pranksterlab/core/bot/PrankstarBotVoiceLabBridge.kt` |
| `PrankstarBotPanel` | Present | `app/src/main/java/com/pranksterlab/components/bot/PrankstarBotPanel.kt` |
| `PrankstarBotVideo` | Present | `app/src/main/java/com/pranksterlab/components/bot/PrankstarBotVideo.kt` |
| `PrankstarBotMood` | Present | `app/src/main/java/com/pranksterlab/components/bot/PrankstarBotMood.kt` |

## Visibility / Entry Points

- Native bot assistant input exists in `HomeScreen` via `PrankstarBotPanel`.
- Voice Lab includes a Bot Helper input that locally generates lines and robot announcements.
- Stable V9 WebView home includes bot videos/controls but no static evidence of a full native bot text-command input bridge.
- Because `home` defaults to Stable V9 WebView, the full native bot panel appears to be on fallback `home_native`, not the primary default home.

## Supported Command Coverage

Parser keyword coverage statically supports:

| User command | Parser result / behavior | Status |
|---|---|---|
| `find creepy sounds` | `SearchSounds("creepy")`; recommender uses creepy/scary tokens | PASS static |
| `show animal sounds` | `SearchSounds("animal")`; recommender uses animal tokens | PASS static |
| `play something funny` | `PlayRecommended("funny")`; auto-play first recommended action | PASS static |
| `make a joke about being late` | `GenerateJoke("being late")` | PASS static |
| `create a robot announcement` | `GenerateJoke(...)` with robot-announcement template intent | PASS static |
| `open stash` | `OpenStash` -> route `library` | PASS static |
| `open jokes` | `OpenJokes` -> route `voice_lab` | PASS static |
| `open forge` | `OpenForge` -> route `forge` | PASS static |
| `stop all` | `StopAll` -> `StopAllSounds` action | PASS static |
| `open system` / `settings` | `OpenSystem` -> route `system` | PASS static |
| `random` / `surprise me` | `PlayRandom` safe random action | PASS static |
| `plan` / `prank plan` | `BuildPrankPlan` with safety note | PASS static |

## Recommender Status

- `PrankstarBotSoundRecommender` scores real `PrankSound` fields: name, category, pack ID, description, preview label, prank style, and tags.
- It returns `PrankSound` objects from the provided repository/catalog list; no fake sound titles were found in the recommender.
- Results are distinct by `id` and limited to 3-6 sounds.
- Weakness: ordinary recommendations are not strict safe-only filters. Safe sounds receive +1 score, but unsafe matching sounds can still appear. Random deploy uses safe filtering.

## Actions / UI Mapping

Actions found:

- `ShowMessage`
- `ShowSoundRecommendations`
- `PlaySound`
- `StopAllSounds`
- `Navigate`
- `FillVoiceLabText`
- `ShowPrankPlan`
- `Refuse`

`PrankstarBotPanel` mappings:

- Submit button sends typed command to `onSubmit`.
- Suggestion chips include find funny, find creepy, make joke, stop all, open stash, and send to Voice Lab where relevant.
- Recommended sound cards include Play, Favorite, and Open Stash controls.
- Generated text includes Voice Lab handoff and copy action.
- Stop All button exists.

`HomeScreen` mappings:

- `PlaySound` calls `AudioPlayerController.playPrankSound(sound)`.
- `StopAllSounds` calls `audioPlayerController.stopAll()`.
- `Navigate` calls `onNavigate(action.route)`.
- `FillVoiceLabText` submits to `PrankstarBotVoiceLabBridge` and navigates to `voice_lab`.

## Joke Generator Status

- Local template generator; no cloud AI dependency found.
- Templates include funny comment, roast-lite, creepy whisper, robot announcement, office announcement, dad joke, and dramatic narrator.
- Prompt is sanitized and capped to 180 characters.
- Suggested voice preset ID is returned with generated text.
- Generated text can be handed to Voice Lab.

## Voice Lab Handoff Status

- `PrankstarBotVoiceLabBridge` exposes a `pendingDraft` `StateFlow`.
- Home bot sends generated text and preset ID through the bridge before navigating to `voice_lab`.
- Voice Lab collects pending draft, fills text, applies suggested preset if present, updates status, and consumes the draft.

Status: PASS static.

## Safety Status

Safety refusal patterns cover:

- Threats/violence/weapons/poison/bombs/SWAT.
- Self-harm terms.
- Harassment/stalking/bullying/traumatizing.
- Emergency/government impersonation.
- Fake emergency/911/evacuation/official alerts.
- Real-person/celebrity/boss/teacher/parent/ex/friend voice impersonation.
- Phone-number spoofing/fake caller ID/robocalls.
- Automatic/secret message sending.
- Illegal activity/hacking/stealing/blackmail/extortion.
- Non-consensual recording/hidden camera.
- Dangerous panic/stampede/evacuation pranks.

Weaknesses / recommendations:

- Add unit tests for each refusal class.
- Consider explicit safety filtering of recommended sounds if user intent is risky or if safe mode is enabled globally.
- Consider exposing refusal state in Stable V9 WebView bot if native AI input is added there.

## Mood / Video Mapping Status

Mapped moods include:

| Mood | Resource mapping |
|---|---|
| `IDLE` | `prankstar_bot_relaxed` |
| `TYPING` / `LISTENING` | `prankstar_bot_typing` |
| `SEARCHING` | `prankstar_bot_searching` |
| `SEARCHING_ALT` | `prankstar_bot_searching2` |
| `THINKING` | `prankstar_bot_thinking` |
| `PROCESSING` / `GENERATING` | `prankstar_bot_processing` |
| `PLAYING` | `prankstar_bot_happy` |
| `CELEBRATING` / `SAVED` | `prankstar_bot_celebrate` |
| `WARNING` | `prankstar_bot_warning` |
| `ERROR` / `CONFUSED` | `prankstar_bot_confused` |
| `BORED` | `prankstar_bot_bored1` |
| `BORED_ALT` | `prankstar_bot_bored2` |
| `RELAXED` | `prankstar_bot_relaxed` |
| `POWERUP` | `prankstar_bot_powerup` |
| `SAD` | `prankstar_bot_sad` |

New clip mapping checklist:

- BORED: mapped.
- SEARCHING: mapped.
- SEARCHING_ALT: mapped.
- RELAXED: mapped.
- POWERUP: mapped.
- SAD: mapped.

## Missing Features / Blockers

1. No automated bot tests found.
2. Full native bot text assistant is not statically visible on default Stable V9 WebView home.
3. No runtime persistence/history verification for bot messages beyond in-memory state.
4. No WebView-to-native bot command bridge found for Stable V9 HTML bot.
5. Recommendation safety is scoring-biased but not strict safe-only in all paths.
6. Runtime video playback/mood switching not verified without device/emulator.

## Exact Recommended Fixes

- Add unit tests:
  - `PrankstarBotCommandParserTest`
  - `PrankstarBotSafetyTest`
  - `PrankstarBotSoundRecommenderTest`
  - `PrankstarBotMoodResourceTest`
  - `PrankstarBotVoiceLabBridgeTest`
- If product wants AI input on Stable V9 home, add a narrow bridge method such as `submitBotCommand(command)` and render results in existing cyberpunk HTML UI; do not replace Stable V9 UI.
- Add optional safe-only filtering to recommender when global safe mode is enabled or when command intent is ambiguous.
