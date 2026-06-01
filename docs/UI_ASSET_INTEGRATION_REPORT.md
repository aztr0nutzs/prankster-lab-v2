# UI Asset Integration Report

Last updated: 2026-05-11

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
| `home` | Core / Reactor | `prankstar_sn1` through `PrankstarHeader` |
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
- Debug build through PowerShell wrapper: PASS
- Runtime visual QA on device/emulator: BLOCKED, no attached device was available during the latest pass

No audio assets or `sound_catalog.json` were changed for UI integration.
