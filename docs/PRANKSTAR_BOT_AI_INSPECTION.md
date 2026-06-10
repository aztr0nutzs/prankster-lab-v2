# Prankstar Bot AI Inspection

Date: 2026-06-10

## Scope

Static inspection and local build verification of the native bot parser, safety layer, recommender, action routing, and Voice Lab exposure.

## Findings

- Native bot parser/controller/recommender/safety code exists in `app/src/main/java/com/pranksterlab/core/bot/`.
- Home already exposed a visual bot panel.
- Voice Lab previously exposed only a small joke helper, not the full text-command path.
- `twak attack` commands were blocked by the generic safety term `attack`.

## Fixes

- Voice Lab now hosts `PrankstarBotPanel` backed by `PrankstarBotController`.
- Recommended sounds can be played from Voice Lab via the shared `AudioPlayerController`.
- Stop All routes to `audioPlayerController.stopAll()` and stops Voice Lab preview/TTS preview.
- Generated bot text can be sent into the Voice Lab text field with the suggested preset.
- Navigation actions route to Stash and Forge through the app nav controller.
- Unsafe requests continue to produce a refusal action.
- `twak attack` is parsed as a harmless local Voice Lab text generation command.

## Command Coverage

Static route coverage:
- `find creepy sounds`: recommendation action.
- `show animal sounds`: recommendation action.
- `play something funny`: recommendation action.
- `make a joke about being late`: generated text action.
- `make a twak attack about fixing a bike`: generated text action.
- `open stash`: navigation action.
- `open forge`: navigation action.
- `stop all`: stop action.
- Emergency impersonation request: refusal action.

## Verification

- `.\gradlew.bat assembleDebug --stacktrace --console=plain`: PASS.

Device runtime interaction remains blocked because no device/emulator is connected.
