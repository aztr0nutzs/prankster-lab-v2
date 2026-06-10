# Voice Lab Static Test Report

Date: 2026-06-10

## Static Result

PASS with runtime-device verification blocked.

## Existing Workflow Paths

- Text input exists and is limited to 300 characters.
- Voice preset selection exists.
- Generate is enabled only when TTS is ready, text is nonblank, and generation is not active.
- Android TextToSpeech readiness errors are surfaced to the user.
- Generated local WAV output is checked for existence and nonzero length.
- Preview uses a managed `MediaPlayer`.
- Stop Preview stops generated preview and TTS preview.
- Save to Stash uses `GeneratedVoiceRepository.saveGeneratedVoice(...)`.
- Generated clips are stored as custom sounds and surfaced in Library filters.

## Fix Added

Voice Lab now exposes the full native bot text-command path:
- Bot command input.
- Send button.
- Quick chips including `Find Creepy`, `Funny Sound`, `Make Joke`, `Twak Attack`, and `Stop All`.
- Bot response display.
- Recommendation cards.
- Play recommended sound.
- Stop All.
- Send generated text into Voice Lab.
- Refusal display through bot response text.

## Verification

- `.\gradlew.bat assembleDebug --stacktrace --console=plain`: PASS.

## Blocked Runtime Checks

The following require a connected unlocked device/emulator:
- TTS engine readiness.
- Generate result.
- Preview and stop preview.
- Save to Stash.
- Library persistence and playback of saved generated clip.
- Screenshots and logcat.
