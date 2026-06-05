# UI Asset Integration Report

Last updated: 2026-06-05

## UI Preservation Rule

The app keeps the premium dark neon Prankstar visual system:

- custom image headers remain in place
- custom Prankstar bottom dock remains in place
- Reactor core remains interactive
- Sound Forge, Voice Lab, Library, Randomizer, Timer, Packs, Settings, and Prank Messages remain present
- no default Material bottom navigation replacement

## Header Assets

Header drawables:

- `app/src/main/res/drawable/header_sound_gen.png`
- `app/src/main/res/drawable/header_sound_stash.png`
- `app/src/main/res/drawable/header_joke_gen.png`
- `app/src/main/res/drawable/header_settings.png`

Reusable component:

- `app/src/main/java/com/pranksterlab/components/PrankstarHeader.kt`

`PrankstarHeader` supports status-only image-first mode with `showTextOverlay = false`. This is used where the header art already contains baked-in screen text, avoiding duplicate large titles over the artwork.

## Screen-to-Header Mapping

Current route map from `PranksterApp.kt`:

| Route | Screen | Header |
| --- | --- | --- |
| `home` | Core / Ultimate Reactor | Native `UltimateReactorScreen` with `PrankstarHeaderVideo` fallback; global dock preserved |
| `library` | Library / Sound Stash | `header_sound_stash`, text overlay disabled |
| `lab` | Sound Packs | `header_sound_stash`, text overlay disabled |
| `forge` | Sound Forge | `header_sound_gen`, text overlay disabled |
| `randomizer` | Randomizer | `header_sound_gen`, text overlay disabled |
| `timer` | Timer | `header_sound_gen`, text overlay disabled |
| `voice_lab` | Voice Lab / Joke Gen | `header_joke_gen`, text overlay disabled |
| `messages` | Prank Messages | `header_joke_gen`, text overlay disabled |
| `system` | Settings | `header_settings`, text overlay disabled |

## Dock Asset

Dock drawable:

- `app/src/main/res/drawable/prankstar_dock_main.png`

Dock component:

- `app/src/main/java/com/pranksterlab/components/PrankstarBottomDock.kt`

## Dock Route Mapping

Required dock tabs:

| Dock tab | Route |
| --- | --- |
| Core | `home` |
| Library | `library` |
| Forge | `forge` |
| Jokes | `voice_lab` |
| System | `system` |

Grouped active-state routes:

- `home`, `randomizer`, `timer` -> Core
- `library`, `lab` -> Library
- `forge` -> Forge
- `voice_lab`, `messages` -> Jokes
- `system` -> System

The old Sequence route is not in the dock. Legacy sequence code remains in the repository, but no active navigation destination is currently registered for `sequence`.

## Active-State Solution

The dock image has baked visual styling, so the dynamic overlay now dims the base image and draws a stronger selected tab:

- base dock image alpha reduced
- dark overlay added across the dock art
- selected tab receives bright fill, gradient border, glow, and white icon/text
- inactive tabs receive dark panels and subtle borders
- tabs expose `Role.Tab`, content descriptions, and selected semantics

This prevents the baked Jokes artwork from reading as active on every route.

## Current Verification

- Static route audit: PASS
- Debug build through `.\gradlew.bat assembleDebug --stacktrace --console=plain`: PASS
- Clean build through `.\scripts\build-android-debug.ps1`: BLOCKED by locked Windows `app/build` files before compilation
- Runtime visual QA on device/emulator: BLOCKED, ADB was available but no device/emulator was attached

No audio assets or `sound_catalog.json` were changed for UI integration.

## Robot MP4 Integration

Prankstar Bot / NEO mascot MP4 source clips remain at the repository root. The app maps moods to these Android-safe raw resource names when clips are packaged under `app/src/main/res/raw/`, but duplicate raw MP4 binaries are intentionally not included in this follow-up commit:

- `prankstar_bot_processing.mp4`
- `prankstar_bot_celebrate.mp4`
- `prankstar_bot_typing.mp4`
- `prankstar_bot_warning.mp4`
- `prankstar_bot_confused.mp4`
- `prankstar_bot_happy.mp4`
- `prankstar_bot_surprised.mp4`
- `prankstar_bot_angry.mp4`
- `prankstar_bot_wakeup.mp4`
- `prankstar_bot_thinking.mp4`
- `prankstar_bot_ecstatic.mp4`
- `prankstar_bot_shutdown.mp4`

The mascot is presented through a compact neon assistant card, not as a full-screen background. Current placements are:

- Core/Home: tap-open overlay from the reactor readout, preserving reactor controls.
- Voice Lab / Joke Gen: below the Joke Gen header and above the creation controls.

The existing custom headers, bottom dock, reactor, Sound Stash, bundled prank sounds, Voice Lab, and Sound Forge remain intact. MP4 robot playback is muted by default, can be disabled with the Animated Bot setting, and falls back to the static Prankstar image when the raw MP4 resources are absent.

## Ultimate Reactor Integration

Core/Home renders `UltimateReactorScreen`, a native Compose implementation based on `prankstar_reactor_ultimate.html` and now prepared for the new Home/Core MP4 direction.

Preserved global UI:

- Custom bottom dock remains visible and routes Core, Stash, Forge, Jokes, and System.
- Sound Stash, Voice Lab/Jokes, Forge, System, Timer, Randomizer, Packs, and Messages routes remain registered.
- `sound_catalog.json` and audio assets were not modified.
- NEO/Prankstar Bot is preserved as a compact tap-open overlay from the reactor readout.

New Core visual assets are drawn natively:

- Top reactor status bar
- Canvas reactor rings, arcs, LEDs, cogs, face, console, VU, radar, and waveform motifs
- Left/right side strips
- Local CORE / MODE / SENSOR / LOG panel
- MP4-ready Home/Core background layer through `PrankstarVideoBackground`
- MP4-ready top Home/Core banner through `PrankstarHeaderVideo`
- Native FX/ripple overlay through `PrankstarFxOverlay`
- Floating action controls through `PrankstarFloatingControls`

Runtime screenshots for the previous Core screen are in `qa/screenshots/ultimate_reactor_*.png`.

## Home/Core MP4 Asset Pass

Implemented:

- Added Media3-based raw video playback components for Home/Core background and header banner.
- Wired Home/Core to use the requested raw resource names `prankstar_bg` and `prankstar_header` when those resources exist.
- Preserved the existing ultimate reactor, real sound deployment, global Stop All, Library, Voice Lab, Forge, System, global dock, and NEO bot panel.
- Added an explicit floating action strip with real callbacks for Stash, Jokes, Deploy, Stop All, and Forge.
- Added a Compose FX overlay for native ripple behavior over the video/scrim background.
- Added `mp4` to Android no-compress resource handling.

Not implemented because source assets were missing:

- None for asset import. Project-root sources were found and copied.

Imported paths:

- `./prankstar_bg.mp4` -> `app/src/main/res/raw/prankstar_bg.mp4`
- `./prankstar_header.mp4` -> `app/src/main/res/raw/prankstar_header.mp4`
- `./prankstar_home.html` -> `docs/reference/prankstar_home.html`

Validation:

- `python tools\validate_sound_catalog.py`: PASS, 369 entries, 0 missing files.
- `node tools\advanced_validate.cjs`: PASS, checked 369 files.

No placeholder MP4 or fake HTML was created.
