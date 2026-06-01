# REMAINING_BLOCKERS.md

Last updated: 2026-06-01

## Build Status

PASS.

Commands run:

- `bash scripts/android-env-check.sh`
- `bash scripts/build-android-debug.sh`

`assembleDebug` completed successfully in 51 seconds.

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

Blocked. ADB exists in the Android SDK, but no device or emulator is attached.

Blocked note: `qa/RUNTIME_QA_BLOCKED.md`

Screens that still need runtime verification:

- Core reactor and random safe playback
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
