# Home Video Runtime QA

Date: 2026-06-05

## A. Device Used

No Android device or emulator was attached.

Command run:

```powershell
adb devices
```

Result:

```text
List of devices attached
```

## B. APK Path And Size

- `app/build/outputs/apk/debug/app-debug.apk`
- Size: `99,158,237` bytes

## C. Home/Core Visual Result

Not executed. Runtime QA requires a connected Android device or emulator.

## D. Background Video Result

Not executed. `prankstar_bg.mp4` is packaged as `R.raw.prankstar_bg`, but visual playback could not be observed without a device.

## E. Header Video Result

Not executed. `prankstar_header.mp4` is packaged as `R.raw.prankstar_header`, but visual playback could not be observed without a device.

## F. Reactor/Audio Result

Not executed. Reactor tap/deploy and Stop All require a running app instance.

## G. Navigation Result

Not executed. Stash, Jokes, Forge, System, and Core route checks require a running app instance.

## H. Performance Observations

Not executed. No 30-second Home/Core runtime observation was possible.

## I. Logcat Findings

No runtime logcat was captured because no device/emulator was attached.

## J. Screenshot Paths

No screenshots were captured. Do not treat any existing screenshots as results for this QA pass.

Expected paths for a future connected-device run:

- `qa/screenshots/home_core_video_idle.png`
- `qa/screenshots/home_core_video_playing.png`
- `qa/screenshots/home_core_after_stop.png`
- `qa/screenshots/nav_stash.png`
- `qa/screenshots/nav_jokes.png`
- `qa/screenshots/nav_forge.png`
- `qa/screenshots/nav_system.png`
- `qa/screenshots/nav_back_home.png`

## K. Bugs Found

None verified. The requested runtime flow could not be exercised.

## L. Bugs Fixed

None. No verified runtime issue was available to fix.

## M. Remaining Blockers

- Attach an Android device or start an emulator, then rerun the Home/Core MP4 runtime QA.
- Required checks still pending: APK install, app launch, background/header video playback, muted video confirmation, reactor deploy, Stop All, navigation, screenshots, and logcat.
