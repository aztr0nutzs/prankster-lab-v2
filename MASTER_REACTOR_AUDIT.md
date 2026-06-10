# Master Reactor Audit

Audit date: 2026-06-10

Scope: audit only. No reactor implementation, video movement, HTML rewiring, Kotlin rewiring, Gradle changes, sound catalog changes, or UI redesign were performed.

## Acceptance criteria covered

- Identify where reactor videos are currently stored.
- Identify whether runtime uses WebView HTML assets, native Android raw resources, Jetpack Compose video rendering, or mixed handling.
- Identify current reactor registry/mapping files, click/touch handlers, and screen routes opened by reactor interactions.
- Identify Prankstar / NEO robot integration and whether it is wired into reactor actions.
- Identify broken, placeholder, unused, duplicate, or mismatched reactor code.
- Document where each new/replacement reactor video should be moved or copied.
- Document exact implementation-phase edit plan and build risks.
- Preserve the existing dark neon Prankstar UI, custom header, bottom dock, robot integration, sound library, stash/library, settings, voice/joke features, and existing audio playback logic.

## Exact files inspected

### Repository and build/config context

- `AGENTS.md`
- `docs/AGENTS.md`
- `local.properties`
- `app/build.gradle.kts`
- `build.gradle.kts`
- `gradle.properties`
- `app/src/main/AndroidManifest.xml`

### Runtime navigation / app shell

- `app/src/main/java/com/pranksterlab/PranksterApp.kt`
- `app/src/main/java/com/pranksterlab/MainActivity.kt`
- `app/src/main/java/com/pranksterlab/screens/PrankstarStableHomeWebViewScreen.kt`
- `app/src/main/java/com/pranksterlab/screens/PrankstarHomeWebViewScreen.kt`

### WebView bridge and audio routing

- `app/src/main/java/com/pranksterlab/bridge/PrankstarWebBridge.kt`
- `app/src/main/java/com/pranksterlab/core/audio/AudioPlayerController.kt`
- `app/src/main/java/com/pranksterlab/core/repository/SoundRepository.kt`
- `app/src/main/assets/sound_catalog.json` was inspected for existence/path only; it was not modified.

### Current WebView reactor UI/assets

- `app/src/main/assets/prankstar/prankstar_new_home_bot_screen.html`
- `app/src/main/assets/prankstar/assets/prankstar_header.mp4`
- `app/src/main/assets/prankstar/assets/prankstar_boot.mp4`
- `app/src/main/assets/prankstar/assets/prankstar_boot2.mp4`
- `app/src/main/assets/prankstar/assets/reactor2.mp4`
- `app/src/main/assets/prankstar/assets/reactor5.mp4`
- `app/src/main/assets/prankstar/assets/reactor6.mp4`
- `app/src/main/assets/prankstar/assets/reactor7.mp4`

### Native Compose reactor and bot code

- `app/src/main/java/com/pranksterlab/screens/HomeScreen.kt`
- `app/src/main/java/com/pranksterlab/screens/UltimateReactorScreen.kt`
- `app/src/main/java/com/pranksterlab/components/reactor/ReactorCorePanel.kt`
- `app/src/main/java/com/pranksterlab/components/reactor/PranksterCoreReactor.kt`
- `app/src/main/java/com/pranksterlab/components/reactor/ReactorUiState.kt`
- `app/src/main/java/com/pranksterlab/components/reactor/NeonControlPanel.kt`
- `app/src/main/java/com/pranksterlab/components/reactor/ultimate/UltimateReactorState.kt`
- `app/src/main/java/com/pranksterlab/components/reactor/ultimate/UltimateReactorCanvas.kt`
- `app/src/main/java/com/pranksterlab/components/reactor/ultimate/UltimateReactorControls.kt`
- `app/src/main/java/com/pranksterlab/components/reactor/ultimate/UltimateReactorSideStrip.kt`
- `app/src/main/java/com/pranksterlab/components/reactor/ultimate/UltimateReactorBottomPanel.kt`
- `app/src/main/java/com/pranksterlab/components/home/PrankstarFloatingControls.kt`
- `app/src/main/java/com/pranksterlab/components/home/PrankstarFxOverlay.kt`
- `app/src/main/java/com/pranksterlab/components/video/PrankstarHeaderVideo.kt`
- `app/src/main/java/com/pranksterlab/components/video/PrankstarVideoBackground.kt`
- `app/src/main/java/com/pranksterlab/components/bot/PrankstarBotMood.kt`
- `app/src/main/java/com/pranksterlab/components/bot/PrankstarBotPanel.kt`
- `app/src/main/java/com/pranksterlab/components/bot/PrankstarBotVideo.kt`

### Root video candidates inspected

- `reactor1.mp4`
- `reactor2.mp4`
- `reactor4.mp4`
- `reactor7.mp4`
- `reactor9.mp4`
- `reactor10.mp4`
- `reactor11.mp4`
- `reactor12.mp4`

The requested root candidates `reactor3.mp4`, `reactor5.mp4`, `reactor8.mp4`, `reactor13.mp4`, `reactor14.mp4`, `reactor15.mp4`, `reactor16.mp4`, `reactor17.mp4`, and `reactor18.mp4` are not present at the project root in this checkout.

## Existing reactor asset path pattern

### Active packaged WebView asset pattern

The active WebView home loads assets under:

```text
app/src/main/assets/prankstar/prankstar_new_home_bot_screen.html
app/src/main/assets/prankstar/assets/*.mp4
```

Inside the WebView HTML, relative paths such as `assets/reactor2.mp4`, `assets/reactor5.mp4`, `assets/reactor6.mp4`, `assets/reactor7.mp4`, and `assets/prankstar_header.mp4` resolve under `app/src/main/assets/prankstar/assets/`.

### Native raw video pattern

The native Compose video components use Android raw resources under:

```text
app/src/main/res/raw/*.mp4
```

Those raw resources currently include Prankstar background/header/bot clips, but no `reactor*.mp4` raw resources are currently present.

### Embedded HTML base64 pattern

Several WebView reactor slots embed MP4 data URIs directly inside `prankstar_new_home_bot_screen.html`. This is a conflict-heavy and maintenance-heavy pattern. The implementation phase should remove the embedded base64 sources for replaced reactors and switch to relative file assets for all reactor videos.

## Current runtime handling model

The app currently has mixed handling:

1. **Default app home route is WebView-backed.** `PranksterApp` starts at route `home`, and route `home` creates `PrankstarStableHomeWebViewScreen`.
2. **The WebView screen loads `file:///android_asset/prankstar/prankstar_new_home_bot_screen.html`.** The screen enables JavaScript, file access for bundled assets, autoplay, and registers `PrankstarBridge` plus `PrankstarAndroid` JavaScript interfaces.
3. **The WebView HTML handles the visible seven-reactor selector, top video header, reactor videos, overlaid touch zones, bottom dock, and bot screen.** It calls the Android bridge for audio playback, stop-all, route navigation, and mode logging.
4. **Native Compose reactor screens still exist but are not the default home route.** Route `home_native` loads `HomeScreen`; route `home_ultimate` loads `UltimateReactorScreen`. They use native Compose reactor canvases/images and existing audio playback, not the root reactor MP4 files.
5. **Robot / bot video rendering is also mixed.** WebView home has HTML video tags for bot clips under `app/src/main/assets/prankstar/assets/bot/`; native Compose bot rendering uses `PrankstarBotVideo` with Media3/ExoPlayer against `app/src/main/res/raw/` bot clips.
6. **Audio playback remains native Android MediaPlayer-backed through `AudioPlayerController`; WebView actions call `PrankstarWebBridge`, which selects catalog sounds and delegates to `AudioPlayerController`.**

## Current reactor registry and mappings

### Active WebView registry

The active WebView registry is the `rConfig` object in `app/src/main/assets/prankstar/prankstar_new_home_bot_screen.html`:

| Current ID | Current display name | Selector class | Current source | Status |
|---|---|---|---|---|
| `r1` | `GAG ENGINE` | `on-g` | embedded base64 data URI | Active, mismatched to final target |
| `r2` | `GAG ENGINE II` | `on-b` | `assets/reactor2.mp4` | Active, no final target requested |
| `r3` | `MISCHIEF MODE` | `on-pk` | embedded base64 data URI | Active, must be replaced by final reactor3 target |
| `r4` | `WHOOPEE CHARGE` | `on-o` | embedded base64 data URI | Active, no final target requested |
| `r5` | `PRANKSTAR SURGE` | `on-r` | `assets/reactor5.mp4` | Active, must be replaced by final reactor5 target |
| `r6` | `LAUGH CORE` | `on-teal` | `assets/reactor6.mp4` | Active, no final target requested |
| `r7` | `CHAOS OVERDRIVE` | `on-y` | `assets/reactor7.mp4` | Active, no final target requested |

The selector UI currently exposes only seven reactors (`r1` through `r7`). There are no active selectors, slots, or config entries for final target reactors `r8`, `r9`, `r12`, `r13`, `r14`, `r15`, `r16`, `r17`, or `r18`.

### Native Compose mappings

Native Compose does not currently have numbered reactor video IDs. It has conceptual modes and prank types:

- `UltimateReactorScreen` has `HomeReactorMode`: `ULTIMATE`, `CLASSIC`, `COMPACT`, `VISUALIZER`.
- `UltimateReactorState` has `UltimatePrankType`: `SPLASH`, `SOUND`, `SMOKE`, `ZAP`.
- `HomeScreen` routes core taps to `ReactorCorePanel` and quick deploy/category logic; it does not address `reactor1.mp4` ... `reactor18.mp4` assets.

## Current click/touch handlers

### WebView reactor selector and video touch zones

- Reactor selector buttons call `switchR('rN', this, 'on-*')`.
- `switchR` updates `curR`, toggles `.active-slot`, starts the selected video via `ensurePlay`, updates the top reactor name, calls `callAndroid('setReactorMode', rid)`, and logs a reactor switch event.
- Overlaid zone buttons call `zoneAction(rid, label, icon)`.
- `zoneAction` increments the prank counter, creates ripple/flash feedback, increases charge, shows an alert, logs the zone action, and delegates Android work through `bridgeZoneAction(label)`.
- `bridgeZoneAction` maps generic label substrings to Android bridge calls: right zones trigger jokes, left zones trigger effects, bottom/release/core/deploy zones trigger `deployRandom` or `playRandomSound`, and other labels map to a category through `bridgeCategoryFor`.
- The deploy button calls `mainDeploy()`, which sets mode `core` and calls `deployRandom` or `playRandomSound`.
- Side-strip actions call `sAct(t)`: audio stops all, gear opens settings/system, zap plays effects, and spring is UI charge feedback only.

### WebView bottom dock / panels / routes

- The bottom dock is a custom in-HTML dock. It is not the native `PrankstarBottomDock` because the native dock is hidden on the default `home` route.
- WebView dock routes call Android bridge methods for native screens where available:
  - stash/library: `openStash()` -> route `library`
  - jokes/voice: `openJokes()` -> route `voice_lab`
  - forge: `openForge()` -> route `forge`
  - system/settings: `openSystem()` -> route `system`
  - bot: stays in the HTML bot screen via `showScreen('bot')`
  - core: stays in the HTML reactor screen via `showScreen('reactor')`

### Native Compose handlers

- `HomeScreen` `ReactorCorePanel` taps trigger catalog playback via `AudioPlayerController.playPrankSound` and expose `stopAll`, `onOpenStash`, `onOpenJokes`, and `onOpenForge` callbacks.
- `UltimateReactorScreen` `UltimateStage` core taps call `onDeploy`, power toggles stop all audio when powering off, side strips open system or voice lab depending on action, and floating controls open library/voice/forge or deploy/stop.

## Current screens/panels/routes opened by reactor interactions

Routes registered in `PranksterApp`:

| Route | Screen |
|---|---|
| `home` | WebView Prankstar home / active runtime reactor |
| `home_native` | Native `HomeScreen` |
| `home_ultimate` | Native `UltimateReactorScreen` |
| `library` | `LibraryScreen` / stash |
| `timer` | `TimerPrankScreen` |
| `forge` | `SoundForgeScreen` |
| `lab` | `SoundPacksScreen` |
| `system` | `SettingsScreen` |
| `voice_lab` | `VoiceJokeGeneratorScreen` |
| `randomizer` | `RandomizerScreen` |
| `messages` | `PrankMessagesScreen` |

Current WebView interactions can directly open `library`, `voice_lab`, `forge`, and `system` through the bridge. Native screens can also open `timer`, `lab`, and `randomizer` through native mode cards/bottom dock, but those are not currently mapped one-to-one to numbered WebView reactors.

## Robot / Prankstar / NEO mascot integration

### WebView home bot integration

The WebView home includes an HTML bot screen and bot mode functions:

- `showScreen('bot')` swaps from reactor area to `#prankstar-bot-screen` without leaving the WebView route.
- `botMap` maps bot modes to HTML video IDs: idle, scan, power, dance, and celebrate.
- `botPulse(label, mode)` switches to the bot screen, calls `setReactorMode('bot')`, calls category/joke bridge actions for scan/power/dance/celebrate/head/chest, updates charge/logs, and plays bot videos.
- A runtime wrapper around `switchDock` forces dock tab `bot` to show the bot screen and other dock tabs to show the reactor screen.

This means WebView bot actions are wired to Android audio playback through the same bridge. They are not currently wired to specific numbered reactor IDs such as `r9` safety or `r18` voice disguise.

### Native Compose bot integration

Native Compose has `PrankstarBotVideo`, `PrankstarBotPanel`, `PrankstarBotController`, and bot moods. In `HomeScreen`, the bot panel can play sounds, stop all sounds, navigate, open stash, and submit voice-lab text. In `UltimateReactorScreen`, bot mood changes with playback, errors, power, and overload state, and tapping the compact bot toggles mischief AI.

## Broken, placeholder, unused, or duplicated reactor findings

### Missing requested project-root files

Only these root reactor videos are present in this checkout:

| Root file | Size | SHA-256 |
|---|---:|---|
| `reactor1.mp4` | 2,703,481 bytes | `d6bd532065af77ef431fba7d635f3d317abd9450bc897f329ffc7167b5f331fc` |
| `reactor2.mp4` | 1,298,004 bytes | `96d40d2c045b58654da96dd4f5c4ec1a3b55eb4328f9148348fd975e5e3ea200` |
| `reactor4.mp4` | 2,423,891 bytes | `b97618560e0a2bf9bb8fc82baed5a6a72d7ccaf82d972b37dba559654e435d0f` |
| `reactor7.mp4` | 4,964,261 bytes | `4ce1e1cfc06b1e4756a5aee3f6c3f1380b6ae8de1b5fa1c024aa5547f5beaf7f` |
| `reactor9.mp4` | 1,946,553 bytes | `bf422f4b4abc222126f5692ce1ce4922528fc209753e66c297ecd1c87f8523af` |
| `reactor10.mp4` | 2,703,481 bytes | `d6bd532065af77ef431fba7d635f3d317abd9450bc897f329ffc7167b5f331fc` |
| `reactor11.mp4` | 2,445,369 bytes | `65ad0461d3bd59db1c6bacd729b7adb24806cd54bd146984231fa05e3453be4f` |
| `reactor12.mp4` | 2,460,932 bytes | `88fe201184442d437a18fccd108a565c9931c59102e22b2c5f88d05382881b01` |

Requested root files not found:

- `reactor3.mp4`
- `reactor5.mp4`
- `reactor8.mp4`
- `reactor13.mp4`
- `reactor14.mp4`
- `reactor15.mp4`
- `reactor16.mp4`
- `reactor17.mp4`
- `reactor18.mp4`

### Existing duplicate files

- `reactor1.mp4` and `reactor10.mp4` at project root are byte-identical by SHA-256. This may be intentional staging or a misnamed duplicate; do not infer reactor10 as a replacement for any final target without user confirmation.
- `reactor2.mp4` root is byte-identical to `app/src/main/assets/prankstar/assets/reactor2.mp4`.
- `reactor7.mp4` root is byte-identical to `app/src/main/assets/prankstar/assets/reactor7.mp4`.

### Packaged asset mismatch

Packaged WebView assets currently include only:

- `app/src/main/assets/prankstar/assets/reactor2.mp4`
- `app/src/main/assets/prankstar/assets/reactor5.mp4`
- `app/src/main/assets/prankstar/assets/reactor6.mp4`
- `app/src/main/assets/prankstar/assets/reactor7.mp4`

There is no packaged file asset for reactors 1, 3, or 4 even though those are active WebView slots; those three are embedded in HTML as base64 data URIs. There are also no packaged files for reactors 8, 9, 12, 13, 14, 15, 16, 17, or 18.

### Active mapping mismatch against final target mapping

The final requested mapping conflicts with current WebView names and behavior:

| Reactor | Final target | Current active mapping |
|---|---|---|
| `reactor1` | Vortex X / Singularity Shenanigans / random prank chains | `r1` = GAG ENGINE with embedded video and generic deploy/audio/AI/console zones |
| `reactor3` | Portal Pulse / Space-Time Trolling / warp-delay, teleport, remote-feel prank routing | `r3` = MISCHIEF MODE with embedded video and generic soundboard-style actions |
| `reactor5` | Pixel Bomb / Data Corruption Initiated / glitch burst, scrambled UI/audio prank effects | `r5` = PRANKSTAR SURGE using packaged `assets/reactor5.mp4` |
| `reactor8` | Sonic Blast / Decibel Reactor Overdrive / sound intensity and overdrive controls | no active slot/config/asset |
| `reactor9` | Prankstar Armor / Prank Shield Active / safety, cooldowns, stop-all, limits | root file exists but no active slot/config/asset |
| `reactor12` | Prankstar Live / live deploy console and active queue | root file exists but no active slot/config/asset |
| `reactor13` | Gag Engine / joke, gag, prank-combo generator | no root file and no active slot/config/asset |
| `reactor14` | Mischief Mode / main categorized soundboard | no root file and no active slot/config/asset |
| `reactor15` | Trap Actuator / timed traps and trigger-based playback | no root file and no active slot/config/asset |
| `reactor16` | Loud Mode / quick loud favorites launcher | no root file and no active slot/config/asset |
| `reactor17` | Gravity Glitch / pitch, speed, stutter, reverse, weird FX | no root file and no active slot/config/asset |
| `reactor18` | Digital Disguise / voice masks, character voice prank mode | no root file and no active slot/config/asset |

### Generic behavior problem

Most WebView reactor zones ultimately collapse into a small set of generic bridge actions (`deployRandom`, `playRandomSound`, `playRandomJoke`, `playSoundByCategory`, `openSystem`, `stopAll`). This is buildable today, but it does not satisfy the final requirement that each new reactor have a distinct purpose. The implementation phase must add a small typed reactor action registry instead of wiring every reactor to the same generic soundboard action.

### Potential placeholder/dead behavior

- HTML toggle switches (`.tog-sw`) currently only toggle CSS classes and do not persist or call Android. They are UI controls but not all are real settings.
- `sAct('spring')` currently shows feedback/charge but does not call Android playback or navigation.
- Some labels and names are stale (`GAG ENGINE`, `MISCHIEF MODE`, `PRANKSTAR SURGE`) relative to the final target map.
- The WebView app contains historical/root HTML references (`prankstar_new_home_bot_screen.html`, `prankstar_reactor_ultimate.html`) at project root, but the active runtime HTML is under `app/src/main/assets/prankstar/`.

## Where each final reactor file should be moved/copied

Implementation should copy/move final reactor MP4 files into the active WebView asset directory:

```text
app/src/main/assets/prankstar/assets/
```

Do not use `app/src/main/res/raw/` for these reactor videos unless the implementation also changes runtime from WebView video tags to native Compose/ExoPlayer. The current default runtime is WebView, so the lowest-risk implementation path is to keep WebView video tags and load relative bundled assets.

| Final reactor file | Source expected at project root | Destination for implementation | Current source status |
|---|---|---|---|
| `reactor1.mp4` | `reactor1.mp4` | `app/src/main/assets/prankstar/assets/reactor1.mp4` | present at root |
| `reactor3.mp4` | `reactor3.mp4` | `app/src/main/assets/prankstar/assets/reactor3.mp4` | missing at root |
| `reactor5.mp4` | `reactor5.mp4` | `app/src/main/assets/prankstar/assets/reactor5.mp4` | missing at root; old packaged file exists and must be replaced when source is available |
| `reactor8.mp4` | `reactor8.mp4` | `app/src/main/assets/prankstar/assets/reactor8.mp4` | missing at root |
| `reactor9.mp4` | `reactor9.mp4` | `app/src/main/assets/prankstar/assets/reactor9.mp4` | present at root |
| `reactor12.mp4` | `reactor12.mp4` | `app/src/main/assets/prankstar/assets/reactor12.mp4` | present at root |
| `reactor13.mp4` | `reactor13.mp4` | `app/src/main/assets/prankstar/assets/reactor13.mp4` | missing at root |
| `reactor14.mp4` | `reactor14.mp4` | `app/src/main/assets/prankstar/assets/reactor14.mp4` | missing at root |
| `reactor15.mp4` | `reactor15.mp4` | `app/src/main/assets/prankstar/assets/reactor15.mp4` | missing at root |
| `reactor16.mp4` | `reactor16.mp4` | `app/src/main/assets/prankstar/assets/reactor16.mp4` | missing at root |
| `reactor17.mp4` | `reactor17.mp4` | `app/src/main/assets/prankstar/assets/reactor17.mp4` | missing at root |
| `reactor18.mp4` | `reactor18.mp4` | `app/src/main/assets/prankstar/assets/reactor18.mp4` | missing at root |

## Files that must be edited during implementation phase

Minimum-risk implementation should edit only these files plus copied MP4 assets:

1. `app/src/main/assets/prankstar/prankstar_new_home_bot_screen.html`
   - Replace embedded base64 sources for active replacement reactors with `assets/reactorN.mp4` file references.
   - Add slots/selectors/config entries for reactors 8, 9, 12, 13, 14, 15, 16, 17, and 18 without renumbering.
   - Keep existing header, bottom dock, bot screen, sound panels, stash, forge, jokes, settings, and route calls.
   - Add distinct per-reactor labels/actions mapped to the final target behavior.
   - Avoid fake buttons; every new touch zone should either call an Android bridge method, navigate to an existing route, or perform visible local state changes tied to a real feature.
2. `app/src/main/java/com/pranksterlab/bridge/PrankstarWebBridge.kt`
   - Add narrowly scoped bridge methods for reactor-specific behaviors that cannot be represented by current generic methods, e.g. safety/stop/cooldown, live queue/deploy, category launch, trap/timer navigation, loud favorites, voice mode navigation, and FX category routing.
   - Preserve existing methods for backward compatibility.
3. `app/src/main/java/com/pranksterlab/PranksterApp.kt`
   - Edit only if the bridge needs existing routes exposed from WebView that are not already bridge-callable. Current routes already include `timer`, `randomizer`, `messages`, and `lab`, but there are no bridge methods for those yet.
4. Optional only if native mirrors are required later: `app/src/main/java/com/pranksterlab/screens/UltimateReactorScreen.kt`, `HomeScreen.kt`, and `components/reactor/ultimate/*`. These should not be part of the first WebView/video mapping PR unless the product requirement explicitly requires native `home_ultimate` parity.

Do not edit `sound_catalog.json` unless a verified ID/path mapping bug is found. No such catalog bug was found in this audit.

## Exact implementation plan

### Phase 0: asset gate before editing runtime

1. Confirm the required root files exist before implementation:
   - replacements: `reactor1.mp4`, `reactor3.mp4`, `reactor5.mp4`, `reactor8.mp4`
   - additions: `reactor9.mp4`, `reactor12.mp4`, `reactor13.mp4`, `reactor14.mp4`, `reactor15.mp4`, `reactor16.mp4`, `reactor17.mp4`, `reactor18.mp4`
2. Block or split implementation if any required source file is missing. In this checkout, nine requested files are missing.
3. Record size and SHA-256 for every source MP4 before copying.

### Phase 1: copy replacement/addition videos to active WebView assets

1. Copy root `reactor1.mp4` to `app/src/main/assets/prankstar/assets/reactor1.mp4`.
2. Replace `app/src/main/assets/prankstar/assets/reactor5.mp4` only when the new root `reactor5.mp4` is available.
3. Copy the available addition files `reactor9.mp4` and `reactor12.mp4` only if proceeding with partial asset staging is explicitly approved.
4. Copy missing final files once provided: `reactor3.mp4`, `reactor8.mp4`, `reactor13.mp4`, `reactor14.mp4`, `reactor15.mp4`, `reactor16.mp4`, `reactor17.mp4`, and `reactor18.mp4`.
5. Do not delete existing reactor2/reactor4/reactor6/reactor7 assets unless a later product decision removes those IDs from the UI. The user explicitly said not to renumber target reactors.

### Phase 2: create a real WebView reactor registry

In `app/src/main/assets/prankstar/prankstar_new_home_bot_screen.html`:

1. Replace `rConfig` with entries for the final numbered IDs while preserving existing non-target reactors as legacy/secondary if needed.
2. Recommended final registry fields:
   - `id`: `r1`, `r3`, `r5`, `r8`, `r9`, `r12`, `r13`, `r14`, `r15`, `r16`, `r17`, `r18`
   - `file`: `assets/reactorN.mp4`
   - `name`: final display name
   - `subtitle`: final feature phrase
   - `color` / selector class
   - `primaryAction`: bridge action name
   - `panel`: matching existing panel or route
3. Ensure selectors are horizontally scrollable or grid-wrapped without flattening the bottom dock or hiding the custom header.
4. Keep `r1`, `r3`, `r5`, and `r8` as replacements; do not keep old `r3` or old `r5` active under the same IDs.

### Phase 3: replace embedded video sources with asset files

1. Change `vid-r1` source from base64 data URI to `assets/reactor1.mp4`.
2. Change `vid-r3` source from base64 data URI to `assets/reactor3.mp4` after the replacement file exists.
3. Keep `vid-r5` source as `assets/reactor5.mp4`, but replace the packaged file with the new root file.
4. Add new `<div class="reactor-slot" id="slot-rN" data-rid="rN">` blocks for `r8`, `r9`, `r12`, `r13`, `r14`, `r15`, `r16`, `r17`, and `r18` using `assets/reactorN.mp4`.
5. Prefer a small helper/template function if editing HTML manually becomes too large, but avoid wholesale rewrites.

### Phase 4: add distinct, non-duplicate action routing

Add a new JavaScript function such as `reactorAction(rid, action)` that maps each final reactor to existing real app behavior:

| Reactor | Required behavior target | Safe first implementation route/action |
|---|---|---|
| `r1` | random prank chains | call `deployRandom`, optionally schedule 2-3 safe sequential deploys with visible queue/log and stop support |
| `r3` | warp-delay, teleport, remote-feel prank routing | route to timer/random delayed deploys through new bridge method or navigate to `timer` |
| `r5` | glitch burst, scrambled UI/audio prank effects | play effects/cartoon/ambience category and trigger visible glitch overlay |
| `r8` | sound intensity and overdrive controls | adjust local intensity UI and play high-intensity safe categories; never bypass safe mode |
| `r9` | safety, cooldowns, stop-all, limits | call `stopAll`, set cooldown UI, expose settings/system route |
| `r12` | live deploy console and active queue | show existing core/log panel and queue real deploy actions |
| `r13` | joke/gag/combo generator | call `playRandomJoke`, open `voice_lab` for joke creation |
| `r14` | categorized soundboard | open stash/library or existing category panel, play selected category only |
| `r15` | timed traps and trigger playback | navigate to `timer` or add `openTimer` bridge method |
| `r16` | loud favorites launcher | play explicitly marked safe high-intensity/loud category candidates, with clear stop-all |
| `r17` | pitch/speed/stutter/reverse/weird FX | route to `forge` and/or effects category; do not claim unsupported real-time pitch unless implemented natively |
| `r18` | voice masks/character voice prank mode | navigate to `voice_lab` and/or play generated voice category |

### Phase 5: bridge additions

In `PrankstarWebBridge.kt`, add only small, stable methods:

- `openTimer()` -> `navigate("timer")`
- `openRandomizer()` -> `navigate("randomizer")`
- `openPacks()` -> `navigate("lab")`
- `openMessages()` -> `navigate("messages")`
- `playReactorCategory(category: String)` -> wrapper around existing category selection if JavaScript names need clearer semantics
- Optional `deployChain(count: Int)` with safety caps and coroutine delay, if random prank chains are required in the bridge rather than JS timers

Do not add dependencies. Do not change MediaPlayer playback internals unless bridge calls reveal a real playback bug.

### Phase 6: validation

1. Run a static asset verification script/command:
   - every `assets/reactorN.mp4` referenced by HTML exists under `app/src/main/assets/prankstar/assets/`
   - no duplicate IDs in `rConfig`
   - every selector has a matching slot and every slot has a matching config
   - no final replacement ID still points to embedded base64
2. Before Gradle, follow Android build instructions:
   - run `chmod +x scripts/build-android-debug.sh`
   - run `./scripts/build-android-debug.sh`
3. Verify the script confirms SDK environment before Gradle.
4. Run manual emulator/device QA if `adb` is available:
   - launch app
   - switch every final reactor ID
   - confirm video plays
   - tap every active zone
   - verify no dead touch target
   - verify navigation to library, forge, voice lab, settings, timer, and randomizer as applicable
   - verify stop-all and safety reactor behavior

## Build risks

1. **Missing source videos block complete implementation.** Nine requested root files are absent in this checkout, including replacement files for `reactor3`, `reactor5`, and `reactor8`.
2. **Conflict-magnet file risk.** The active runtime file `app/src/main/assets/prankstar/prankstar_new_home_bot_screen.html` is large, contains embedded base64 videos, and is explicitly conflict-prone. The implementation should be surgical and avoid unrelated formatting.
3. **APK size risk.** Moving all reactor videos from base64/asset staging into packaged Android assets will increase APK size. Embedded base64 removal may offset some size, but all final files should be checked.
4. **WebView autoplay/lifecycle risk.** Adding many simultaneous `<video>` tags can increase memory use. Only the active slot should play; inactive videos should be paused or not preloaded aggressively.
5. **Bridge/security risk.** New JavaScript interface methods must be narrow and not expose arbitrary route strings or file paths.
6. **Route mismatch risk.** Existing `PrankstarWebBridge` does not expose timer/randomizer/messages/lab routes. New bridge methods are needed for reactors 15 and possibly 12/14/16.
7. **No native reactor video parity.** `home_native` and `home_ultimate` will not show numbered reactor MP4s unless separately implemented. That is acceptable for a WebView-first implementation because the default `home` route is WebView.
8. **Android SDK/build environment risk.** Any Gradle build must go through `scripts/build-android-debug.sh` per project instructions so SDK paths and required directories are verified first.

## Audit conclusion

Implementation is **not complete**. The current app uses a WebView-first reactor runtime with seven active reactor slots and mixed native Compose fallback/alternate screens. The final requested target requires replacing old reactors 1, 3, 5, and 8, adding reactors 9, 12, 13, 14, 15, 16, 17, and 18, and introducing distinct per-reactor behavior rather than generic soundboard wiring.

The safest implementation path is a small WebView-first PR that copies the final MP4s into `app/src/main/assets/prankstar/assets/`, surgically updates `app/src/main/assets/prankstar/prankstar_new_home_bot_screen.html`, and adds only minimal bridge route/action methods in `PrankstarWebBridge.kt`. This should preserve the existing Prankstar dark neon UI, custom header, bottom dock, robot/avatar integration, sound library, stash/library screens, settings, voice/joke features, and existing audio playback logic.
