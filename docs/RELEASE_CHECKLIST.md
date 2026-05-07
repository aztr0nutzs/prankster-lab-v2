# RELEASE_CHECKLIST.md

Last updated: 2026-05-07

## RELEASE READINESS SCORE

Current score:
82 / 100

Recommendation:
Do not publish yet. The APK builds and automated audio validation passes, but device manual QA and dependency audit remediation remain open.

## REQUIRED COMMANDS

Run before every release candidate:

```powershell
.\gradlew.bat clean assembleDebug --stacktrace --console=plain
python tools\validate_sound_catalog.py
node tools\validate_sound_assets.cjs
node tools\validator_v3.cjs
npm audit --audit-level=high
```

## CURRENT AUTOMATED RESULTS

Build:
PASS

Audio validation:
PASS

npm audit:
FAIL

Audit blocker:
- `google-tts-api` depends on vulnerable `axios`.

## AUDIO CHECKLIST

- [x] Catalog entries exist
- [x] Catalog entries decode under validators
- [x] No missing assets
- [x] No unsupported extensions
- [x] No UTF-8/TTS-substitute corruption
- [x] No uncataloged packaged audio
- [x] No orphan catalog rows
- [x] No duplicate IDs
- [x] No duplicate asset paths
- [x] No unsafe filenames

## MANUAL DEVICE QA CHECKLIST

Status:
NOT EXECUTED in current environment because `adb` is unavailable.

Required on emulator or physical device:
- [ ] Install debug APK
- [ ] Launch app
- [ ] Home loads
- [ ] Reactor triggers real sound
- [ ] Stop All works
- [ ] Library search works
- [ ] Library category filters work
- [ ] SoundCard play/stop works
- [ ] Timer plays selected real sound
- [ ] Randomizer plays valid random sounds
- [ ] Sequence Builder creates and plays a real sequence
- [ ] Sound Forge generates WAV
- [ ] Sound Forge preview works
- [ ] Sound Forge save works
- [ ] Generated sound appears in Library
- [ ] Settings persist after app restart

## PERFORMANCE CHECKLIST

- [x] Shared app-level `AudioPlayerController`
- [x] MediaPlayer release on stop/error/dispose
- [x] Randomizer jobs cancellable
- [x] Sequence playback job cancellable
- [x] Catalog playability cache present
- [ ] Device profiler pass
- [ ] Long-session playback soak

## ACCESSIBILITY CHECKLIST

- [x] Bottom dock labels/content descriptions
- [x] Main action buttons generally labeled
- [x] Strong dark/neon contrast
- [x] Reduced animation setting persisted
- [ ] TalkBack pass on device
- [ ] Reduced animation applied globally
- [ ] Icon-only controls reviewed screen-by-screen

## RELEASE BLOCKERS

1. Manual device QA not executed.
2. npm audit high severity vulnerabilities.
3. Build warnings not fully triaged.
4. No release signing/Play Store build validation performed.

## RELEASE RECOMMENDATION

Internal debug QA:
Allowed.

External beta:
Not recommended until manual device QA passes.

Production release:
Blocked.
