# Home Screen HTML Reference Mapping

The actual `prankstar_home.html` file was not available in the received attachments, so this mapping is based on the supplied task description of the HTML sections.

| HTML section/concept | Android component/file | Implemented behavior | Known difference from HTML |
| --- | --- | --- | --- |
| `#root` full-screen shell | `UltimateReactorScreen` in `app/src/main/java/com/pranksterlab/screens/UltimateReactorScreen.kt` | Full-screen Compose `Box` with video/fallback background, scrim, scanlines, content layers | Native Compose, not DOM |
| background/stage | `PrankstarVideoBackground` | Uses `prankstar_bg` raw resource if present, muted looping crop video with fallback | Missing source asset currently blocks actual MP4 display |
| top header | `PrankstarHeaderVideo` | Uses `prankstar_header` raw resource if present, compact muted looping banner | Missing source asset currently falls back to existing header image |
| prank counter | `UltimateReactorState.prankCount`, `UltimateReactorTopBar`, `UltimateReactorBottomPanel` | Increments on successful deploy through existing audio playback path | Layout follows existing reactor telemetry rather than unknown exact HTML coordinates |
| `#stage` / `#reactor-wrap` | Existing `UltimateReactorCanvas` | Preserves enhanced reactor tap/deploy, power, charge, overload, mode, and VU behavior | Uses existing native reactor rather than a static HTML image |
| `#fx-canvas` | `PrankstarFxOverlay` | Compose `Canvas` draws neon ripple rings while playing/charged/overloaded | Native animation timing differs from browser canvas |
| `#vu-left` / `#vu-right` | `UltimateReactorSideStrip` and `UltimateReactorVuMeter` | Animated side VU strips remain active around the reactor | Uses existing app VU design |
| floating controls | `PrankstarFloatingControls` | Real buttons for Stash, Joke Gen, Deploy, Stop All, and Forge | Native icon buttons replace web hit regions |
| reactor name/status marquee | `ReactorReadout` | Displays current sound/status and opens compact NEO panel on tap | Compact readout rather than full HTML marquee |
| clock/status behavior | `UltimateReactorTopBar` | Retains live system status, temp, battery, and online/offline indication | Live local clock is not added because topbar currently prioritizes reactor telemetry |
| dock area | Global `PrankstarBottomDock` in `PranksterApp` | Single app dock maps Core/Stash/Forge/Jokes/System routes | No duplicate local dock is added |
| tap/ripple/deploy | `UltimateReactorCanvas`, `triggerDeploy`, `PrankstarFxOverlay` | Reactor tap and Deploy button play real catalog sounds through `AudioPlayerController` | Ripple is Compose-native |
