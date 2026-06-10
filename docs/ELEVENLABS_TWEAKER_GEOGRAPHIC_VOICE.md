# ElevenLabs Tweaker Geographic Voice

## Dedicated Voice

- Voice ID: `wV67xHKrIHTU0gtChZiQ`
- App constant: `TWEAKER_GEOGRAPHIC_VOICE_ID`
- Intended feature: Tweaker Geographic / Tweakographic Narrator only.
- Manual reuse elsewhere is possible only if future UI explicitly lets a user choose that voice.

## API Key Handling

The key is not hardcoded in source code. Android `BuildConfig.ELEVENLABS_API_KEY` is populated from one of these local inputs at build time:

1. Gradle property: `ELEVENLABS_API_KEY`
2. Environment variable: `ELEVENLABS_API_KEY`
3. `local.properties`: `ELEVENLABS_API_KEY=your_key_here`

`local.properties` and `.env*` are ignored by git. Settings/System shows only `Configured` or `Missing`; if configured, it shows only the final four key characters.

Missing-key user error:

> ElevenLabs API key is not configured. Add ELEVENLABS_API_KEY to local.properties or environment variables.

## Endpoint

The feature sends user-approved narration text only after the user taps **Generate British Narration**.

- Method: `POST`
- URL: `https://api.elevenlabs.io/v1/text-to-speech/wV67xHKrIHTU0gtChZiQ?output_format=mp3_44100_128`
- Model: `eleven_multilingual_v2`
- Accept: `audio/mpeg`
- Content-Type: `application/json`
- Header: `xi-api-key`

## Generated File Location

ElevenLabs narration audio is saved separately from local Android TTS WAV output:

`context.filesDir/generated/elevenlabs/tweaker_geo_<timestamp>.mp3`

The implementation does not add generated MP3s to `sound_catalog.json`.

## MP3 Metadata

Saved stash entries preserve:

- `source = elevenlabs`
- `voiceId = wV67xHKrIHTU0gtChZiQ`
- `format = mp3`
- `feature = tweaker_geographic`
- title
- narration text
- created timestamp

## Voice Lab Flow

1. User creates Tweaker Geographic narration text locally.
2. Voice source defaults/points to **Tweaker Geographic British Narrator** for this section.
3. User taps **Generate British Narration**.
4. App shows `Recording field narration…`.
5. ElevenLabs MP3 is generated and saved locally.
6. Preview and Save to Stash use the existing generated audio flow.

Local Android TTS support remains available and unchanged for normal Voice Lab clips.

## Bot AI Flow

Bot commands such as `make a tweaker geographic about looking for a lighter` create local narration text and route it into Voice Lab. The bot may suggest **Generate with British Narrator**, but it does not call ElevenLabs or spend credits automatically. The user must review the text and tap Generate.

## Failure Handling

Structured failures are mapped to user-safe messages:

- Missing API key
- Blank text
- Text too long
- Unauthorized key
- Rate limit
- Bad request
- Network error
- Empty audio
- Unknown failure

No network failure crashes the app, and failed/empty files are deleted.

## Tests

Unit tests use a fake OkHttp interceptor and do not make real ElevenLabs calls. Covered cases:

- missing API key
- blank text
- successful fake MP3 response writes bytes
- empty response
- 401 unauthorized
- 429 rate limit

## Runtime QA Result

Manual device QA was not executed in this container because no ADB/device session was available during implementation. Generation cannot be marked runtime-passed without a configured API key and device/emulator test.

## Twak-Attacks Visual State

The Twak-Attacks visual pass preserves the dedicated voice ID `wV67xHKrIHTU0gtChZiQ`. The Twak Bot switches to `GENERATING` while ElevenLabs is called, `EXCITED` after a successful MP3, `PREVIEWING` during playback, `SAVED` after Sound Stash save, and `ERROR` for missing API key, network, or generation failures.
