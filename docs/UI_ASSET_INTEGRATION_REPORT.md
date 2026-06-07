# UI Asset Integration Report

Last updated: 2026-06-07

## Preserved identity

- Dark neon cyberpunk Prankstar/Prankster identity preserved.
- Stable V9 WebView Home/Core route preserved.
- Sound Stash/Library preserved.
- Voice Lab / Joke Gen preserved.
- Sound Forge preserved.
- Settings/System preserved.
- Existing audio playback preserved.
- Existing robot MP4 mascot system preserved and expanded.
- `sound_catalog.json` was not modified.
- App package name remains `com.pranksterlab`.

## Launcher icon

Root `prankstar_icon.png` was copied exactly to the Android drawable launcher resources. Manifest icon fields now point at:

- `@drawable/ic_launcher`
- `@drawable/ic_launcher_round`

Existing mipmap launcher PNGs were overwritten with the same root icon file so old default launcher art is not left as the active fallback.

## Header PNG

Native screens now use `R.drawable.prankstar_header` through `PrankstarHeader`. The reusable header uses full width, `ContentScale.Fit`, an 88dp to 96dp height band, no default text overlay, and a dark neon frame.

Exact source file `prankstar_header.png` was not present. The only header PNG source found was `prankster_header.png`; it was copied to the required Android resource path as `app/src/main/res/drawable/prankstar_header.png`.

Stable V9 WebView Home keeps its internal header and does not receive a stacked native banner.

## Bot video assets

The seven requested bot clips were imported into raw resources and mapped through `PrankstarBotMood`. Existing bot clips were also packaged under their expected `prankstar_bot_*` names so older moods remain functional.

## Bot assistant

The native bot panel remains compact and supports command input, quick chips, recommendation cards, generated joke text, Voice Lab handoff, safe playback, stop-all, and navigation actions. Voice Lab consumes bot drafts through the shared `PrankstarBotVoiceLabBridge`.

## Build and validation

- `git diff --check`: passed with line-ending warnings only.
- `.\scripts\android-env-check.ps1`: passed.
- `.\gradlew.bat assembleDebug --stacktrace --console=plain`: passed.
- `python tools\validate_sound_catalog.py`: passed, 369 entries.
- `node tools\advanced_validate.cjs`: passed, 369 files checked.

## Runtime QA

ADB is installed, but `adb devices` returned no attached devices. No runtime screenshots or logcat were captured.
