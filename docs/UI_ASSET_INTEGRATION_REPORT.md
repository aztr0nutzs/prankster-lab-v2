# UI Asset Integration Report

## Packaged MP4 resources referenced by Home/Core

- `app/src/main/res/raw/prankstar_bg.mp4` — Home/Core background video.
- `app/src/main/res/raw/prankstar_header.mp4` — Home/Core header banner video.
- `app/src/main/res/raw/prankstar_boot.mp4` — startup boot video.

## Bot video resources

- This PR does **not** add bot MP4 binaries to `app/src/main/res/raw/`.
- `PrankstarBotVideo` still resolves animated bot videos through `PrankstarBotMood.videoResourceName()` and `videoResId(context)` using `prankstar_bot_*` raw resource names when those resources are already packaged by the repository/app.
- If a matching packaged bot raw resource is unavailable, the existing bot component falls back to its static rendering path instead of blocking Home/Core rendering.

## Runtime references

- Background and header videos are referenced through `R.raw.prankstar_bg` and `R.raw.prankstar_header`.
- Boot video is referenced by `MainActivity.kt` through `R.raw.prankstar_boot`.
- Bot video resource lookup remains runtime-based through the existing `PrankstarBotMood` mapping; no new binary bot asset is introduced by this change.

## Integration result

The Home/Core route now uses the existing packaged background/header assets directly in the visible runtime composition. Bot visibility is wired into Home/Core without adding new binary MP4 files to this PR.
