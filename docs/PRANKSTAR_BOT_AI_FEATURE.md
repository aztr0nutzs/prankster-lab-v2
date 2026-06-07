# Prankstar Bot Agent AI-Style Feature

## Summary

Prankstar Bot Agent is a local deterministic assistant layer for Prankster Lab. It keeps the existing Prankstar / NEO MP4 mascot mood system and adds useful actions on top of it.

This phase does not use cloud APIs, WebView assistant logic, payment flows, or external LLM calls.

## What the bot can do

- Parse simple natural-language requests locally.
- Search and recommend real sounds loaded from `SoundRepository`.
- Generate harmless joke/comment lines from safe local templates.
- Suggest voice preset IDs for Voice Lab.
- Send generated text to Voice Lab through a shared in-app draft bridge.
- Build harmless prank plans with explicit safety notes.
- Trigger safe app actions: play a real sound, stop audio, navigate to Stash/Jokes/Forge.
- Explain what it did in the bot message bubble.
- Update the existing robot mood video state.

## Local parser behavior

The parser supports keywords such as:

- `find`, `search`, `show`
- `play`, `random`, `recommend`
- `joke`, `roast`
- `voice`, `say`
- `plan`, `prank`
- `creepy`, `funny`, `animal`, `scary`, `robot`
- `stop`
- `stash`, `library`, `jokes`, `voice lab`, `forge`

Example mappings:

- `find creepy sounds` -> `SearchSounds("creepy")`
- `show animal pranks` -> `SearchSounds("animal")`
- `play something funny` -> `RecommendSounds("funny")`
- `make a joke about my friend being late` -> `GenerateJoke("my friend being late")`
- `create a creepy prank plan` -> `BuildPrankPlan("creepy")`
- `stop all` -> `StopAll`
- `open stash` -> `OpenStash`

## Safety limits

The bot refuses requests involving threats, self-harm, harassment, stalking, emergency/government impersonation, real-person voice impersonation, phone-number spoofing, automatic message sending, illegal activity, bypassing consent, or dangerous pranks.

Refusals are friendly and keep the prank tone: the bot redirects users toward harmless sound pranks, goofy voice clips, or consent-friendly jokes.

## Future cloud LLM interface

The current controller is intentionally dependency-free and deterministic. A future cloud/LLM implementation can be added behind the controller/parser/recommender interfaces while preserving the same action model and safety gate.
