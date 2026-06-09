# Audio Catalog Test Report

Date: 2026-06-09

Scope: Repository/cloud validation and static source inspection only. No runtime Android playback was available.

## Commands Run

```bash
python3 tools/validate_sound_catalog.py
node tools/advanced_validate.cjs
python3 - <<'PY'
# Report-only catalog metrics: count, duplicates, missing files, uncataloged files, unsupported extensions.
PY
```

## Validator Results

### `python3 tools/validate_sound_catalog.py`

PASS:

- Catalog entries: 369.
- Missing files: 0.
- Unsupported extensions: 0.
- Bad headers: 0.
- UTF-8 corrupted files: 0.
- Uncataloged on disk: 0.
- Orphan catalog rows: 0.

### `node tools/advanced_validate.cjs`

PASS:

- Checked 369 files.
- Validation passed.
- 0 warnings ignored.

## Report-Only Static Metrics

- Bundled catalog count: 369.
- Recursive audio file count under `app/src/main/assets/sounds/`: 369.
- Duplicate IDs: 0.
- Duplicate asset paths: 0.
- Missing catalog files: 0.
- Uncataloged audio files: 0.
- Unsupported catalog extensions: 0.
- Sounds excluded from safe random mode: 3.

Category distribution:

| Category | Count |
|---|---:|
| AMBIENCE | 73 |
| ANIMAL | 38 |
| CARTOON | 14 |
| CREEPY | 3 |
| FUNNY | 17 |
| MISC | 56 |
| VOICE | 168 |

## Catalog / Repository Integration

Files inspected:

- `app/src/main/assets/sound_catalog.json`
- `app/src/main/assets/sounds/`
- `app/src/main/java/com/pranksterlab/core/repository/SoundRepository.kt`
- `app/src/main/java/com/pranksterlab/core/audio/AudioPlayerController.kt`
- `app/src/main/java/com/pranksterlab/core/model/PrankSound.kt`

Static status:

- `SoundRepository.getBundledSounds()` loads `sound_catalog.json` from assets and caches it.
- Bundled playback preflight checks asset path is non-blank, extension is supported, and asset can be opened.
- Custom/generated sound preflight checks local file existence and non-zero length for file paths; `content://` and `file://` URIs are allowed through for platform handling.
- Safe random setting exists through DataStore.
- `PrankstarWebBridge.deployRandom()` filters playable bundled sounds and respects safe random default when enabled.
- `AudioPlayerController.playPrankSound()` routes bundled assets vs local/generated paths correctly.
- `AudioPlayerController` validates source, catches MediaPlayer exceptions, marks invalid sound IDs, updates playback state, and releases on completion/error/stop.
- `stopAll()` releases the current player and clears playback state.

## Generated / Custom Sound Handling

Static status:

- `GeneratedVoiceRepository.saveGeneratedVoice()` saves generated voice clips as `PrankSound` entries with:
  - `category = VOICE_GENERATED`
  - `packId = voice_lab`
  - `isCustom = true`
  - `localUri = file.absolutePath`
  - `sourceType = GENERATED`
  - generated metadata containing preset, pitch, speech rate, volume, tone style, source text, and duration.
- `SoundRepository.getCustomSoundsFlow()` exposes custom/generated sounds from DataStore.
- Library filters include Generated, Voice Lab, and Forge filters.
- Settings includes generated voice clip cleanup and missing generated metadata cleanup.

## Placeholder / Corruption Risk

- Validators found 0 bad headers and 0 UTF-8-corrupted bundled assets.
- Static Voice Lab code writes `.wav` files from Android TextToSpeech and labels them `WAV/PCM`.
- Voice Lab requires generated file existence and non-zero size before success/save.
- No static evidence was found that bundled catalog sounds are placeholder text/TTS filenames.
- Runtime decode and acoustic content quality still require device/emulator playback QA.

## Blockers

- Android runtime playback was not verified because no device/emulator/ADB is available.
- Build/APK packaging was blocked because Android SDK is missing.
- No automated audio unit/instrumented tests were found.

## Recommended Tests

1. JVM/Android test for catalog count and no duplicates.
2. Asset existence test for every catalog `assetPath`.
3. `SoundRepository.isCatalogSoundPlayable()` test with fake/assets fixture.
4. `AudioPlayerController` Robolectric or instrumentation smoke test for valid/invalid paths.
5. Generated clip metadata save/load test.
