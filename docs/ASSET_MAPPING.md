# Prankstar Visual Asset Mapping

Source assets supplied via `prankstar_headers/` are integrated into the
Android project as follows. Resource names are lowercase, `_`-separated,
and Android-valid.

| Source file                       | Android resource                                                | Used by |
|-----------------------------------|-----------------------------------------------------------------|---------|
| `prankstar_headers/prankstar_icon.png` | `app/src/main/res/drawable/prankstar_icon.png` (canonical source) | Launcher icon source — rasterized into `mipmap-{m,h,xh,xxh,xxxh}dpi/ic_launcher.png` and `ic_launcher_round.png` |
| `prankstar_headers/prankstar_sn1.png`  | `app/src/main/res/drawable/prankstar_sn1.png`                  | `PrankstarHeader(variant = SN1)` — used at the top of `HomeScreen` |
| `prankstar_headers/prankstar_sn2.png`  | `app/src/main/res/drawable/prankstar_sn2.png`                  | `PrankstarHeader(variant = SN2)` — used at the top of `LibraryScreen` and `SequenceBuilderScreen` |
| `prankstar_headers/prankstar_sn3.png`  | `app/src/main/res/drawable/prankstar_sn3.png`                  | `PrankstarHeader(variant = SN3)` — used at the top of `RandomizerScreen` |
| `prankstar_headers/prankstar_core.png` | `app/src/main/res/drawable/prankstar_core.png`                 | Decorative backdrop layer inside the central core of `ReactorCorePanel` (does NOT replace the interactive reactor; it sits behind the HUD crosshair / icons and inherits the same `corePulse` scale animation) |

## Launcher icons

Generated from `prankstar_icon.png` at standard launcher dimensions
(48 / 72 / 96 / 144 / 192 px) via `tools/gen_launcher_icons.py`.
Both square `ic_launcher.png` and circular `ic_launcher_round.png` are
emitted per density. The `AndroidManifest.xml` `application` element
references `@mipmap/ic_launcher` and `@mipmap/ic_launcher_round`.

## UI preservation

The new assets only **add** decoration. They do not remove or replace:

- the interactive reactor core (animations, drag-to-rotate-category,
  long-press to charge, tap-to-deploy, error/cooldown states),
- the existing `WaveformHeader`,
- neon glass cards,
- the bottom navigation dock,
- Sound Forge / Randomizer / Sequence Builder / Library / Settings UIs,
- audio playback or `sound_catalog.json`.
