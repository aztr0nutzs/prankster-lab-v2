# AUDIO_VALIDATION_REQUIREMENTS.md

Last updated: 2026-05-07

## REQUIRED AUDIO VALIDATION

Every Android release candidate must pass all validators:

```powershell
python tools\validate_sound_catalog.py
node tools\validate_sound_assets.cjs
node tools\validator_v3.cjs
```

## REQUIRED CHECKS

Validators must verify:
- every catalog `assetPath` exists
- every catalog entry decodes successfully
- duration metadata is present and greater than zero where strict validator requires it
- MIME/container/header is valid for `.ogg`, `.mp3`, or `.wav`
- catalog IDs are unique
- catalog asset paths are unique
- no audio file under `app/src/main/assets/sounds/` is uncataloged
- no catalog row points to a missing/orphaned asset
- no UTF-8 replacement-character/TTS-substitute corruption remains
- no unsupported extensions are packaged
- no unsafe filenames are present

## CURRENT VALIDATION RESULT

Date:
2026-05-07

Result:
PASS

Summary:
- Catalog entries: 369
- Missing files: 0
- Unsupported extensions: 0
- Bad headers: 0
- UTF-8 corrupted: 0
- Uncataloged on disk: 0
- Orphan catalog rows: 0
- Advanced validator errors: 0

## REPAIR NOTES

Final QA repaired release-blocking audio issues:
- 35 ambience OGG files lacked duration metadata and were replaced with valid WAV files.
- `sounds/voices_fighter/it's_a_tie.ogg` was renamed to `sounds/voices_fighter/its_a_tie.ogg`.

## INVALID VALIDATION

The following is not sufficient:
- path exists only
- non-zero file size only
- catalog row exists only
- successful Gradle packaging only

Release validation must include decode/duration checks.
