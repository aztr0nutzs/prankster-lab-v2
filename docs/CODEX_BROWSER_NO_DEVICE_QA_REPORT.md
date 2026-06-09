# Codex Browser No-Device QA Report

Date: 2026-06-09  
Environment: `/workspace/prankster-lab-v2` on Linux shell from Codex via Android browser  
Branch: `work`

## Acceptance Criteria Covered

- Starting git status inspected.
- Project assets inventoried without deleting or redesigning assets.
- Android SDK environment check run before any Gradle build attempt.
- Debug build path attempted through the project build script; Gradle itself was blocked by invalid SDK environment.
- Audio catalog validators run.
- Screen and route inventory inspected statically.
- Major control mappings inspected statically.
- Stable V9 WebView and JavaScript bridge inspected statically.
- Stable V9 HTML local asset references parsed and checked.
- Audio catalog, repository, and playback code inspected statically.
- Voice Lab / generated sound flow inspected statically.
- Prankstar Bot AI architecture inspected statically.
- Unit-test discovery performed.
- ADB/device availability checked.
- No runtime/device success, screenshots, or physical tapping claimed.

## Environment Limits

- No physical Android device was attached.
- `adb` is not installed in this container, so device/emulator smoke QA is blocked.
- No emulator was available or launched.
- No screenshots were taken in this pass.
- Android SDK is not available in the Linux environment.
- `local.properties` exists but points to a Windows SDK path: `C:\Users\Aztr0nutZs\AppData\Local\Android\Sdk`.
- `ANDROID_HOME` and `ANDROID_SDK_ROOT` are empty.
- Required SDK folders (`platforms/`, `platform-tools/`, `build-tools/`) could not be confirmed in this container.

## Starting State / Git Status

Commands run:

```bash
git status --short
git branch --show-current
git remote -v
git status --short --branch
```

Result:

- Branch: `work`.
- `git status --short`: clean before report generation.
- Untracked files before report generation: none reported.
- Modified files before report generation: none reported.
- User changes existed before report generation: no.
- Required upstream sync command `git fetch origin --prune` failed because no `origin` remote is configured in this checkout.

## Build Result

Commands run:

```bash
chmod +x scripts/android-env-check.sh scripts/build-android-debug.sh
./scripts/android-env-check.sh
./scripts/build-android-debug.sh
```

Result:

- Android environment check failed.
- `./scripts/build-android-debug.sh` also failed at the required SDK check.
- Per `AGENTS.md`, Gradle was not run after the SDK check failed.
- `assembleDebug` result: **blocked by missing Android SDK**, not a source compile result.
- APK result: no `app/build/outputs/apk/debug/app-debug.apk` was produced during this pass.
- Existing debug APK search found no debug APK under `app/build/outputs/apk/debug/`.

Key failure:

```text
ERROR: No Android SDK found via ANDROID_HOME/ANDROID_SDK_ROOT or common paths.
```

## APK Path and Size

- Expected path: `app/build/outputs/apk/debug/app-debug.apk`.
- Current pass: APK not generated because Android SDK is missing.
- APK size: not available.

## Validator Results

Commands run:

```bash
python3 tools/validate_sound_catalog.py
node tools/advanced_validate.cjs
```

Results:

- `python3 tools/validate_sound_catalog.py`: PASS.
  - Catalog entries: 369.
  - Missing files: 0.
  - Unsupported extensions: 0.
  - Bad headers: 0.
  - UTF-8 corrupted assets: 0.
  - Uncataloged on disk: 0.
  - Orphan catalog rows: 0.
- `node tools/advanced_validate.cjs`: PASS.
  - Checked 369 files.
  - Validation passed.

Additional report-only static audio check:

- Catalog entries: 369.
- Recursive audio files under `app/src/main/assets/sounds/`: 369.
- Duplicate IDs: 0.
- Duplicate asset paths: 0.
- Missing catalog files: 0.
- Uncataloged audio files: 0.
- Unsupported catalog extensions: 0.
- Category distribution: AMBIENCE 73, ANIMAL 38, CARTOON 14, CREEPY 3, FUNNY 17, MISC 56, VOICE 168.
- `isSafeForRandomMode=false`: 3 entries.

## Asset Inventory Summary

Present required launcher/header assets:

- `app/src/main/res/drawable/prankstar_icon.png`
- `app/src/main/res/drawable/prankstar_header.png`
- `app/src/main/res/drawable/ic_launcher.png`
- `app/src/main/res/drawable/ic_launcher_round.png`

Launcher icon status:

- Manifest `android:icon` points to `@drawable/ic_launcher`.
- Manifest `android:roundIcon` points to `@drawable/ic_launcher_round`.
- `cmp` confirmed `ic_launcher.png` is byte-identical to `prankstar_icon.png`.
- `cmp` confirmed `ic_launcher_round.png` is byte-identical to `prankstar_icon.png`.
- No adaptive icon XML files were present under the inspected paths, so no adaptive icon was found replacing or hiding the Prankstar art.

Present Stable V9 home assets:

- `app/src/main/assets/prankstar/prankstar_new_home_bot_screen.html`
- `app/src/main/assets/prankstar/assets/prankstar_header.mp4`
- `app/src/main/assets/prankstar/assets/reactor2.mp4`
- `app/src/main/assets/prankstar/assets/reactor5.mp4`
- `app/src/main/assets/prankstar/assets/reactor6.mp4`
- `app/src/main/assets/prankstar/assets/reactor7.mp4`
- `app/src/main/assets/prankstar/assets/bot/high.mp4`
- `app/src/main/assets/prankstar/assets/bot/scanning2.mp4`
- `app/src/main/assets/prankstar/assets/bot/powerup2.mp4`
- `app/src/main/assets/prankstar/assets/bot/dancing.mp4`
- `app/src/main/assets/prankstar/assets/bot/celebrate2.mp4`

Missing from the explicit requested Stable V9 asset checklist:

- `app/src/main/assets/prankstar/assets/reactor1.mp4`
- `app/src/main/assets/prankstar/assets/reactor3.mp4`
- `app/src/main/assets/prankstar/assets/reactor4.mp4`

Static interpretation:

- The current HTML embeds at least one reactor video as base64 data and references only `reactor2.mp4`, `reactor5.mp4`, `reactor6.mp4`, and `reactor7.mp4` as external files.
- Missing external `reactor1.mp4`, `reactor3.mp4`, and `reactor4.mp4` are checklist gaps, but the parsed current HTML did not reference them as file paths.

Present robot raw clips:

- `prankstar_bot_angry.mp4`
- `prankstar_bot_bored1.mp4`
- `prankstar_bot_bored2.mp4`
- `prankstar_bot_celebrate.mp4`
- `prankstar_bot_confused.mp4`
- `prankstar_bot_ecstatic.mp4`
- `prankstar_bot_happy.mp4`
- `prankstar_bot_powerup.mp4`
- `prankstar_bot_processing.mp4`
- `prankstar_bot_relaxed.mp4`
- `prankstar_bot_sad.mp4`
- `prankstar_bot_searching.mp4`
- `prankstar_bot_searching2.mp4`
- `prankstar_bot_shutdown.mp4`
- `prankstar_bot_surprised.mp4`
- `prankstar_bot_thinking.mp4`
- `prankstar_bot_typing.mp4`
- `prankstar_bot_wakeup.mp4`
- `prankstar_bot_warning.mp4`

Boot/splash/intro assets found:

- `app/src/main/assets/prankstar/assets/prankstar_boot.mp4`
- `app/src/main/assets/prankstar/assets/prankstar_boot2.mp4`
- `app/src/main/assets/prankstar_boot.mp4`
- `app/src/main/res/raw/prankstar_boot.mp4`

Large asset groups:

- `app/src/main/assets/sounds`: 34 MB.
- `app/src/main/assets/prankstar`: 47 MB.
- `app/src/main/res/raw`: 57 MB.

Suspicious duplicates / APK-size risk:

- Multiple top-level duplicate-looking prankstar media files exist outside `app/src/main`, including reactor clips and bot clips. They are not necessarily packaged but add repository weight.
- Boot video appears in both assets and raw resources.
- Robot/video assets in `res/raw` plus HTML/WebView assets are a size risk; do not delete without product decision and runtime QA.

## Screen / Route Inventory Summary

Default route behavior:

- `NavHost` starts at `home`.
- Route `home` renders `PrankstarStableHomeWebViewScreen`, preserving Stable V9 Home/Core WebView as primary.
- Native bottom dock is hidden only on route `home`, because the HTML home owns its own dock.
- Native bottom dock appears for native routes such as Library, Forge, Voice Lab, System, Randomizer, Timer, Packs, Messages, and native fallback homes.

Routes found:

| Route | Rendered screen/composable | Status | Reachability notes |
|---|---|---|---|
| `home` | `PrankstarStableHomeWebViewScreen` | Primary Stable V9 WebView | Start destination; HTML dock controls route out. |
| `home_native` | `HomeScreen` | Native fallback | Defined but not in main dock. |
| `home_ultimate` | `UltimateReactorScreen` | Native fallback/alternate | Defined but not in main dock. |
| `library` | `LibraryScreen` | Primary Sound Stash | Native dock Stash and WebBridge Stash route here. |
| `timer` | `TimerPrankScreen` | Utility screen | Reachable from Library timer shortcut. |
| `forge` | `SoundForgeScreen` | Primary Sound Forge | Native dock and WebBridge route here. |
| `lab` | `SoundPacksScreen` | Packs screen | Defined; dock maps `lab` selection state to Library, but no primary dock tab opens `lab`. |
| `system` | `SettingsScreen` | Primary Settings/System | Native dock and WebBridge route here. |
| `voice_lab` | `VoiceJokeGeneratorScreen` | Voice Lab / Joke Gen | Native dock and WebBridge Jokes route here. |
| `randomizer` | `RandomizerScreen` | Utility screen | Defined; not in main dock. |
| `messages` | `PrankMessagesScreen` | Utility/safe message composer | Defined; not in main dock. |

Potential route concerns:

- `lab`, `randomizer`, and `messages` are defined routes but not visible on the primary 5-tab dock. They are reachable only through secondary UI if such controls are discovered/used.
- No obvious `settings` route exists; bot parser maps “settings” to `OpenSystem`, which navigates to `system`, so no settings/system mismatch was found.

## Button / Control Mapping Summary

Static inspection found no empty `onClick = {}` or empty `clickable {}` patterns in the main source tree.

Home / Stable V9:

- Core dock: local HTML screen switch and `setReactorMode('core')` bridge call.
- Stash dock: `openStash()` bridge call to `library`.
- Forge dock: `openForge()` bridge call to `forge`.
- Jokes dock: `openJokes()` bridge call to `voice_lab`.
- System dock: `openSystem()` bridge call to `system`.
- Deploy: `deployRandom()` with fallback `playRandomSound()`.
- Stop: `stopAll()` via side audio action.
- Reactor tap/zone actions: call category/random bridge methods.
- Some HTML controls are intentionally visual/local only: power toggle, mode buttons, knobs, spring/holo/AI-style side actions, charge/temp/log animations.

Library / Sound Stash:

- Search toggle, diagnostics toggle, category chips, pack chips, favorites, play/stop via playback button, loop toggle, Create Joke, and Timer shortcut are mapped.
- Generated/custom clips are included through repository custom sounds flow and displayed by Library filtering.

Voice Lab / Joke Gen:

- Bot helper line generation, robot line generation, category filters, preset selection, pitch/speed/volume/effect sliders, echo checkbox, output name, random preset, preview style, generate, preview generated file, stop preview, and save to Stash are mapped.
- Generate is disabled until TTS readiness is `READY`, text is non-blank, and not already generating.

Sound Forge:

- Generator type selection, duration/volume/custom parameter controls, FX toggles/sliders, seed shuffle/lock/randomize/reset, presets, generate, preview/stop preview, and save are mapped through `SoundForgeViewModel` and `AudioPlayerController`.

Settings/System:

- Master volume, safe random mode, haptics, animated bot, bot assistant, bot suggestions, safety acknowledgement, animation intensity, developer diagnostics, clear recent sounds, clear favorites, delete generated voice clips, clear missing generated metadata, reset Sound Forge presets, and validation-report action are mapped.
- “Open validation report” is a status-only in-app message, not a file/browser opener.

Randomizer/Timer/Packs/Messages:

- Randomizer controls are mapped to timer intervals, category, safe-only mode, loop/surprise toggles, start/stop, and stop-all.
- Timer controls are mapped to preset delays, sound picker, start/pause/resume/cancel, and sound selection.
- Packs controls preview a pack sound and open Library with active pack filter.
- Messages controls are local composer/copy/reset only; no automatic sending was found.

## Stable V9 Bridge Summary

- WebView URL: `file:///android_asset/prankstar/prankstar_new_home_bot_screen.html`.
- JavaScript enabled: yes.
- DOM storage enabled: yes.
- File access enabled: yes.
- Content access enabled: yes.
- File URL access enabled; universal file URL access disabled.
- Media autoplay configured through `mediaPlaybackRequiresUserGesture = false`.
- Background set to black.
- Bridge names registered: `PrankstarBridge` and `PrankstarAndroid`.
- HTML bridge lookup supports both names.
- Required bridge methods exist: `deployRandom()`, `stopAll()`, `openStash()`, `openJokes()`, `openForge()`, `openSystem()`, `setReactorMode(mode)`, `logEvent(event)`.
- Additional compatibility methods exist: `playRandomSound()`, `playSoundByCategory(category)`, `playRandomJoke()`, `playJokeByType(type)`, `stopPlayback()`.

Bridge action status:

- `deployRandom()` selects a real bundled playable catalog sound through `SoundRepository` and plays through `AudioPlayerController`.
- `deployRandom()` consults safe random default flow and filters safe sounds when safe random mode is enabled.
- `stopAll()` calls real `AudioPlayerController.stopAll()`.
- Navigation bridge routes match defined routes: `library`, `voice_lab`, `forge`, `system`.

## HTML Asset Path Summary

Parsed local references in Stable V9 HTML:

- `assets/prankstar_header.mp4`: present.
- `assets/reactor2.mp4`: present.
- `assets/reactor5.mp4`: present.
- `assets/reactor6.mp4`: present.
- `assets/reactor7.mp4`: present.
- `assets/bot/high.mp4`: present.
- `assets/bot/scanning2.mp4`: present.
- `assets/bot/powerup2.mp4`: present.
- `assets/bot/dancing.mp4`: present.
- `assets/bot/celebrate2.mp4`: present.

Missing required checklist files but not parsed as external HTML references:

- `assets/reactor1.mp4`
- `assets/reactor3.mp4`
- `assets/reactor4.mp4`

## Audio Catalog Result

- Bundled catalog count remained 369.
- `sound_catalog.json` was not modified.
- 0 missing files.
- 0 duplicate IDs.
- 0 duplicate asset paths.
- 0 uncataloged files under `app/src/main/assets/sounds/`.
- 0 unsupported extensions.
- Repository validates bundled asset existence and extension before playback.
- Playback controller validates local/generated paths, handles MediaPlayer errors, marks invalid sounds, and releases on stop/error/completion.
- Generated voice clips are stored as custom/generated sounds and appear in Library via repository custom sounds flow.
- The historical “woman reading filename” placeholder issue was not proven by validators. Static code now synthesizes actual TTS text to `.wav` and validates non-empty output before success/save; validators report no corrupt bundled assets.

## Voice Lab Inspection Summary

Passes by static inspection:

- TTS readiness state exists.
- Generate is disabled until TTS is ready and text is present.
- Synthesis failures are surfaced through status/error text.
- Output file existence/size is checked before marking generation usable.
- Generated output format is labeled `WAV/PCM`, not falsely labeled MP3.
- Preview player releases resources on stop/completion/error and on screen disposal.
- Stop Preview calls both generated preview player and TTS preview stop.
- Save to Stash validates generated result success and non-empty file before repository save.
- Generated clips are persisted through `GeneratedVoiceRepository.saveGeneratedVoice()` as `PrankSound` with `sourceType = GENERATED`, `packId = voice_lab`, `isCustom = true`, and `localUri`.
- Bot handoff exists through `PrankstarBotVoiceLabBridge.pendingDraft` and Voice Lab consumes that draft.

Device-only limitations:

- Actual Android TextToSpeech initialization, synthesize-to-file behavior, preview audio, and MediaPlayer decode are not runtime verified in this environment.

## Prankstar Bot AI Inspection Summary

Files/classes present:

- `PrankstarBotIntent`
- `PrankstarBotAction`
- `PrankstarBotState`
- `PrankstarBotMessage`
- `PrankstarBotController`
- `PrankstarBotCommandParser`
- `PrankstarBotResponseBuilder`
- `PrankstarBotSafety`
- `PrankstarBotSoundRecommender`
- `PrankstarBotJokeGenerator`
- `PrankstarBotVoiceLabBridge`
- `PrankstarBotPanel`
- `PrankstarBotVideo`
- `PrankstarBotMood`

Capabilities found:

- Visible native bot input exists on `HomeScreen` fallback/native home through `PrankstarBotPanel`.
- Visible helper bot input also exists in `VoiceJokeGeneratorScreen`.
- Parser handles the required sample commands by keyword coverage:
  - `find creepy sounds`
  - `show animal sounds`
  - `play something funny`
  - `make a joke about being late`
  - `create a robot announcement`
  - `open stash`
  - `open jokes`
  - `open forge`
  - `stop all`
- Recommender scores real `PrankSound` fields and returns actual catalog entries passed into the controller.
- Recommended sounds have Play/Favorite/Open Stash controls in `PrankstarBotPanel`.
- Stop all action exists.
- Navigate actions exist.
- Joke generator is local/template-based and uses safety prompt sanitization.
- Text handoff to Voice Lab exists via `PrankstarBotVoiceLabBridge`.
- Voice preset suggestion exists via generated joke/preset IDs.
- Safety refuses threats, harassment/stalking/bullying, emergency/government impersonation, real-person voice impersonation, phone-number spoofing/robocalls, automatic/secret message sending, illegal activity, non-consensual recording, and dangerous panic/emergency pranks.
- Mood/video mapping includes BORED, BORED_ALT, SEARCHING, SEARCHING_ALT, RELAXED, POWERUP, and SAD mapped to raw resources.

Missing / weak bot pieces:

- Primary Stable V9 WebView home has animated HTML bot controls/videos, but no native bot command input bridge was found there.
- The richest bot assistant panel appears on `home_native`, which is a fallback route rather than the default `home` route.
- No dedicated unit tests were found for parser, safety, recommender, mood/video mapping, or Voice Lab bridge.
- Bot persistence/history beyond current Compose state was not verified.
- Recommender does not explicitly filter only safe sounds for every recommendation path; it adds safe-mode scoring but can still return unsafe entries if they match strongly. Auto random uses safe entries.

## Tests Run

Commands run:

```bash
find . -maxdepth 6 -type f \( -iname '*Test.kt' -o -iname '*Test.java' -o -iname '*.spec.*' \) | sort
```

Result:

- No automated unit test files were found within the requested search depth.
- `testDebugUnitTest` was not run because Android SDK environment validation failed and the project instructions forbid running Gradle after a failed SDK check.

Recommended tests to add:

- `SoundRepository` catalog load/count/path existence test.
- Sound catalog duplicate ID/path test.
- `PrankstarBotCommandParser` sample command test.
- `PrankstarBotSafety` refusal test.
- `PrankstarBotSoundRecommender` real-sound recommendation test.
- Voice Lab generated metadata/save test.
- `PrankstarWebBridge` route/action test with fake repository/player.
- `PrankstarBotMood` resource mapping test.

## Safe Fixes Applied

- No code, UI, Gradle, manifest, catalog, or asset fixes were applied.
- Only documentation/report files were created/updated.

## Remaining Blockers

See `docs/REMAINING_BLOCKERS.md` for categorized blockers.

Highest priority blockers:

1. Install/configure Android SDK in this environment or run build on a host with valid SDK.
2. Run `assembleDebug` after SDK validation passes.
3. Run `testDebugUnitTest` once Gradle is allowed.
4. Perform actual device/emulator smoke QA with ADB.
5. Decide whether missing external Stable V9 reactor checklist assets (`reactor1.mp4`, `reactor3.mp4`, `reactor4.mp4`) are acceptable because current HTML embeds/omits them, or restore external files for checklist completeness.
6. Add automated tests for audio catalog, bot parser/safety/recommender, WebBridge, and Voice Lab metadata.

## Final Readiness Score

**Static/cloud readiness: 82 / 100**

Rationale:

- Strong static/audio/catalog/WebView/route coverage passed.
- Audio catalog validators passed with 369 valid sounds.
- Stable V9 default route and core bridge are intact.
- Voice Lab and Bot architecture are present and substantially wired.
- Major deductions are for missing Android SDK/build result, no ADB/device runtime QA, no automated tests, missing checklist reactor assets, and primary Stable V9 WebView not exposing the full native bot text-input assistant.

**Device/runtime production readiness: BLOCKED** until build and device/emulator QA run successfully.
