# Voice Lab Runtime Fix Report

## A. Files changed
- app/src/main/java/com/pranksterlab/core/voice/VoiceSynthesisEngine.kt
  - Added explicit `VoiceEngineReadiness` sealed interface with
    `INITIALIZING`, `READY`, `UNAVAILABLE`, `ERROR(message)` states.
  - Synthesis result now carries `success`, `errorMessage`,
    `formatLabel`, and optional `durationMs` so the UI can validate
    real generation before promoting to GENERATED/SAVED.
- app/src/main/java/com/pranksterlab/core/voice/AndroidTextToSpeechEngine.kt
  - Captures `TextToSpeech` init status, language status, and
    publishes a typed readiness flow consumed by Compose.
  - `synthesizeToFile` now waits for `READY`, validates pitch / rate
    set return codes, returns failure if `synthesizeToFile` start
    return is not `SUCCESS`, only resolves success on `onDone` after
    confirming the output file exists and `length() > 0`, returns
    failure on `onError`, and times out if no callback ever arrives.
- app/src/main/java/com/pranksterlab/screens/voice/VoiceJokeGeneratorScreen.kt
  - Replaced the previous inline `MediaPlayer` usage with a managed
    `ManagedPreviewPlayer` that:
    - validates the file before constructing the player,
    - keeps the `MediaPlayer` reference in a class field so the
      completion / error callbacks resolve `stop()` to the outer
      class method (the previous version's `apply { ... }` scoping
      bug meant `stop()` inside the listeners resolved to
      `MediaPlayer.stop()` and the player was never `release()`d),
    - releases the player defensively in `try`/`catch` blocks per
      call so a thrown `IllegalStateException` on `isPlaying` cannot
      skip `reset()` / `release()`,
    - stops the previous preview before starting a new one,
    - releases on completion, on Stop Preview, and on screen
      `DisposableEffect.onDispose`,
    - reports playback failure to the UI status surface.
  - Generate is gated by readiness `READY`, non-blank text, and not
    already `GENERATING`.
  - `Preview Voice Style` is now also disabled while
    `GENERATING` so a `QUEUE_FLUSH` `speak()` cannot interrupt an
    in-flight `synthesizeToFile`. It also calls `previewPlayer.stop()`
    so the two preview sources are mutually exclusive.
  - `Preview` (generated file) is disabled while `GENERATING` and
    calls `tts.stopPreview()` first for the same mutual exclusion.
  - Save is blocked unless the generated file exists, is non-empty,
    and the last generation reported `success`.
- app/src/main/res/raw/prankstar_boot.mp4 *(new)*
  - Required by `MainActivity.kt`'s boot sequence
    (`R.raw.prankstar_boot`). The asset was present in the repo root
    but missing from the Android resource tree, which made
    `assembleDebug` fail on `Unresolved reference: raw` regardless of
    Voice Lab work. Added so the build can be verified end to end.

## B. TTS readiness behavior
- Readiness uses the four required states:
  - `INITIALIZING` (default at construction)
  - `READY` (TTS reported `SUCCESS` and language load did not return
    `LANG_MISSING_DATA` / `LANG_NOT_SUPPORTED`)
  - `UNAVAILABLE` (TTS init status was not `SUCCESS`)
  - `ERROR(message)` (engine null, language missing, language
    unsupported, or initialization timed out)
- Voice Lab keeps the Generate button disabled unless readiness is
  `READY`. While initializing, the status banner shows
  "INITIALIZING VOICE ENGINE / Preparing Android TextToSpeech." and
  flips to "READY / Voice engine is ready." once readiness fires.
- `awaitReady()` has an init timeout (10 s) so a stuck engine
  fails synthesis with a clear error instead of hanging.

## C. Generation validation behavior
- Generate refuses early if text is blank or matches the
  "police"/"emergency" impersonation guard.
- Synthesis requires readiness `READY` before proceeding.
- `setPitch` / `setSpeechRate` return codes are inspected and
  reported as failures when the engine rejects them.
- `synthesizeToFile` start return code is checked. Anything other
  than `TextToSpeech.SUCCESS` resolves to a failure result with the
  listener detached.
- Success is reported only from `onDone` after verifying:
  - `outputFile.exists()`, and
  - `outputFile.length() > 0L`.
- `onError(utteranceId)` and `onError(utteranceId, errorCode)` both
  resolve to a failure result with the error code surfaced in the
  message.
- A 60 s synthesis timeout returns a failure result with the
  listener detached if no callback arrives.
- The format label stays `WAV/PCM` (Android `synthesizeToFile`
  output) — the UI never claims `MP3` is produced.

## D. Preview player behavior
- The previous inline `MediaPlayer` implementation had two real
  defects that the new `ManagedPreviewPlayer` corrects:
  1. The completion and error listeners called `stop()` inside an
     `apply { ... }` block whose receiver was the `MediaPlayer`, so
     `stop()` resolved to `MediaPlayer.stop()` instead of the
     intended `ManagedPreviewPlayer.stop()`. The player was stopped
     but never `release()`d, leaking native resources until the next
     `play()` or screen dispose explicitly stopped it.
  2. `mediaPlayer?.runCatching { if (isPlaying) stop(); reset();
     release() }` would skip `reset()`/`release()` if `isPlaying`
     threw on an illegally-stated player.
- The new player:
  - Constructs the `MediaPlayer` outside the lambda scope so the
    listeners' `stop()` unambiguously calls `ManagedPreviewPlayer.stop()`.
  - Validates the file exists and is non-empty before touching the
    `MediaPlayer`.
  - Releases per-operation in independent try/catch blocks so each
    of `stop()`, `reset()`, `release()` runs even if a previous step
    threw.
  - Clears `mediaPlayer` before releasing so reentrant callbacks
    cannot double-release.
  - Is called from `DisposableEffect.onDispose`, the Stop button,
    the start of a new preview, and is paired with `tts.stopPreview()`
    whenever switching between preview sources so the two are
    mutually exclusive.
  - Surfaces playback errors to the UI status banner as `ERROR`.

## E. Save behavior
- Save is blocked unless:
  - `generatedFile` exists,
  - `generatedFile.length() > 0`,
  - the last `VoiceSynthesisResult.success == true`.
- `GeneratedVoiceRepository.saveGeneratedVoice(...)` is wrapped in
  `runCatching`. The status only flips to `SAVED` after the suspend
  call returns successfully; any thrown exception routes to `ERROR`
  with the message. The button is additionally disabled once
  `savedGeneratedFilePath` matches the current file's path, so a
  user cannot double-save the same clip.
- `GeneratedVoiceRepository.saveGeneratedVoice` itself `require`s
  the file exist and have non-zero length, so even if the UI gate
  were bypassed the repository would refuse.

## F. Build result
- `./gradlew assembleDebug --stacktrace` was run against an Android
  SDK installed into `/opt/android-sdk` (platform-tools, SDK platform
  34, build-tools 34.0.0) with `sdk.dir` pointed there via local
  `local.properties` (gitignored, not committed).
- Result: **BUILD SUCCESSFUL** in 34s.
- APK produced at
  `app/build/outputs/apk/debug/app-debug.apk` (~83 MB).
- Only warnings reported are unrelated to Voice Lab (unused
  parameters in `SoundGeneratorEngine`, deprecated icon imports in
  `PrankMessagesScreen` / `TimerPrankScreen`, unused `onBack` in
  `SoundForgeWorkbench`, and the JDK 21 source/target 1.8
  deprecation notice).
- A pre-existing compile error (`Unresolved reference: raw` from
  `MainActivity.kt` referencing `R.raw.prankstar_boot`) blocked the
  build regardless of Voice Lab. The asset was present at the repo
  root as `prankster_boot.mp4` but missing from
  `app/src/main/res/raw/`. The file was copied to
  `app/src/main/res/raw/prankstar_boot.mp4` so the build could be
  verified.

## G. Runtime test result
- Device/emulator runtime flow was not executed: this remote
  environment has no connected device or emulator and no AVD image
  installed. The Voice Lab flow (open Voice Lab → select preset →
  type text → generate → preview → stop preview → save → play from
  Library) was therefore not exercised on a live runtime.
- The build does produce an installable debug APK; the runtime path
  is expected to work given the validations above, but is not
  empirically confirmed in this environment.

## H. Remaining limitations
- No on-device verification of Generate / Preview / Stop /
  Save / Library playback in this environment. Recommend re-running
  the manual flow on a real device or emulator before release.
- `tts.preview()` and `synthesizeToFile()` still share a single
  `TextToSpeech` instance. The UI now prevents concurrent invocation
  by disabling the preview buttons during `GENERATING`, but a future
  refactor could split synthesis and preview onto separate engines
  for additional isolation.
- Duration is read via `MediaMetadataRetriever`; if the device's
  metadata reader cannot decode the produced WAV, `durationMs` will
  be `null` and the saved sound stores `0L`. Generation still
  succeeds.
- The Voice Lab UI still contains a simple substring guard
  ("police", "emergency") rather than a structured policy classifier;
  this is unchanged because the task scope is runtime reliability,
  not content policy.
