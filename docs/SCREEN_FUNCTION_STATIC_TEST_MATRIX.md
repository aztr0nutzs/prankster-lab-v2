# Screen Function Static Test Matrix

Date: 2026-06-09

Scope: Static source inspection only. No physical device, emulator, ADB install, runtime tapping, or screenshots were available.

## Route Matrix

| Route | Screen file | Entry point | Visible controls inspected | Mapped action | Static status | Issue / fix needed |
|---|---|---|---|---|---|---|
| `home` | `app/src/main/java/com/pranksterlab/screens/PrankstarStableHomeWebViewScreen.kt`; `app/src/main/assets/prankstar/prankstar_new_home_bot_screen.html` | `NavHost(startDestination = "home")` | HTML dock, deploy, stop/audio, reactor tap/zones, reactor selector, mode buttons, knobs, bot dock/video controls | Android bridge calls `deployRandom`, `stopAll`, category playback, and route methods; local animations/state for purely visual controls | PASS static; NEEDS DEVICE runtime | Missing checklist external assets `reactor1.mp4`, `reactor3.mp4`, `reactor4.mp4`, though current HTML did not reference them as file paths. Stable V9 bot is animated/video-first, not full native text assistant. |
| `home_native` | `app/src/main/java/com/pranksterlab/screens/HomeScreen.kt` | Defined route; fallback only | Native reactor controls, floating controls, bot input panel, recommendations | Uses `AudioPlayerController`, `SoundRepository`, `PrankstarBotController`, navigation callbacks | PASS static; NEEDS DEVICE runtime | Fallback route not primary dock destination. |
| `home_ultimate` | `app/src/main/java/com/pranksterlab/screens/UltimateReactorScreen.kt` | Defined route; fallback/alternate | Ultimate reactor controls, side strip, power, deploy, stop, Stash/Jokes/Forge | Uses local reactor state, audio deploy/stop, navigation callbacks | PASS static; NEEDS DEVICE runtime | Fallback route not primary dock destination. |
| `library` | `app/src/main/java/com/pranksterlab/screens/LibraryScreen.kt` | Native dock Stash; WebBridge `openStash`; Library callbacks | Search, diagnostics, category chips, pack chips, generated filters, favorites, loop, play/stop, Create Joke, Timer shortcut | Repository filters/favorites/custom sounds; `AudioPlayerController.playPrankSound/stopAll`; route to `voice_lab`/`timer` | PASS static; NEEDS DEVICE runtime | None proven statically. |
| `timer` | `app/src/main/java/com/pranksterlab/screens/TimerPrankScreen.kt` | Library timer shortcut; route defined | Preset delay, custom delay, sound picker, start/pause/resume/cancel, sound selection | Timer state and `AudioPlayerController` playback | PASS static; NEEDS DEVICE runtime | Not on main dock. |
| `forge` | `app/src/main/java/com/pranksterlab/screens/soundforge/SoundForgeScreen.kt`; `SoundForgeWorkbench.kt` | Native dock Forge; WebBridge `openForge` | Generator type, duration, volume, generator params, FX toggles/sliders, seed controls, presets, generate, preview/stop, save | `SoundForgeViewModel`, generator engine, custom sound manager, shared audio controller | PASS static; NEEDS DEVICE runtime | None proven statically. |
| `lab` | `app/src/main/java/com/pranksterlab/screens/SoundPacksScreen.kt` | Route defined | Pack preview, open pack | Preview plays representative pack sound; open calls Library with active pack filter | PASS static; NEEDS DEVICE runtime | Route exists but no primary dock tab navigates directly to `lab`. |
| `system` | `app/src/main/java/com/pranksterlab/screens/SettingsScreen.kt` | Native dock System; WebBridge `openSystem`; bot parser settings/system | Volume, safe random, haptics, bot toggles, safety ack, animation intensity, diagnostics, cleanup/reset actions | DataStore-backed repository settings and cleanup calls | PASS static; NEEDS DEVICE runtime | “Open validation report” is status text, not actual file opening. |
| `voice_lab` | `app/src/main/java/com/pranksterlab/screens/voice/VoiceJokeGeneratorScreen.kt` | Native dock Jokes; WebBridge `openJokes`; Library Create Joke; bot handoff | Bot helper, text input, voice preset chips, search/filter, pitch/speed/volume/effect, echo, generate, preview, stop preview, save | Android TTS engine, generated repo, preview MediaPlayer, custom sound persistence | PASS static; NEEDS DEVICE runtime | TTS synthesis/preview requires device/runtime engine. |
| `randomizer` | `app/src/main/java/com/pranksterlab/screens/RandomizerScreen.kt` | Route defined | Interval sliders, category controls, safe-only, loop/surprise, start/stop, stop all | Randomizer state and audio playback | PASS static; NEEDS DEVICE runtime | Not on main dock. |
| `messages` | `app/src/main/java/com/pranksterlab/screens/PrankMessagesScreen.kt` | Route defined | Category/template selection, message text, phone field, copy/reset | Local text composition and clipboard-style action; no auto-send discovered | PASS static; NEEDS DEVICE runtime | Not on main dock; phone field should remain local-only unless explicit consent/send flow is added. |

## Native Bottom Dock Status

| Dock tab | Route | Status |
|---|---|---|
| CORE | `home` | Present on native routes; hidden on Stable V9 home so HTML dock can be used. |
| STASH | `library` | Present and mapped. |
| FORGE | `forge` | Present and mapped. |
| JOKES | `voice_lab` | Present and mapped. |
| SYS | `system` | Present and mapped. |

## Dead / Suspicious Control Search

Command:

```bash
rg -n "TODO|onClick[[:space:]]*=[[:space:]]*\{[[:space:]]*\}|clickable[[:space:]]*\{[[:space:]]*\}|Button\(|IconButton\(|navigate\(" app/src/main/java || true
```

Findings:

- No empty `onClick = {}` or empty `clickable {}` patterns were found.
- Many mapped buttons and clickables were found in expected screens.
- No broken route string was proven statically.

## Device-Only Items Not Verified

- Actual touch hit boxes and scrollability.
- WebView video autoplay across Android WebView versions.
- Android MediaPlayer decode/playback on target devices.
- TextToSpeech availability and `synthesizeToFile` output on target devices.
- Haptics and animation performance.
- Back navigation and process-death behavior.
