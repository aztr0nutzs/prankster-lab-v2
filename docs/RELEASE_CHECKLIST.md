# Release Checklist

Last updated: 2026-05-11

## Release Position

Do not claim production-ready status yet. The debug APK builds through the Windows SDK/JDK flow and audio validators pass, but live device QA has not completed in the latest pass.

## Build

- [x] Run Android environment check before Gradle
- [ ] Linux/devcontainer `./scripts/build-android-debug.sh` passes
- [x] Windows `.\scripts\build-android-debug.ps1` passes
- [x] APK exists at `app/build/outputs/apk/debug/app-debug.apk`
- [ ] Build warnings reviewed and triaged
- [ ] Release signing build validated

## Validators

- [x] `python3 tools/validate_sound_catalog.py`
- [x] `node tools/advanced_validate.cjs`
- [ ] Optional broader validator sweep reviewed
- [ ] Dependency audit triaged

## Install

- [ ] `adb devices` lists a target
- [ ] `adb install -r app/build/outputs/apk/debug/app-debug.apk`
- [ ] App launches through monkey command
- [ ] No launch crash in logcat

## Screen QA

- [ ] Home/Core loads, header/core visible, dock active Core
- [ ] Library loads 369 catalog sounds, search/filter works
- [ ] Sound Forge loads and controls remain accessible
- [ ] Voice Lab loads, preset library visible
- [ ] Randomizer starts/stops
- [ ] Timer selects sound and counts down
- [ ] Packs shows real pack counts and opens Library
- [ ] Settings diagnostics load
- [ ] Prank Messages opens without sending SMS/share during QA

## Audio QA

- [ ] Home reactor play/stop
- [ ] Library play/stop five sounds
- [ ] No filename-reader audio
- [ ] Randomizer plays only valid sounds
- [ ] Timer playback works
- [ ] Packs preview plays random valid pack sound
- [ ] Stop All / kill switch behavior verified

## Generated Voice QA

- [ ] TTS unavailable path shows error without crash
- [ ] TTS ready path enables Generate
- [ ] Generated output file exists and is non-empty
- [ ] Preview generated voice works
- [ ] Save to Library works
- [ ] Saved generated voice appears in Library
- [ ] Generated voice can be played from Library
- [ ] Generated voice can be selected in Timer
- [ ] Randomizer include/exclude generated voice toggle works
- [ ] Settings generated clip count is correct

## Screenshot QA

Capture current screenshots to `qa/screenshots/`:

- [ ] `home_core.png`
- [ ] `library.png`
- [ ] `library_playing.png`
- [ ] `forge.png`
- [ ] `voice_lab.png`
- [ ] `voice_lab_generated.png`
- [ ] `randomizer.png`
- [ ] `timer.png`
- [ ] `packs.png`
- [ ] `settings.png`
- [ ] `messages.png`

## Safety Review

- [x] UI warns against real-person and official-alert impersonation
- [x] Voice Lab does not claim MP3 export
- [x] Generated clips are not classified as bundled assets
- [ ] Device QA confirms safety warnings are visible
- [ ] SMS/share flows are tested without sending messages

## Legacy / Inactive Areas

- Sequence Builder code remains in the repository but no active `sequence` route is registered in `PranksterApp.kt`.
- Do not list Sequence Builder as a release-critical active feature unless the route is restored and live QA passes.
