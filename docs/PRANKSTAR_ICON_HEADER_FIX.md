# Prankstar Icon and Header Fix

Last updated: 2026-06-07

## Asset search result

Required search found:

- `prankstar_icon.png` at repository root, 2563132 bytes.
- `bored1.mp4`, `bored2.mp4`, `searching.mp4`, `relaxed.mp4`, `powerup.mp4`, `sad.mp4`, and `searching2.mp4` at repository root.
- Existing `app/src/main/res/drawable/prankstar_icon.png`, but it had a different SHA-256 from the root icon.

Required search did not find `prankstar_header.png` by exact filename. A deeper full recursive search also found no exact `prankstar_header.png`. The only matching PNG header source present is `prankster_header.png`, 683211 bytes. It was copied to the required Android resource destination as `app/src/main/res/drawable/prankstar_header.png` and is documented as a source-name mismatch.

## Launcher icon implementation

Root `prankstar_icon.png` was copied exactly to:

- `app/src/main/res/drawable/prankstar_icon.png`
- `app/src/main/res/drawable/ic_launcher.png`
- `app/src/main/res/drawable/ic_launcher_round.png`
- all existing `app/src/main/res/mipmap-*/ic_launcher*.png` files

`app/src/main/AndroidManifest.xml` now uses:

- `android:icon="@drawable/ic_launcher"`
- `android:roundIcon="@drawable/ic_launcher_round"`

SHA-256 verification confirmed the root icon, drawable icon, drawable launcher icon, and drawable round launcher icon match.

## Header PNG implementation

Because the exact source file `prankstar_header.png` was absent, `prankster_header.png` was copied to:

- `app/src/main/res/drawable/prankstar_header.png`

SHA-256 verification confirmed the copied Android drawable matches `prankster_header.png`.

Native `PrankstarHeader` call sites now pass `R.drawable.prankstar_header`. The reusable header:

- uses full width
- uses `ContentScale.Fit`
- caps height at 88dp to 96dp
- defaults to no text overlay, preventing duplicate title text over the banner art
- keeps the dark framed neon treatment

`PrankstarHeaderVideo` now renders the PNG header art instead of the old header MP4 path. Stable V9 Home remains WebView-owned and does not receive a stacked native PNG header.

## Validation

- `git diff --check`: passed; Git reported line-ending conversion warnings only.
- `.\scripts\android-env-check.ps1`: passed.
- `.\gradlew.bat assembleDebug --stacktrace --console=plain`: passed.
- `python tools\validate_sound_catalog.py`: passed, 369 catalog entries.
- `node tools\advanced_validate.cjs`: passed, 369 files checked.

## Runtime QA

ADB is installed, but `adb devices` reported no attached devices. Runtime launcher-icon/header screenshots were not captured.
