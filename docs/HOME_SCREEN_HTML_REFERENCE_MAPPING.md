# Home Screen HTML Reference Mapping

Reference: `docs/reference/prankstar_home.html`

## Runtime mapping

| HTML-inspired element | Android runtime implementation | Status |
| --- | --- | --- |
| Animated background | `PrankstarVideoBackground` using `R.raw.prankstar_bg` | Mounted on actual `home` route |
| Top banner | `PrankstarHeaderVideo` using `R.raw.prankstar_header` | Mounted as Home/Core top banner |
| Prank counter | `UltimateReactorState.prankCount` in the reactor bottom controls/readouts | Preserved |
| Central reactor stage | `UltimateReactorCanvas`, `ReactorCorePanel`, compact canvas, or visualizer canvas depending on selected mode | Added selectable modes |
| Left/right VU strips | `VisualizerStage` side strips | Added in visualizer mode |
| FX/ripple overlay | `PrankstarFxOverlay` | Mounted above stage content |
| Floating controls | `PrankstarFloatingControls` | Real deploy/stop/navigation callbacks |
| Reactor name/status readout | `ReactorReadout` | Visible in every mode |
| Robot avatar / bot | `PrankstarBotVideo` | Always visible compact Home assistant |
| Bottom navigation | Existing global `PrankstarBottomDock` | Retained; no duplicate Home dock added |

## Mode behavior

- `ULTIMATE` preserves the original ultimate stage and side strips.
- `CLASSIC` renders the existing classic reactor path so old reactor behavior is not bypassed.
- `COMPACT` uses a smaller high-contrast reactor card for tight mobile screens.
- `VISUALIZER` emphasizes VU bars and ripple energy for an audio-reactive presentation.

All modes call the same real deploy and stop functions, so they do not create fake controls.
