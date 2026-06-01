# AGENTS.md

## PROJECT IDENTITY

Project Name: Prankster Lab
Platform: Android
Primary Stack:
- Kotlin
- Jetpack Compose
- Android MediaPlayer
- DataStore
- Gradle Kotlin DSL

Primary Android Package:
`com.pranksterlab`

Primary Android Source Root:
`app/src/main/java/com/pranksterlab/`

Primary Android Asset Root:
`app/src/main/assets/`

Primary Sound Asset Root:
`app/src/main/assets/sounds/`

## CORE DIRECTIVE

This project must remain a premium-quality Android prank laboratory application with:
- immersive cyberpunk UI
- stable Android architecture
- categorized sound library
- advanced reactor-core interactions
- procedural sound generation
- offline playback
- legacy sequence-builder code retained but not currently routed
- timers and randomizers
- generated sound management

## STRICT UI PRESERVATION AMENDMENT

No agent may:
- replace custom UI with generic Material layouts
- flatten layered surfaces
- remove waveform systems
- remove glow systems
- remove reactor interactions
- replace custom navigation with default tabs
- downgrade animated interfaces into plain columns
- replace premium cards with plain lists
- reduce visual hierarchy
- remove neon styling

All future work must preserve:
- dark cyberpunk aesthetic
- neon edge lighting
- layered surfaces
- animated transitions
- waveform headers
- premium dock navigation
- glowing category chips
- HUD-inspired composition
- animated reactor core
- rich interaction feedback

## BUILD INTEGRITY RULE

Before claiming success, run:

```powershell
.\gradlew.bat clean assembleDebug --stacktrace --console=plain
```

Current status as of 2026-05-07:
PASS

## AUDIO RULES

Required:
- real MediaPlayer playback
- stop support
- active state tracking
- playback validation
- corrupt file handling
- duration validation
- no missing assets
- no uncataloged packaged audio
- no duplicate catalog IDs or paths

Forbidden:
- fake playback
- hardcoded fake sound lists
- silent playback failures
- placeholder audio files
- TTS-substitute corrupted binary files

Current automated audio status as of 2026-05-07:
PASS

## CURRENT KNOWN BLOCKERS

1. Manual device/emulator QA not executed because `adb` is unavailable in the current environment.
2. npm audit reports high-severity vulnerabilities through `google-tts-api` -> `axios`.
3. Build warnings remain for manifest package declaration, Java 8 target under JDK 21, deprecated icons, and unused parameters.
4. Reduced animation preference is persisted but not globally enforced across every animated component.

## SUCCESS CONDITIONS

Production readiness requires:
- successful clean `assembleDebug`
- all catalog sounds validated
- no corrupt assets
- no missing assets
- no duplicate IDs or paths
- manual app flow QA on device/emulator
- stable playback across Home, Library, Timer, Randomizer, Sound Forge, Voice Lab, Packs, Settings, and Prank Messages
- dependency audit triaged
- visually cohesive premium UI preserved
