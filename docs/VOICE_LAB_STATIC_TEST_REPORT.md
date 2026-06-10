# Voice Lab Static Test Report

Last updated: 2026-06-10

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

## UI Preservation

Preserved:

- `PrankstarHeader`
- `PrankstarBotVideo`
- `PrankstarBotPanel`
- synthetic preset list
- status card
- Android TextToSpeech generation path
- generated clip preview and stop controls
- Save to Stash flow
- cyberpunk glass/neon styling

Added:

- Twak-Attacks / Tweaker Geographic narrator card.
- Action and optional Setting inputs.
- Tone chips.
- Optional sound-search cue checkbox.
- Local narration preview.
- `Send to Voice Lab`.
- User-confirmed ElevenLabs British Narrator generation path.

## Bot Integration

Voice Lab exposes the full native bot text-command path:

- Bot command input.
- Send button.
- Quick chips including `Find Creepy`, `Funny Sound`, `Make Joke`, `Twak Attack`, `Open Stash`, and `Stop All`.
- Bot response display.
- Recommendation cards.
- Play recommended sound.
- Stop All.
- Send generated text into Voice Lab.
- Refusal display through bot response text.

## ElevenLabs Tweaker Geographic Static Check

- Dedicated voice ID constant is present.
- API key is read via `BuildConfig.ELEVENLABS_API_KEY` from local Gradle/environment inputs; no key is stored in source.
- Voice Lab preserves Android TTS WAV generation and adds a user-confirmed ElevenLabs MP3 path for Tweaker Geographic.
- Preview uses local `MediaPlayer` file playback for local MP3/WAV files.
- Save to Stash records generated metadata with source, voice ID, format, and feature fields.

## Verification

- Previous recorded `testDebugUnitTest`, `lintDebug`, and `assembleDebug` checks passed.

## Blocked Runtime Checks

The following require a connected unlocked device/emulator:

- TTS engine readiness.
- Generate result.
- Preview and stop preview.
- Save to Stash.
- Library persistence and playback of saved generated clip.
- Twak-Attacks visual tap flow.
- ElevenLabs end-to-end MP3 generation.
- Screenshots and logcat.
