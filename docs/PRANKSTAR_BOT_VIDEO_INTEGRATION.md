# Prankstar Bot Video Integration

Last updated: 2026-06-07

## Imported MP4 files

The seven requested new robot clips were imported into `app/src/main/res/raw/`:

| Source clip | Android raw resource |
| --- | --- |
| `bored1.mp4` | `prankstar_bot_bored1` |
| `bored2.mp4` | `prankstar_bot_bored2` |
| `searching.mp4` | `prankstar_bot_searching` |
| `searching2.mp4` | `prankstar_bot_searching2` |
| `relaxed.mp4` | `prankstar_bot_relaxed` |
| `powerup.mp4` | `prankstar_bot_powerup` |
| `sad.mp4` | `prankstar_bot_sad` |

Existing root bot clips were also copied into raw resources so older moods continue to play:

- `happy.mp4` -> `prankstar_bot_happy`
- `wakeup.mp4` -> `prankstar_bot_wakeup`
- `processing.mp4` -> `prankstar_bot_processing`
- `thinking.mp4` -> `prankstar_bot_thinking`
- `typing.mp4` -> `prankstar_bot_typing`
- `celebrate.mp4` -> `prankstar_bot_celebrate`
- `ectastic.mp4` -> `prankstar_bot_ecstatic`
- `surprised.mp4` -> `prankstar_bot_surprised`
- `warning.mp4` -> `prankstar_bot_warning`
- `confused.mp4` -> `prankstar_bot_confused`
- `angry.mp4` -> `prankstar_bot_angry`
- `shutdown.mp4` -> `prankstar_bot_shutdown`

No old MP4 assets were deleted.

## Mood mapping

`PrankstarBotMood` now includes the requested moods:

- `BORED` -> `prankstar_bot_bored1`
- `BORED_ALT` -> `prankstar_bot_bored2`
- `SEARCHING` -> `prankstar_bot_searching`
- `SEARCHING_ALT` -> `prankstar_bot_searching2`
- `RELAXED` -> `prankstar_bot_relaxed`
- `POWERUP` -> `prankstar_bot_powerup`
- `SAD` -> `prankstar_bot_sad`

Existing moods remain mapped:

- startup/wakeup uses `WAKEUP` or `POWERUP`
- typing uses `TYPING`
- processing/generating uses `PROCESSING`
- playing uses `PLAYING`
- success uses `HAPPY`, `SAVED`, or `CELEBRATING`
- warning/refusal uses `WARNING`
- failure can use `SAD`, `ERROR`, or `CONFUSED`

Fallback resource chains:

- `SEARCHING` and `SEARCHING_ALT` fall back to `THINKING`, then `PROCESSING`.
- `BORED` and `BORED_ALT` fall back to `RELAXED`, then `HAPPY`.
- `SAD` falls back to `CONFUSED`.
- `POWERUP` falls back to `WAKEUP`.
- `RELAXED` falls back to `HAPPY`.

## Playback behavior

`PrankstarBotVideo` continues to use Media3 ExoPlayer through Compose `AndroidView`. Videos loop, are muted by default, and use `RESIZE_MODE_FIT` to preserve the robot aspect ratio. If a resource is missing or playback fails, the component falls back to another bot clip or the static drawable fallback instead of crashing.

## Validation

- `.\gradlew.bat assembleDebug --stacktrace --console=plain`: passed.
- No missing `R.raw` references were introduced; bot resources are resolved by raw resource name with fallback.

## Runtime QA

ADB is installed, but no device was attached. Bot mood runtime screenshots were not captured.
