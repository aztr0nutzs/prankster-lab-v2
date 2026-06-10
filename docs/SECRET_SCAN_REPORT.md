# Secret Scan Report

Last updated: 2026-06-10

## Commands

- `Select-String -Path "**/*" -Pattern "ELEVENLABS_API_KEY|xi-api-key|sk-|AIza|api_key|apikey|password|secret|token|Bearer " -CaseSensitive:$false -ErrorAction SilentlyContinue`
- Text-focused follow-up scan using `rg --files` while excluding build outputs and binary media.
- Targeted scans for `BuildConfig.ELEVENLABS_API_KEY`, `xi-api-key`, `sk-`, `AIza`, and bearer-token patterns.

## Result

No real ElevenLabs API key, OpenAI-style `sk-` key, Google `AIza` key, bearer token, or password secret was found in tracked text files.

## Findings

- `app/build.gradle.kts` references the `ELEVENLABS_API_KEY` variable name for debug/local builds only.
- Release `BuildConfig.ELEVENLABS_API_KEY` was verified as an empty string in generated release BuildConfig.
- `app/src/main/java/com/pranksterlab/core/elevenlabs/ElevenLabsTtsService.kt` uses the `xi-api-key` header name, but the value is supplied at runtime and is not logged.
- Docs contain placeholder setup text for `ELEVENLABS_API_KEY`; no real key is printed.
- Tests use non-secret literals such as `key`.
- Binary and QA-log scans produced false positives for generic strings such as `token` or byte sequences resembling `sk-`.

## Repository Hygiene

- `local.properties` is ignored and was removed from Git tracking. The local file remains on disk for SDK configuration.
- `.env*` is ignored; `.env.example` remains tracked.
- No `.env` file was present in the repo root during this pass.

## Production Gate

Release builds compile with an empty ElevenLabs key. Production ElevenLabs generation is blocked by the missing-key gate until a backend/proxy or approved release secret delivery model is implemented.

## Rotation Guidance

No committed real secret was found, so no mandatory rotation was triggered by this scan. If a real ElevenLabs key was ever placed in `local.properties` while it was tracked historically, rotate that key before external release.
