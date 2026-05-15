# UI Crash and Dock Fix Report

## A. Library crash root cause from logcat
- Could not capture device logcat in this environment because Android SDK/adb is unavailable.
- Most likely crash vector addressed: `LazyColumn` in `LibraryScreen` used `key = { it.id }` and can crash with duplicate IDs (`IllegalArgumentException: Key ... was already used`) when bundled/custom/generated entries collide.

## B. Files changed
- `app/src/main/java/com/pranksterlab/components/PrankstarBottomDock.kt`
- `app/src/main/java/com/pranksterlab/components/PrankstarHeader.kt`
- `app/src/main/java/com/pranksterlab/screens/LibraryScreen.kt`

## C. Dock layout fix
- Reduced dock height to 78dp and tightened paddings for 360dp devices.
- Replaced wrapping-prone labels with short labels (`CORE`, `STASH`, `FORGE`, `JOKES`, `SYS`).
- Switched dock image scaling from `ContentScale.FillBounds` to `ContentScale.Fit` and reduced alpha.
- Reworked tab content to vertical icon+label and enforced `maxLines = 1`.
- Preserved five-route mapping and content descriptions.

## D. Header layout fix
- Updated `PrankstarHeader` to compact range (`88dp..108dp`).
- Changed header image scaling to `ContentScale.Fit` to avoid cropping baked art/text.
- Existing screen mappings already use image-first headers with `showTextOverlay = false` for baked header assets.

## E. Screens tested
- Static code-path validation only (no emulator/device available in environment).

## F. Screenshot paths
- Not captured: adb/device unavailable in this environment.

## G. Build result
- Android build not executed after env check failure (`No Android SDK found via ANDROID_HOME/ANDROID_SDK_ROOT or common paths`).

## H. Validator results
- `python3 tools/validate_sound_catalog.py`: PASS
- `node tools/advanced_validate.cjs`: PASS

## I. Remaining issues
- Full acceptance still requires running build/install/adb flow on a machine with Android SDK + device/emulator.
- Library crash confirmation from real logcat remains pending due to environment limitation.
