# Stable V9 Screen Integration

## Asset paths copied
- `app/src/main/assets/prankstar/prankstar_new_home_bot_screen.html`
- `app/src/main/assets/prankstar/assets/prankstar_header.mp4`
- `app/src/main/assets/prankstar/assets/reactor1.mp4`
- `app/src/main/assets/prankstar/assets/reactor2.mp4`
- `app/src/main/assets/prankstar/assets/reactor3.mp4`
- `app/src/main/assets/prankstar/assets/reactor4.mp4`
- `app/src/main/assets/prankstar/assets/reactor5.mp4`
- `app/src/main/assets/prankstar/assets/reactor6.mp4`
- `app/src/main/assets/prankstar/assets/reactor7.mp4`
- `app/src/main/assets/prankstar/assets/bot/high.mp4`
- `app/src/main/assets/prankstar/assets/bot/scanning2.mp4`
- `app/src/main/assets/prankstar/assets/bot/powerup2.mp4`
- `app/src/main/assets/prankstar/assets/bot/dancing.mp4`
- `app/src/main/assets/prankstar/assets/bot/celebrate2.mp4`
- `docs/README_STABLE_V9_SURGICAL.md`

## WebView URL
- `file:///android_asset/prankstar/prankstar_new_home_bot_screen.html`

## Bridge methods
- `deployRandom()`
- `stopAll()`
- `openStash()`
- `openJokes()`
- `openForge()`
- `openSystem()`
- `setReactorMode(mode)`
- `logEvent(event)`

## HTML functions patched
- `mainDeploy()`
- `stashDeploy()`
- `forgeAction()`
- `jokeAction()`
- `reactorClick()`
- `switchDock()`
- `botPulse()`
- `sAct()`
- bridge helper routing via `callAndroid()`

## Navigation behavior
- `home` now opens the Stable V9 WebView screen.
- `library`, `voice_lab`, `forge`, and `system` remain native routes.
- `home_native` and `home_ultimate` remain as fallback routes.

## Audio integration
- Bridge deploy uses `SoundRepository` to choose a real bundled catalog sound.
- Selection respects the safe-random preference.
- Playback still goes through `AudioPlayerController`.
- Stop uses `AudioPlayerController.stopAll()`.

## Dock decision
- Native bottom dock is hidden on `home` so the HTML dock owns the Core screen.

## Boot sequence status
- Boot flow remains in `MainActivity` via `PrankstarBootSequence`.
- No boot assets were removed.

## Build result
- `assembleDebug` succeeded.

## Validator result
- `python tools/validate_sound_catalog.py` passed.
- `node tools/advanced_validate.cjs` passed.

## Runtime QA
- No `adb` device/emulator was available in this environment, so no runtime QA screenshots were captured.
