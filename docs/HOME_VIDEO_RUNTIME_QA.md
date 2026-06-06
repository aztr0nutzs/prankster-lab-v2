# Home Video Runtime QA

Date: 2026-06-05

## A. Device Used

- Device: `RFCT70ET5TF`
- Model from logcat: `SM-G781V`
- Android: API 33 device runtime

Command:

```powershell
adb devices
```

Result:

```text
RFCT70ET5TF    device
```

## B. APK Path And Size

- `app/build/outputs/apk/debug/app-debug.apk`
- Size: `99,158,237` bytes
- Install command: `adb install -r app/build/outputs/apk/debug/app-debug.apk`
- Install result: `Success`

## C. Home/Core Visual Result

PASS.

Home/Core opened after launch. The enhanced reactor remained visible, readable, and interactive. The global bottom dock appeared once; no duplicate dock was observed.

## D. Background Video Result

PASS.

`prankstar_bg.mp4` displayed behind the Home/Core content with a dark scrim and scanline layer keeping the reactor readable. The background did not block taps: reactor deploy and dock navigation still worked.

Audio leakage from the MP4 was not observed during the run. The implementation keeps Media3 player volume at `0f`.

## E. Header Video Result

PASS.

`prankstar_header.mp4` appeared as the top banner. The banner was cleanly sized for the phone screen, did not visibly stretch, and did not push the reactor off-screen.

## F. Reactor/Audio Result

PASS.

Tapping the reactor/deploy area played real catalog sounds and updated the readout/visual state. Logcat confirmed real asset playback:

- `sounds/funny/dragon-studio-creepy-laugh-2-401714.mp3`
- `sounds/funny/freesound_community-grito-81340.mp3`

Stop All was tapped through the visible floating control area and completed without crash. One tested sound naturally completed quickly; no playback error was logged.

## G. Navigation Result

PASS.

Bottom dock navigation opened:

- Stash / Library
- Jokes / Voice Lab
- Forge
- System
- Core / Home

The active dock state changed correctly on the captured route screens. Returning Home still showed the MP4-ready Home/Core layout.

## H. Performance Observations

PASS with minor device-log noise.

Home/Core was observed for at least 30 seconds. No visible crash, black Home frame, frozen header, or lagging controls were observed in the captured state after the wait.

Animation intensity was tested:

- `FULL`: video Home/Core mode displayed during the primary QA run.
- `MINIMAL`: switched successfully from System settings and Home used the static fallback without crashing.

The device entered doze/lockscreen after inactivity during cleanup, causing black adb screenshots. This was confirmed through `dumpsys power` as `mWakefulness=Dozing`; it was not an app crash.

## I. Logcat Findings

Full log:

- `qa/home_video_runtime_logcat.txt`

Filtered summary:

- `qa/home_video_runtime_summary.txt`

Crash markers:

- `FATAL EXCEPTION`: 0
- `AndroidRuntime`: 0
- `PlaybackException`: 0

Notable non-fatal findings:

- MediaCodec/ExoPlayer init and release lines appeared during route/video lifecycle changes.
- Device codec logs included `OMX-VDEC-1080P set_parameter` errors, but playback remained visible and no ExoPlayer fatal/playback exception followed.
- Samsung/system screenshot and Exif warnings appeared when using screenshot capture tooling; these were unrelated to app runtime.

## J. Screenshot Paths

Required captures:

- `qa/screenshots/home_core_video_idle.png`
- `qa/screenshots/home_core_video_playing.png`
- `qa/screenshots/home_core_after_stop.png`
- `qa/screenshots/nav_stash.png`
- `qa/screenshots/nav_jokes.png`
- `qa/screenshots/nav_forge.png`
- `qa/screenshots/nav_system.png`
- `qa/screenshots/nav_back_home.png`

Additional captures:

- `qa/screenshots/home_after_30s.png`
- `qa/screenshots/settings_animation_minimal.png`
- `qa/screenshots/home_animation_minimal.png`

## K. Bugs Found

No app crash was found.

QA tooling issues encountered:

- Initial coordinate taps used viewer-scaled screenshot coordinates and missed the dock. Retested with the device's actual 1080x2400 coordinate space.
- Samsung screenshot toolbar appeared during one capture attempt and blocked taps. Retested using device-side `screencap -p /sdcard/...` plus `adb pull`.
- The device entered doze/lockscreen during cleanup, causing black screenshots. Confirmed as a device state issue, not an app rendering crash.

## L. Bugs Fixed

No code changes were required. Verified issues were QA-environment/tooling issues, not app defects.

## M. Remaining Blockers

- A human visual pass on-device is still useful for subjective video smoothness and audio leakage confirmation, because screenshots cannot prove looping or silence over time.
- Codec warning lines should be watched in future performance QA if lower-end devices show stutter, but no runtime failure was observed on `SM-G781V`.
