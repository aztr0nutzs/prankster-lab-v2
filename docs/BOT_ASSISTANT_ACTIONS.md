# Bot Assistant Actions

Last updated: 2026-06-07

## Supported intents

- `SearchSounds(query)`
- `RecommendSounds(vibe)`
- `PlayRecommended(vibe)`
- `GenerateJoke(prompt)`
- `BuildPrankPlan(prompt)`
- `ChooseVoice(prompt)`
- `PlayRandom`
- `StopAll`
- `OpenStash`
- `OpenJokes`
- `OpenForge`
- `OpenSystem`
- `Help`
- `Unknown(raw)`

## Supported actions

- `ShowMessage(text)` shows a transparent explanation of what the bot did.
- `ShowSoundRecommendations(sounds, reason)` displays real `PrankSound` cards only.
- `PlaySound(sound)` delegates playback to the existing `AudioPlayerController`.
- `StopAllSounds` calls existing stop-all behavior.
- `Navigate(route)` asks the UI layer to navigate; the controller does not own `NavController`.
- `FillVoiceLabText(text, suggestedVoicePresetId)` stores a draft through `PrankstarBotVoiceLabBridge`.
- `ShowPrankPlan(plan)` displays a harmless plan; plans do not auto-run.
- `Refuse(reason)` displays a safety refusal.

## Route names

- `home`
- `library`
- `voice_lab`
- `forge`
- `system`

## Example command behavior

- `find creepy sounds`: searches real stash sounds and shows recommendation cards.
- `show animal sounds`: searches category/tags/name/pack metadata for animal sounds.
- `play something funny`: recommends funny sounds and plays the first real result.
- `make a joke about being late`: generates local text and exposes Voice Lab handoff.
- `create a robot announcement`: generates a robot-style local line.
- `open stash`: navigates to `library`.
- `open jokes`: navigates to `voice_lab`.
- `open forge`: navigates to `forge`.
- `open system`: navigates to `system`.
- `stop all`: stops the shared audio controller.

## Execution constraints

- Sound cards are backed by repository sounds; no fake recommendation rows are created.
- Generate/Preview/Save in Voice Lab remains user-controlled.
- Prank plans are informational and not auto-run.
- Navigation is returned as an action and executed by the Composable host.
- The bot does not send messages automatically.
