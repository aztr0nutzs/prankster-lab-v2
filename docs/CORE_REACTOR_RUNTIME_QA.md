# Core Reactor Runtime QA

Date: 2026-06-01

## A. Build result

SDK preflight passed with `scripts/android-env-check.sh`.

Gradle debug build passed with `scripts/build-android-debug.sh`.

Build warnings remain for the manifest package attribute, Java 8 source/target on JDK 21, deprecated icons, and unused parameters. No build-blocking errors remain.

## B. APK path and size

`app/build/outputs/apk/debug/app-debug.apk`

Size after final rebuild: `86,392,306` bytes.

## C. Device/emulator used

Physical device via SDK-local ADB:

- Serial: `RFCT70ET5TF`
- Screen: `1080x2400`
- Package: `com.pranksterlab`

## D. Validator results

Python validator:

- Catalog entries: `369`
- Missing files: `0`
- Unsupported extensions: `0`
- Bad headers: `0`
- UTF-8 corrupted: `0`
- Uncataloged on disk: `0`
- Orphan catalog rows: `0`
- Result: `VALIDATION OK`

Node validator:

- Checked files: `369`
- Missing files: `0`
- Duplicate IDs: `0`
- Duplicate paths: `0`
- Result: `Validation PASSED`

Note: `python3` is not mapped in the Windows PowerShell environment, so the Python validator was run with `py -3`.

## E. Reactor idle result

Passed.

Home/Core launched after the boot video transition. The enhanced reactor, custom header, NEO assistant panel, prankstar core image, status pills, category controls, readout, quick actions, and bottom dock remained present.

The first immediate screenshot after launch can look black while the boot video is still transitioning. Waiting for the boot video to complete shows Home/Core correctly.

## F. Tap playback result

Passed.

Tapping the reactor center started real catalog playback. The reactor state changed to `PLAYING`, the readout updated with the current sound, animated waveform/halo visuals changed, and NEO assistant copy changed to deployed status.

Observed playback example: `Minion Voicewav`.

## G. Stop All result

Passed.

Tapping while playing stopped playback. Stop All also returned the reactor to an armed/stopped state. MediaPlayer release/reset lines were present in logcat.

## H. Long-press charge result

Passed.

Long press showed `CHARGING`, a yellow charge ring, percentage text, and the charge progress strip. Release triggered charged playback behavior. No double-fire crash was observed.

## I. Category selection result

Passed.

Category selection changed highlight and label. Selecting `ANIMAL` changed the readout to `ANIMAL` and playback selected an animal-category sound.

## J. Navigation button result

Bottom dock navigation passed:

- `STASH` opened Sound Stash / Library.
- `JOKES` opened Voice Lab / Joke Gen.
- `FORGE` opened Sound Forge.
- `SYS` opened Settings/System.

Reactor action-strip Stash navigation was also verified after scrolling the action strip into view. Isolated ADB coordinate retests for action-strip Jokes/Forge were inconclusive because the automated swipe did not reliably place the action strip under the tap point on fresh launch. The code wiring is present, but this specific row interaction should be manually checked if strict action-strip-only proof is required.

## K. Bot compatibility result

Passed.

Prankstar Bot / NEO assistant remained visible on Home/Core. Bot mood/status changed between armed, playing, and ready/deployed states during reactor playback.

## L. Screenshot paths

Required screenshots captured:

- `qa/screenshots/core_reactor_idle.png`
- `qa/screenshots/core_reactor_playing.png`
- `qa/screenshots/core_reactor_charging.png`
- `qa/screenshots/core_reactor_category_selected.png`
- `qa/screenshots/core_reactor_after_stop.png`

Additional QA screenshots captured:

- `qa/screenshots/core_reactor_idle_scrolled.png`
- `qa/screenshots/core_reactor_action_strip_visible.png`
- `qa/screenshots/nav_stash.png`
- `qa/screenshots/nav_jokes.png`
- `qa/screenshots/nav_forge.png`
- `qa/screenshots/nav_system.png`
- `qa/screenshots/nav_action_stash.png`

## M. Logcat findings

Logcat captured:

- `qa/core_reactor_runtime_logcat.txt`
- `qa/core_reactor_runtime_summary.txt`

Post-fix logcat contains no `FATAL EXCEPTION`, no `Process: com.pranksterlab` crash stack, and no `PrankSound.hashCode` crash.

Relevant expected lines include MediaPlayer playback complete/reset/release events for `com.pranksterlab`.

## N. Bugs found

Opening Stash initially crashed:

`java.lang.NullPointerException: Attempt to invoke virtual method 'int com.pranksterlab.core.model.SoundSourceType.hashCode()' on a null object reference`

Stack:

- `com.pranksterlab.core.model.PrankSound.hashCode`
- `kotlin.collections.CollectionsKt___CollectionsKt.toSet`
- `com.pranksterlab.screens.LibraryScreenKt.LibraryScreen(LibraryScreen.kt:120)`

Cause: `LibraryScreen` computed invalid sounds using `allSounds - validSounds.toSet()`. Some persisted/custom sound metadata can have a null `sourceType`, and hashing the full Kotlin data class crashed before the Library screen rendered.

## O. Bugs fixed

Fixed the Stash navigation crash in `LibraryScreen.kt` by comparing valid/invalid sounds by stable `id` instead of hashing whole `PrankSound` instances.

No audio assets were modified. `sound_catalog.json` was not modified.

## P. Remaining limitations

- Automated ADB coordinate testing of reactor action-strip Jokes/Forge was inconclusive because fresh-launch swipes did not consistently place the action strip under the tap coordinate. Bottom dock navigation for those routes passed.
- Runtime audio was verified through app state changes and MediaPlayer logcat events; no direct audio capture was performed.
- Existing build warnings remain and were not part of this scoped fix.
