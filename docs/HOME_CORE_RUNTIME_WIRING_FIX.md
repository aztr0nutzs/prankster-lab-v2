# Home/Core Runtime Wiring Fix

## Actual Home route before/after

- `PranksterApp` routes `home` to `HomeScreen(audioPlayerController, soundRepository, onNavigate)`, and `HomeScreen` delegates to `UltimateReactorScreen`.
- The routed composable was therefore not a missing route. The problem was that the visible runtime Home/Core root still had weak visibility and hidden feature wiring.
- After this fix, the same actual `home` route renders the corrected `UltimateReactorScreen` with the MP4 background, MP4 header banner, always-visible compact NEO bot panel, reactor mode chooser, HTML-inspired stage overlays, VU strips, floating controls, and the existing global dock.

## Why previous changes were not visible

- `PrankstarVideoBackground` was mounted, but the Home/Core root placed a heavy black/blue vertical scrim over it, making `prankstar_bg.mp4` barely visible.
- `PrankstarHeaderVideo` was mounted, but an additional `UltimateReactorTopBar` appeared above it and competed with it visually.
- `PrankstarBotVideo` was present only behind a `showBot` toggle and started hidden, so no robot/avatar was visible on first Home render.
- The reactor chooser did not exist, so only the ultimate presentation was available.

## Background visibility fix

- `prankstar_bg.mp4` exists at `app/src/main/res/raw/prankstar_bg.mp4`.
- Home/Core still references `R.raw.prankstar_bg` through `PrankstarVideoBackground`.
- The Home/Core root now layers the background first and uses a reduced black/blue scrim in the requested approximate range instead of an opaque or near-opaque panel.
- Stage panels are translucent on Home/Core so they do not bury the video.

## Header visibility fix

- `prankstar_header.mp4` exists at `app/src/main/res/raw/prankstar_header.mp4`.
- Home/Core still references `R.raw.prankstar_header` through `PrankstarHeaderVideo`.
- The old competing top bar was removed from the Home/Core composition only.
- The MP4 banner is mounted at the top of the actual Home/Core route with the existing 88 dp height.

## Bot placement

- `PrankstarBotVideo` is now always mounted on Home/Core as a compact assistant panel near the upper-right of the stage.
- The default message is `Ready to deploy chaos.`.
- Mood mapping is driven by runtime playback and reactor state:
  - powered off: `SHUTDOWN`
  - overloaded: `WARNING`
  - playback error: `CONFUSED`
  - playing: `PLAYING`
  - ready/idle: `HAPPY` or `ARMED`
- This cleanup intentionally does not add bot MP4 binaries to the PR; animated bot playback uses already-packaged `prankstar_bot_*` raw resources when present, and otherwise the existing bot component falls back to static rendering.

## Reactor chooser modes

The Home/Core route now includes compact `REACTOR MODE` chips:

1. `ULTIMATE` — HTML-inspired full central stage with side strips, charge readout, floating controls, and ultimate reactor canvas.
2. `CLASSIC` — existing `ReactorCorePanel` / `PranksterCoreReactor` presentation, using the same deploy and stop callbacks.
3. `COMPACT` — smaller translucent chaos core card using the ultimate canvas and same playback callbacks.
4. `VISUALIZER` — VU/ripple-focused layout with left/right bars and a central audio-reactive canvas.

Mode selection is persisted for the active composition/session via `rememberSaveable`. It is not yet persisted to DataStore.

## Boot sequence status

- Boot video asset exists at `app/src/main/res/raw/prankstar_boot.mp4`.
- Boot code exists in `MainActivity.kt` as `PrankstarBootSequence`.
- Launch flow still calls the boot sequence on a fresh Activity creation (`savedInstanceState == null`) and transitions to `PranksterApp` on completion or video error.
- No boot routing change was required.

## HTML design mapping improvements

- MP4 background is the bottom layer.
- Dark scrim and scanline overlays remain light enough for visibility.
- MP4 banner is the Home/Core header.
- Central reactor stage remains the focal element.
- Left/right VU strips are available in `VISUALIZER` mode.
- FX/ripple overlay remains mounted above stage content.
- Floating controls remain wired to real callbacks:
  - Deploy: random playable catalog sound
  - Stop: `AudioPlayerController.stopAll()`
  - Stash: `library`
  - Jokes: `voice_lab`
  - Forge: `forge`
- The existing global `PrankstarBottomDock` is retained and no second bottom dock was added.

## Build result

- `git diff --check` passed.
- Android build could not be attempted because the required SDK environment check failed in this cloud/browser environment.
- `local.properties` exists but points to a Windows SDK path that is not present in this Linux container.
- `ANDROID_HOME` and `ANDROID_SDK_ROOT` are empty.

## Validators

- `python tools/validate_sound_catalog.py` passed.
- `node tools/advanced_validate.cjs` passed.

## Screenshots / runtime QA

- No runtime screenshots were captured because no ADB device/emulator was attached in this environment.

## Remaining blockers

- Configure a real Android SDK in the container or run locally with a valid SDK path before executing `./gradlew assembleDebug --stacktrace --console=plain`.
- Device/emulator visual QA remains required to confirm exact mobile layout spacing and screenshot evidence.
