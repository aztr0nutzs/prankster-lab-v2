# Home Screen Asset Integration

## Uploaded Assets

| Original upload name | Android resource/reference name | Target path | Current status | Use |
| --- | --- | --- | --- | --- |
| `prankstar_bg.mp4` | `R.raw.prankstar_bg` | `app/src/main/res/raw/prankstar_bg.mp4` | Blocked: source file not found in attachments/workspace | Home/Core full-screen animated background |
| `1000056777.mp4` | `R.raw.prankstar_header` | `app/src/main/res/raw/prankstar_header.mp4` | Blocked: source file not found in attachments/workspace | Home/Core top video banner |
| `prankstar_home.html` | `docs/reference/prankstar_home.html` | `docs/reference/prankstar_home.html` | Blocked: source file not found in attachments/workspace | Design reference only, no WebView |

## Runtime Integration

- `PrankstarVideoBackground` looks up raw resource name `prankstar_bg`, mutes it, loops it, crops it to portrait, and releases Media3 ExoPlayer on dispose.
- `PrankstarHeaderVideo` looks up raw resource name `prankstar_header`, mutes it, loops it, crops it inside a compact 88dp banner, and releases Media3 ExoPlayer on dispose.
- Both video components respect the persisted `animation_intensity` setting. `MINIMAL` uses static fallback.
- `app/build.gradle.kts` now marks `mp4` as uncompressed so raw video resources can stream correctly.

## Fallbacks

Until the missing MP4 files are provided:

- Home/Core background falls back to the existing dark neon radial background and scrim.
- Header banner falls back to the existing `prankster_header` drawable.
- The HTML reference is not loaded in runtime and no WebView is used.
