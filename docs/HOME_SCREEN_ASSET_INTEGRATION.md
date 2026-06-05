# Home Screen Asset Integration

## Uploaded Assets

| Original upload name | Android resource/reference name | Target path | Current status | Use |
| --- | --- | --- | --- | --- |
| `prankstar_bg.mp4` | `R.raw.prankstar_bg` | `app/src/main/res/raw/prankstar_bg.mp4` | Imported from project root | Home/Core full-screen animated background |
| `prankstar_header.mp4` root variant for requested `1000056777.mp4` | `R.raw.prankstar_header` | `app/src/main/res/raw/prankstar_header.mp4` | Imported from project root | Home/Core top video banner |
| `prankstar_home.html` | `docs/reference/prankstar_home.html` | `docs/reference/prankstar_home.html` | Imported from project root | Design reference only, no WebView |

## Source Paths Found

- `./prankstar_bg.mp4`
- `./prankstar_header.mp4`
- `./prankstar_home.html`

## Runtime Integration

- `PrankstarVideoBackground` references `R.raw.prankstar_bg`, mutes it, loops it, crops it to portrait, and releases Media3 ExoPlayer on dispose.
- `PrankstarHeaderVideo` references `R.raw.prankstar_header`, mutes it, loops it, crops it inside a compact 88dp banner, and releases Media3 ExoPlayer on dispose.
- Both video components respect the persisted `animation_intensity` setting. `MINIMAL` uses static fallback.
- `app/build.gradle.kts` now marks `mp4` as uncompressed so raw video resources can stream correctly.

## Verification

- Raw MP4 resources exist in `app/src/main/res/raw/`.
- Reference HTML exists in `docs/reference/`.
- The HTML reference is not loaded in runtime and no WebView is used.
- Code references `R.raw.prankstar_bg` and `R.raw.prankstar_header`.
- `.\gradlew.bat assembleDebug --stacktrace --console=plain` passed after the clean build script was blocked by a locked Windows `app/build` file.
- `python tools\validate_sound_catalog.py` passed: 369 catalog entries, 0 missing files, 0 bad headers.
- `node tools\advanced_validate.cjs` passed: checked 369 files.
- ADB was available through the SDK, but no device/emulator was attached, so runtime screenshots were not captured.
