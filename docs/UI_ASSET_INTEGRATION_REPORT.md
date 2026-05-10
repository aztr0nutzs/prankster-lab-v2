# UI Asset Integration Report

Date: 2026-05-09 (UTC)

## Imported header assets
- `app/src/main/res/drawable/header_sound_gen.png`
- `app/src/main/res/drawable/header_sound_stash.png`
- `app/src/main/res/drawable/header_joke_gen.png`
- `app/src/main/res/drawable/header_settings.png`

## Imported dock asset
- `app/src/main/res/drawable/prankstar_dock_main.png`

## Screen-to-header map (code-verified)
- Core / Home (`home`) → no image header component, uses reactor core hero layout (`HomeScreen.kt`).
- Library / Sound Stash (`library`) → `header_sound_stash`.
- Sound Packs (`lab`) → `header_sound_stash`.
- Sound Forge / Sound Gen (`forge`) → `header_sound_gen`.
- Randomizer (`randomizer`) → `header_sound_gen`.
- Timer (`timer`) → `header_sound_gen`.
- Voice Lab / Joke Gen (`voice_lab`) → `header_joke_gen`.
- Prank Messages (`messages`) → `header_joke_gen`.
- Settings (`system`) → `header_settings`.
- Diagnostics → embedded in Settings screen diagnostics panel (no standalone route).

## Bottom dock route map
Dock component: `PrankstarBottomDock`
- `home` -> HOME
- `library` -> LIBRARY
- `forge` -> FORGE
- `lab` -> PACKS
- `system` -> SYSTEM

## Active state solution
`PranksterApp.kt` normalizes nested routes to dock routes before binding to the dock:
- `timer`, `sequence`, `randomizer`, `voice_lab`, `messages` all map active tab to `forge`.
This prevents misleading active states for forge-adjacent tools.

## QA findings (static code audit)
- Header assets are referenced by all major non-home screens listed above.
- `PrankstarHeader` uses content scale `Fit` and fixed height, reducing stretch risk.
- Header text in `PrankstarHeader` remains concise and positioned with gradient scrim.
- Scaffold + content padding keeps content below headers and above dock.
- Found/fixed one empty click handler in `components/TopBar.kt` (settings button now parameterized callback).
- No missing drawable references after adding required drawable resources.
- No sequence/legacy dock references found in Compose navigation path.

## Build result
- Android environment check script: **FAILED** due missing Android SDK path.
- Gradle `assembleDebug`: **FAILED** for invalid/missing SDK directory (`sdk.dir` does not exist in current environment).

## Audio validator result
- `python3 tools/validate_sound_catalog.py`: PASS.
- `node tools/advanced_validate.cjs`: PASS.

## Known visual limitations
- Manual on-device visual confirmation for stretch/overlap/touch occlusion is blocked in this environment (no usable Android SDK/device runtime).
- Report conclusions are from source-level verification and asset existence checks.
