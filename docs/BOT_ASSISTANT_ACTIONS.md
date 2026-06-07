# Bot Assistant Actions

## Supported intents

- `SearchSounds(query)`
- `RecommendSounds(vibe)`
- `GenerateJoke(prompt)`
- `BuildPrankPlan(prompt)`
- `ChooseVoice(prompt)`
- `PlayRandom`
- `StopAll`
- `OpenStash`
- `OpenJokes`
- `OpenForge`
- `Help`
- `Unknown(raw)`

## Supported actions

- `ShowMessage(text)` shows a transparent explanation of what the bot did.
- `ShowSoundRecommendations(sounds, reason)` displays real `PrankSound` cards only.
- `PlaySound(sound)` delegates playback to the existing `AudioPlayerController`.
- `StopAllSounds` calls existing audio stop-all behavior.
- `Navigate(route)` asks the UI layer to navigate; the controller does not own NavController.
- `FillVoiceLabText(text, suggestedVoicePresetId)` stores a draft for Voice Lab or presents a send button.
- `ShowPrankPlan(plan)` displays a harmless plan; plans do not auto-run in this phase.
- `Refuse(reason)` displays a safety refusal.

## Route names

- `home`
- `library`
- `voice_lab`
- `forge`
- `system`

## Execution constraints

- Sound cards are backed by repository sounds; no fake recommendation rows are created.
- Generate/Preview/Save in Voice Lab remains user-controlled.
- Prank plans are informational and not auto-run.
- Navigation is returned as an action and executed by the Composable host.
