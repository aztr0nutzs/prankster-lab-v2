# Stable V9 WebView Bridge Report

Date: 2026-06-09

Scope: Static source and HTML inspection only. No WebView runtime, tapping, or screenshots were available.

## Files Inspected

- `app/src/main/assets/prankstar/prankstar_new_home_bot_screen.html`
- `app/src/main/java/com/pranksterlab/screens/PrankstarStableHomeWebViewScreen.kt`
- `app/src/main/java/com/pranksterlab/bridge/PrankstarWebBridge.kt`
- `app/src/main/java/com/pranksterlab/PranksterApp.kt`

## WebView URL

Stable V9 WebView loads:

```text
file:///android_asset/prankstar/prankstar_new_home_bot_screen.html
```

## WebView Settings

Static status in `PrankstarStableHomeWebViewScreen.kt`:

| Setting | Status |
|---|---|
| JavaScript | Enabled |
| DOM storage | Enabled |
| Media autoplay | `mediaPlaybackRequiresUserGesture = false` |
| File access | Enabled |
| Content access | Enabled |
| File access from file URLs | Enabled |
| Universal access from file URLs | Disabled |
| Mixed content | Never allow |
| Zoom controls | Disabled |
| Scrollbars | Disabled |
| Background | Black |
| Long click | Disabled |

## Bridge Object Names

Android registers the same bridge object under both names:

- `PrankstarBridge`
- `PrankstarAndroid`

HTML lookup:

```javascript
return window.PrankstarBridge || window.PrankstarAndroid || null;
```

Status: PASS static.

## Required Bridge Methods

| Method | Android status | HTML usage status | Native action |
|---|---|---|---|
| `deployRandom()` | Present | Called by `mainDeploy()` and zone deploy paths | Selects playable bundled sound and calls `AudioPlayerController.playPrankSound`. |
| `stopAll()` | Present | Called by audio side action | Calls `AudioPlayerController.stopAll`. |
| `openStash()` | Present | Called by `switchDock('stash')` | Navigates to `library`. |
| `openJokes()` | Present | Called by `switchDock('jokes')` | Navigates to `voice_lab`. |
| `openForge()` | Present | Called by `switchDock('forge')` | Navigates to `forge`. |
| `openSystem()` | Present | Called by `switchDock('sys')` and gear side action | Navigates to `system`. |
| `setReactorMode(mode)` | Present | Called by dock/mode/deploy/forge/joke flows | Stores current mode for logging/selection context. |
| `logEvent(event)` | Present | Available | Logs event. |

Additional compatibility bridge methods:

- `playRandomSound()` -> `deployRandom()`.
- `playSoundByCategory(category)` -> playable category/tag/pack/name selection with safe-mode fallback.
- `playRandomJoke()` -> funny category playback.
- `playJokeByType(type)` -> category playback.
- `stopPlayback()` -> `stopAll()`.

## HTML Functions Inspected

| HTML function | Static action |
|---|---|
| `callAndroid(method, ...args)` | Safely checks bridge/method existence, catches bridge exceptions, returns boolean. |
| `mainDeploy()` | Calls `setReactorMode('core')`, `deployRandom()` with fallback `playRandomSound()`, updates local UI counters/charge/log. |
| `stashDeploy(name)` | Calls `playSoundByCategory('stash')` and updates local UI. |
| `forgeAction(name)` | Calls `setReactorMode('forge')` and `playSoundByCategory('forge')`. |
| `jokeAction(type)` | Calls `setReactorMode('jokes')` and `playJokeByType(type)`. |
| `reactorClick()` / `reactorTouch()` | Infers zone and calls `bridgeZoneAction()`. |
| `bridgeZoneAction(label)` | Routes right zone to joke, left to effects, deploy/core to random deploy, others to category playback. |
| `switchDock(id, el)` | Switches local panels, calls `setReactorMode`, and navigates for Stash/Forge/Jokes/System. |
| `sAct(t)` | Audio maps to `stopAll`, zap maps to effects, gear maps to System; other side actions are local visual alerts. |
| `botPulse()` | Visual bot/audio animation behavior; no native AI command submission found. |

## Connected Controls

- Deploy button: connected to real random bundled playback.
- Reactor zone/tap: connected to category/random bridge playback.
- Stash dock: connected to Library route.
- Forge dock: connected to Forge route.
- Jokes dock: connected to Voice Lab route.
- System dock/gear: connected to System route.
- Audio side button: connected to Stop All.
- Stash/Forge/Joke action cards: connected to category playback.

## Visual-Only / Local-Only Controls

These controls animate or adjust local HTML state but do not perform a native app action in the current bridge:

- Power toggle pauses/plays HTML videos locally.
- Mode buttons set local mode styling/alerts.
- Knobs/sliders adjust HTML intensity values.
- Spring/holo/AI side actions mostly show local alerts/ripple.
- HTML bot video/pulse behavior is local visual behavior and is not the same as the native `PrankstarBotPanel` text assistant.

This is not automatically a bug; these may be intended immersive controls. Runtime UX should verify labels do not imply unavailable native behavior.

## Asset Path Status

Parsed local HTML references and status:

| Path | Status |
|---|---|
| `assets/prankstar_header.mp4` | Present |
| `assets/reactor2.mp4` | Present |
| `assets/reactor5.mp4` | Present |
| `assets/reactor6.mp4` | Present |
| `assets/reactor7.mp4` | Present |
| `assets/bot/high.mp4` | Present |
| `assets/bot/scanning2.mp4` | Present |
| `assets/bot/powerup2.mp4` | Present |
| `assets/bot/dancing.mp4` | Present |
| `assets/bot/celebrate2.mp4` | Present |

Checklist-required but missing as external files:

- `assets/reactor1.mp4`
- `assets/reactor3.mp4`
- `assets/reactor4.mp4`

Static interpretation:

- Current HTML contains embedded/base64 reactor video content and did not parse references to the missing external files.
- If product requirement is that all seven reactor files exist externally, restore these files without changing the UI.

## Blockers / Recommendations

1. Run on real WebView to confirm video autoplay, bridge calls, and navigation behavior.
2. Decide whether to restore external `reactor1.mp4`, `reactor3.mp4`, and `reactor4.mp4` for checklist completeness.
3. Consider adding a small bridge/unit test for route methods and random deploy with fake repository/player.
4. If the Stable V9 HTML bot should support native AI text input, add a narrow bridge/handoff rather than replacing the UI.
