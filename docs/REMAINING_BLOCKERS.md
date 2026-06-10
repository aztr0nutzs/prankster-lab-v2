# Remaining Blockers

Last updated: 2026-06-10

## Runtime QA

No Android device or emulator was attached in the recorded environment. The following remain unverified on device:

- Launcher icon screenshot.
- Native header screenshot.
- Bot recommendation screenshot.
- Bot joke-to-Voice-Lab screenshot.
- Bot mood screenshot.
- Runtime screenshots and logcat verification.
- Library five-distinct-sound playback sweep.
- Voice Lab generate / preview / save / Library persistence.
- Sound Forge generate / preview / save / Library persistence.
- Native Bot text-command runtime interaction.
- Tweakographic / Twak-Attacks narrator tap verification.
- ElevenLabs-generated MP3 preview, Save to Stash, and playback.

## Asset Follow-up

The exact source asset `prankstar_header.png` was not found by exact filename during the previous asset pass. The Android drawable `app/src/main/res/drawable/prankstar_header.png` was copied from the available `prankster_header.png`. Replace it if the exact source asset becomes available.

## Environment / Build Notes

- A previous `clean assembleDebug` attempt was blocked by a Windows file lock on a Gradle output jar.
- `.\gradlew.bat --stop --console=plain` stopped one daemon, but the file lock remained.
- Plain `.\gradlew.bat assembleDebug --stacktrace --console=plain` passed afterward in the recorded report.
- 2026-06-10 production pass: `assembleRelease` passed with R8 minify and resource shrinking enabled.
- 2026-06-10 production pass: `bundleRelease` passed and produced `app/build/outputs/bundle/release/app-release.aab`.
- Release signing is not configured. The APK is unsigned and `keytool` reports the AAB is not a signed jar file.
- Configure Play upload signing before Play testing.

## Fixed In Recent Passes

- Voice Lab exposes the native bot parser/safety/recommender path through `PrankstarBotPanel`.
- Bot recommended sounds can be played through the shared audio controller.
- Bot Stop All routes to the shared audio controller and Voice Lab preview stop paths.
- Bot generated text can be sent directly into the Voice Lab input.
- `twak attack` commands now generate a local Voice Lab draft instead of being blocked by the generic `attack` safety term.
- Tweaker Geographic commands can hand off narration to Voice Lab with the British Narrator path selected.
- Release builds now compile with an empty ElevenLabs key instead of embedding local/debug credentials.
- `local.properties` was removed from Git tracking while remaining ignored.
- Manifest backup is disabled and cleartext traffic is disabled.
- Generated/custom sound display names are normalized before persistence.

## Security / Dependency Blockers

- `npm audit --omit=dev` reports 3 high and 4 moderate vulnerabilities in the repo JavaScript dependency graph.
- High findings include `google-tts-api -> axios` and `protobufjs`.
- The `google-tts-api` fix path reported by npm is semver-major and should be handled in a separate dependency compatibility pass.
- Crashlytics or equivalent production crash reporting is not integrated.
- Analytics/privacy disclosure work remains before enabling production telemetry.
- Production ElevenLabs narration backend is designed but not deployed.
- Android `ProductionBackendVoiceProvider` is prepared, but app auth token retrieval is not wired yet.
- Billing, entitlement, credit ledger, and backend rate limiting are not implemented.
- `VOICE_BACKEND_BASE_URL` must be configured for release builds before premium narration testing.

## Android Vitals Risks

- APK/AAB size is large due to bundled media assets.
- MP4/WebView paths need device coverage for decoder and rendering warnings.
- Android TextToSpeech behavior varies by device engine and locale.
- ElevenLabs generation is network-dependent and release-gated until backend/proxy or signing-approved secret delivery exists.
- Premium narration credit abuse controls need backend implementation before public release.
- Generated file storage can grow over time; cleanup exists for generated voice clips but needs runtime QA.

## Passed Checks Recorded Previously

- Android SDK environment check passed.
- Debug build passed.
- Sound catalog validator passed at 369 entries.
- Advanced sound validator passed at 369 files.
- `git diff --check` passed with Git CRLF warnings only.
- `npm run lint` passed.
- `npm run build` passed.
- `testDebugUnitTest` passed with no test sources.
- `lintDebug` passed.

## Notes

- Boot flow remains present in `MainActivity`.
- Stable V9 Home remains the default Home/Core route.
- `sound_catalog.json` was not modified.
- ElevenLabs runtime QA requires a connected Android device/emulator and a locally configured `ELEVENLABS_API_KEY`.
