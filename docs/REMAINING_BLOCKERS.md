# REMAINING_BLOCKERS.md

Last updated: 2026-06-01

## Build Status

PASS.

Commands run:

- `bash scripts/android-env-check.sh`
- `bash scripts/build-android-debug.sh`

`assembleDebug` completed successfully in 51 seconds.

Ultimate Reactor follow-up build also passed with `bash scripts/build-android-debug.sh`.

Notes:

- The shell scripts were normalized to LF line endings so Bash can execute them.
- `scripts/android-env-check.sh` now reads `local.properties` and translates Windows SDK paths when needed.
- `scripts/build-android-debug.sh` now falls back to `gradlew.bat` through `cmd.exe` when only Windows Java is available from Bash.

Build warnings remain:

- Deprecated TTS override warning in `AndroidTextToSpeechEngine`.
- Existing unused `onBack` parameter warning in Sound Forge.
- Existing Java 8 target warning under JDK 21.

## Validator Status

PASS.

Commands run:

- `python3 tools/validate_sound_catalog.py` failed because `python3` is not installed on PATH.
- `python tools/validate_sound_catalog.py` passed.
- `node tools/advanced_validate.cjs` passed.

Validator results:

- Catalog entries: 369
- Missing files: 0
- Unsupported extensions: 0
- Bad headers: 0
- UTF-8 corrupted: 0
- Uncataloged on disk: 0
- Orphan catalog rows: 0
- Advanced validator checked 369 files with 0 warnings ignored.

## Runtime QA Status

Partial PASS for the Ultimate Reactor follow-up. ADB was available through the Android SDK and device `RFCT70ET5TF` was attached.

Captured:

- `qa/screenshots/ultimate_reactor_idle.png`
- `qa/screenshots/ultimate_reactor_power_off.png`
- `qa/screenshots/ultimate_reactor_playing.png`
- `qa/screenshots/ultimate_reactor_tab_core.png`
- `qa/screenshots/ultimate_reactor_tab_mode.png`
- `qa/screenshots/ultimate_reactor_tab_sensor.png`
- `qa/screenshots/ultimate_reactor_tab_log.png`
- `qa/screenshots/ultimate_reactor_strip_actions.png`
- `qa/ultimate_reactor_logcat.txt`

Observed:

- Core/Home launched after the existing boot video.
- Ultimate Reactor rendered natively.
- Power off updated topbar/offline state and dimmed reactor.
- Reactor tap played real catalog sounds and updated current sound/readout.
- Local tabs opened.
- Side strip taps completed without crash.

Screens that still need broader runtime verification:

- Sound Stash load/search/filter/play/favorite/timer shortcut
- Forge generate/preview/save-to-Stash
- Jokes generate/preview/stop/save-to-Stash
- System generated cleanup and diagnostics

## Library Crash Status

No Library crash was reproduced during static inspection. Runtime launch still needs device/emulator verification before calling this production ready.

## Voice Lab TTS Limitations

Voice Lab depends on the Android device text-to-speech engine. If no TTS engine or language data is installed, generation is disabled and the screen reports the unavailable state.

Generated output is labeled as WAV/PCM, not MP3. Save is blocked unless the generated file exists and is non-empty.

## Dock/Header Visual Risks

The custom dock image is preserved. Active tab overlay was strengthened, but final judgment requires screenshots at phone widths, especially 360dp, to confirm Jokes does not appear active on every route and labels do not wrap.

Screen headers are preserved. Visual QA should confirm baked text and overlay text remain legible on small devices.

## Not Yet Tested

- Manual playback of at least five bundled sounds
- Saved Voice Lab clip appearing in Stash after app restart
- Saved Forge clip appearing in Stash after app restart
- Settings generated cleanup deleting generated metadata and internal files
- Screenshot capture for Core, Stash, Forge, Jokes, and System
