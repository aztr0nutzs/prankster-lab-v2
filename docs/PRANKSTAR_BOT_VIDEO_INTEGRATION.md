# Prankstar Bot / NEO Video Integration

Last updated: 2026-06-01

## Imported MP4 File List

The NEO robot clips are expected at Android raw resource names under `app/src/main/res/raw/`, but the binary MP4 files are not duplicated in this commit because the source clips already live at the repository root. When a raw resource is not packaged, the bot safely uses the static fallback instead of crashing.

| Original clip | Android raw resource path |
| --- | --- |
| `processing.mp4` | `app/src/main/res/raw/prankstar_bot_processing.mp4` |
| `celebrate.mp4` | `app/src/main/res/raw/prankstar_bot_celebrate.mp4` |
| `typing.mp4` | `app/src/main/res/raw/prankstar_bot_typing.mp4` |
| `warning.mp4` | `app/src/main/res/raw/prankstar_bot_warning.mp4` |
| `confused.mp4` | `app/src/main/res/raw/prankstar_bot_confused.mp4` |
| `happy.mp4` | `app/src/main/res/raw/prankstar_bot_happy.mp4` |
| `surprised.mp4` | `app/src/main/res/raw/prankstar_bot_surprised.mp4` |
| `angry.mp4` | `app/src/main/res/raw/prankstar_bot_angry.mp4` |
| `wakeup.mp4` | `app/src/main/res/raw/prankstar_bot_wakeup.mp4` |
| `thinking.mp4` | `app/src/main/res/raw/prankstar_bot_thinking.mp4` |
| `ectastic.mp4` | `app/src/main/res/raw/prankstar_bot_ecstatic.mp4` |
| `shutdown.mp4` | `app/src/main/res/raw/prankstar_bot_shutdown.mp4` |

The source file `ectastic.mp4` keeps its root filename but maps to the corrected Android resource name `prankstar_bot_ecstatic` when/if copied into `res/raw`.

## Mood Mapping

`PrankstarBotMood.videoResId()` maps app state to clip resources as follows:

| Mood | Clip resource |
| --- | --- |
| `IDLE` | `prankstar_bot_happy` |
| `WAKEUP` | `prankstar_bot_wakeup` |
| `ARMED` | `prankstar_bot_happy` |
| `PLAYING` | `prankstar_bot_happy` |
| `PROCESSING` | `prankstar_bot_processing` |
| `GENERATING` | `prankstar_bot_processing` |
| `THINKING` | `prankstar_bot_thinking` |
| `TYPING` | `prankstar_bot_typing` |
| `LISTENING` | `prankstar_bot_typing` |
| `HAPPY` | `prankstar_bot_happy` |
| `SAVED` | `prankstar_bot_celebrate` |
| `CELEBRATING` | `prankstar_bot_celebrate` |
| `ECSTATIC` | `prankstar_bot_ecstatic` |
| `SURPRISED` | `prankstar_bot_surprised` |
| `WARNING` | `prankstar_bot_warning` |
| `ERROR` | `prankstar_bot_confused` |
| `CONFUSED` | `prankstar_bot_confused` |
| `ANGRY` | `prankstar_bot_angry` |
| `SHUTDOWN` | `prankstar_bot_shutdown` |

## Screens Using the Bot

- **Core/Home:** compact NEO assistant card is placed below the waveform header and above the reactor. It does not replace or cover the reactor. The mood follows wakeup, catalog scanning, playback, and playback-error states.
- **Voice Lab / Joke Gen:** compact NEO assistant card is placed below the Joke Gen header and above the preset/text input flow. The mood follows TTS initialization, typing, generation, preview, generated, saved, and error states.

Sound Forge, Library, and Settings-specific assistant placements are not added in this pass to avoid clutter and keep the PR focused on Core/Home and Voice Lab.

## Playback Implementation

- `PrankstarBotVideo` uses AndroidX Media3 ExoPlayer and `PlayerView` through Compose `AndroidView` when the mapped raw resource is packaged.
- Clips loop with `Player.REPEAT_MODE_ALL`.
- Clips are muted by default to avoid conflicting with prank sound playback.
- Player controls are disabled.
- `AspectRatioFrameLayout.RESIZE_MODE_FIT` preserves the robot aspect ratio.
- The ExoPlayer instance is remembered for the visible composable and released in `DisposableEffect.onDispose`.
- Media items are updated only when the effective mood resource changes.

## Fallback Behavior

- If a mapped raw resource is missing, the card uses the static fallback immediately. If a packaged mood clip fails during playback, the player retries with `prankstar_bot_happy`.
- If the happy clip is missing or also fails, the card switches to a static drawable fallback.
- Static fallback lookup prefers `R.drawable.prankstar_bot` if such a drawable is later added; otherwise it uses the existing `R.drawable.prankstar_sn1` image.
- The component is designed not to crash the app due to a video playback error.

## Settings Toggle Behavior

Settings now includes **Animated Bot** under Animation Intensity.

- **Animated Bot On:** MP4 playback is allowed unless animation intensity is `MINIMAL`.
- **Animated Bot Off:** the bot card uses the static fallback image.
- **Animation Intensity = MINIMAL:** static fallback is used even when Animated Bot is on.
- **Animation Intensity = REDUCED:** MP4 playback remains enabled, but extra pulse/glow animation is reduced.
- **Animation Intensity = FULL:** MP4 playback plus neon pulse/glow effects are enabled.

The Animated Bot setting is persisted through DataStore.

## Performance Notes

- The video player is only created when the bot card is composed and visible.
- Players are released on dispose to avoid unmanaged playback leaks.
- Video volume defaults to `0f`.
- The root-level NEO MP4 clips total approximately 33 MB; this follow-up removes duplicate `res/raw` binaries from the commit to avoid adding that APK/review weight until packaging is explicitly approved.
- Existing prank audio assets and `sound_catalog.json` were not modified.

## Known Limitations

- Manual device/emulator runtime QA and screenshots were not captured in this environment unless `adb` is available.
- The bot is integrated into Core/Home and Voice Lab first; Sound Forge, Library, and Settings mascot placements remain optional follow-up work.
