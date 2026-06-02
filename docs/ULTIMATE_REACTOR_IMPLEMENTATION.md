# ULTIMATE_REACTOR_IMPLEMENTATION.md

Last updated: 2026-06-01

## Source Reference

Native rebuild source: `prankstar_reactor_ultimate.html`.

The HTML was used as the visual and interaction reference for the top status bar, central reactor SVG, side strips, charge/alert overlays, power control, and local CORE / MODE / SENSOR / LOG bottom control zone.

## Files Created

- `app/src/main/java/com/pranksterlab/screens/UltimateReactorScreen.kt`
- `app/src/main/java/com/pranksterlab/components/reactor/ultimate/UltimateReactorState.kt`
- `app/src/main/java/com/pranksterlab/components/reactor/ultimate/UltimateReactorCanvas.kt`
- `app/src/main/java/com/pranksterlab/components/reactor/ultimate/UltimateReactorControls.kt`
- `app/src/main/java/com/pranksterlab/components/reactor/ultimate/UltimateReactorTopBar.kt`
- `app/src/main/java/com/pranksterlab/components/reactor/ultimate/UltimateReactorSideStrip.kt`
- `app/src/main/java/com/pranksterlab/components/reactor/ultimate/UltimateReactorBottomPanel.kt`
- `app/src/main/java/com/pranksterlab/components/reactor/ultimate/UltimateReactorKnob.kt`
- `app/src/main/java/com/pranksterlab/components/reactor/ultimate/UltimateReactorRadar.kt`
- `app/src/main/java/com/pranksterlab/components/reactor/ultimate/UltimateReactorVuMeter.kt`

## Files Modified

- `app/src/main/java/com/pranksterlab/screens/HomeScreen.kt`

`HomeScreen` now routes the Core/Home destination to `UltimateReactorScreen`. The previous Home implementation is retained privately as `LegacyHomeScreen` so existing reactor/audio code remains available for reference.

## JS Function Mapping

| HTML JS | Compose behavior |
| --- | --- |
| `togglePower()` | `togglePower()` in `UltimateReactorScreen`; updates `powered`, topbar status, temp/battery, dim state, log, and stops audio when powered off |
| `switchTab(id)` | `UltimateReactorControls`; updates `activeTab` and renders CORE / MODE / SENSOR / LOG panels |
| `setIntensity(v)` | CORE slider and CHAOS knob update `intensity` and `chaos` |
| `setMode(mode)` | MODE buttons update `UltimatePrankMode` and accent color |
| `setPrankType(type)` | MODE type buttons update `UltimatePrankType` |
| `deployPrank()` | `triggerDeploy()` increments count, raises charge, updates log/readout, flashes overload state, and plays a real catalog sound |
| `stripAction(action)` | Side strip actions toggle state, navigate to System/Voice Lab, charge spring, or trigger ZAP deploy |
| `toggleSw()` | SENSOR switches update the matching boolean flags and log messages |
| `knobTouchStart/Move` | `UltimateReactorKnob` uses drag gestures; sliders are also interactive |
| `onReactorClick/Touch` | Reactor canvas tap charges and deploys through the real audio path |

## Visual Sections Implemented

- HTML-style 40dp topbar with status dot, PRANK*STAR center label, temp, and battery.
- Native Canvas reactor with metallic rim, rim dashes, knurl ticks, LED rim, rotating cyan/magenta arcs, bolts, connector bracket, cogs, speaker grills, inner tick marks, waveform, green face, console labels, EQ bars, and charge/overload ripples.
- Left/right side strips with active buttons, LED columns, and animated VU meters.
- Charge strip and alert box overlays.
- Local CORE / MODE / SENSOR / LOG control zone.
- Compact NEO bot panel is preserved as a tap-open overlay from the readout.
- Global bottom dock remains unchanged.

## Audio Integration

Deploy and reactor tap use the existing `AudioPlayerController.playPrankSound()` path. Sounds are loaded from `SoundRepository.getBundledSounds()` plus custom sounds from `getCustomSoundsFlow()`.

Selection filters:

- Playable sounds only via `SoundRepository.isSoundPlayable()`
- Safe random candidates preferred via `isSafeForRandomMode`
- Type-based category hints for SPLASH, SOUND, SMOKE, and ZAP
- Generated voice clips accepted for SOUND

Power off calls `audioPlayerController.stopAll()`.

## Navigation Integration

The global dock remains: Core, Stash, Forge, Jokes, System.

Side strip mappings:

- Audio: toggles local audio mod state
- Gear: navigates to `system`
- Spring: adds charge/ripple state
- Holo: toggles holo projector state
- Zap: arms ZAP and deploys
- AI: toggles AI and opens `voice_lab` when enabled

## Known Differences From HTML

- The reactor is recreated with native Canvas primitives rather than exact SVG path geometry.
- Some fine SVG decorative details are approximated to keep Compose drawing performant.
- Eye pupil tracking from drag/touch is not implemented yet.
- Animation intensity settings are not fully wired into every new reactor animation.
- The NEO bot is a compact tap-open overlay instead of always consuming Core screen height.
- Lower control panels use vertical scroll where needed so controls remain accessible above the global dock.

## Performance Notes

- Main animation drivers use `rememberInfiniteTransition`.
- Canvas loops draw deterministic primitives without loading image assets.
- No WebView, raw HTML, or static screenshot is used.
- Runtime showed expected first-run Compose/JIT skipped-frame warnings, but no app crash during QA.

## QA Checklist

- [x] Native Compose screen, no WebView
- [x] Topbar updates for online/offline, temp, and battery
- [x] Animated native Canvas reactor
- [x] Power toggle works and stops playback
- [x] Charge strip updates
- [x] Side strips render and actions execute without crash
- [x] CORE / MODE / SENSOR / LOG tabs render
- [x] Knobs/sliders are interactive
- [x] Mode/type buttons are interactive
- [x] Deploy increments counter/log/charge and plays real audio
- [x] Sensor radar/VU/wave visuals render
- [x] Existing global dock remains visible
- [x] Sound catalog validators pass
- [x] Debug build passes
- [x] Runtime screenshots captured on attached device
