# Prankstar Project Context

## Purpose

This file is the primary context boot document for any AI assistant working on Prankstar. Read it before making code, asset, UI, build, or release changes.

## App Overview

Prankstar is a premium Android prank laboratory application focused on harmless comedy, local sound playback, animated reactor interactions, generated prank audio, custom cyberpunk UI, and an in-app robot assistant. The app must feel like a high-energy prank command console rather than a generic soundboard.

### Product Identity

| Field | Value |
|---|---|
| App name | Prankstar |
| Platform | Android |
| Primary stack | Kotlin, Java where present, Jetpack Compose, Gradle |
| Primary package | `com.pranksterlab` |
| Primary source root | `app/src/main/java/com/pranksterlab/` |
| Primary asset roots | `app/src/main/assets/`, `app/src/main/res/drawable/`, `app/src/main/res/raw/` |
| Product style | Cyberpunk, neon, animated, layered, prank-lab HUD |
| Core interaction | Reactor-triggered prank playback and navigation |

## Feature Overview

| Feature Area | Description | Must Preserve |
|---|---|---|
| Home/Core | Main command surface with reactor, header, assistant, quick actions, and dock access. | Reactor visibility, playback wiring, custom layout, premium styling. |
| Sound Stash / Library | Categorized prank sound browser and player. | Real audio playback, stop support, catalog integrity. |
| Sound Forge | Generated or procedural sound creation tools. | Generated clip storage, playback, safe failure handling. |
| Voice Lab / Joke Generator | Text-to-joke and voice/narration workflows. | Safety filtering, user-visible states, bot handoffs. |
| Twak-Attacks / Tweakographic Narrator | Mock-documentary style narrator experience. | Dedicated header/bot assets where available, safe prompt handling. |
| Timer / Randomizer | Delayed or random prank triggering. | Lifecycle-safe scheduling and stop/cancel behavior. |
| Prankstar Bot / NEO | Local deterministic assistant and UI companion. | Safe command parsing, mood/video mapping, non-cloud default behavior. |
| Reactor System | Animated central prank launcher and category selector. | Touch behavior, category state, playback state, animations. |
| Boot Sequence | Startup animation or transition flow. | App startup stability and transition to Home/Core. |
| Bottom Dock Navigation | Custom navigation surface. | No replacement with generic tabs, no duplicate dock. |
| Header System | Branded visual/video headers per screen. | Image/video aspect handling, readable content, no cropping of baked text. |
| Settings/System | Preferences and system controls. | Persistence, reduced animation option, safety/usage settings. |

## Architecture Overview

Prankstar should be maintained as a modular Android app with clear separation between UI, state, domain logic, media playback, asset catalogs, and persistence.

### Target Layering

| Layer | Responsibility | Examples |
|---|---|---|
| UI Components | Stateless or state-hoisted Compose UI, custom neon visuals, animations, accessibility. | Headers, docks, reactor panels, cards, controls. |
| Screens | Route-level composition, state collection, event wiring. | Home/Core, Library, Voice Lab, Forge, Settings. |
| Navigation | Route definitions and transitions. | Bottom dock destinations, quick actions, deep links if added. |
| Domain | Feature rules independent of Android rendering. | Bot parsing, joke templates, catalog selection, randomization. |
| Media | Playback, stop-all, validation, generated clips. | MediaPlayer/ExoPlayer wrappers, sound repository. |
| Persistence | User settings, selected reactor, generated sound metadata. | DataStore or existing storage. |
| Assets | Packaged sounds, videos, images, icons, animation data. | `assets/sounds`, `res/raw`, `res/drawable`. |
| QA Docs | Repro steps, screenshots, validation reports, release blockers. | `qa/`, `docs/`, trackers. |

## UI Overview

Prankstar UI is not generic Material. It must preserve:

- Dark cyberpunk foundation.
- Neon edge lighting and glow systems.
- Layered panels and glass/HUD surfaces.
- Animated waveform headers and cards.
- Premium custom bottom dock navigation.
- Glowing category chips and reactor controls.
- Animated reactor core with rich interaction feedback.
- Robot assistant panel that communicates state and mood.
- Readable contrast over video/image backgrounds.

## Navigation Overview

| Route / Area | Entry Points | Expected Behavior |
|---|---|---|
| Home/Core | App launch after boot, dock Home/Core, back from sections. | Shows reactor command surface and current playback state. |
| Sound Stash | Dock, reactor action strip, bot command. | Browse/play categorized real sounds. |
| Voice Lab / Jokes | Dock, reactor action strip, bot handoff. | Draft/generate safe joke or narration content. |
| Forge | Dock, reactor action strip. | Create or manage generated prank sounds. |
| Settings/System | Dock or system action. | Preferences, reduced animation, app controls. |
| Timer/Randomizer | Screen menus or feature cards. | Delayed/random playback with cancellation. |

Navigation changes must document route names, origin screens, destination screens, back behavior, and any affected dock active-state logic.

## Asset Overview

Asset work must be tracked before integration. Assets may exist at repository root, in `public/`, under `app/src/main/assets/`, or under Android resources. Never assume an asset is packaged just because a similarly named root file exists.

### Common Asset Categories

| Category | Typical Locations | Validation |
|---|---|---|
| Sound effects | `app/src/main/assets/sounds/`, `public/sounds/` | Catalog path exists, valid header/container, playable duration. |
| Reactor videos | `app/src/main/assets/prankstar/assets/`, `app/src/main/res/raw/` | File exists, name matches reference, loops without crash. |
| Bot videos | `app/src/main/res/raw/`, feature-specific raw resources. | Mood mapping resolves safely with fallback. |
| Headers | `app/src/main/res/drawable/`, `app/src/main/res/raw/` | Aspect ratio preserved, baked text readable. |
| Dock art/icons | `app/src/main/res/drawable/`, `mipmap-*` | Touch targets and active states remain clear. |
| Generated audio | App-private storage | Playback and cleanup verified. |

## Reactor Overview

The reactor is the symbolic and functional center of Prankstar. It should support category selection, animated state changes, touch/gesture interaction, playback triggers, stop behavior, action-strip navigation, and persistence of selected mode where implemented.

Required reactor states:

| State | Meaning |
|---|---|
| Idle / Armed | Ready to trigger a prank. |
| Charging | User interaction or animation before launch. |
| Playing / Deployed | A sound or prank action is active. |
| Stopped | Playback was stopped and UI returned safely. |
| Disabled / Offline | Feature unavailable without crashing. |

## Robot Overview

Prankstar Bot / NEO is a local assistant layer and companion UI. It may parse user commands, recommend real sounds, draft safe jokes, hand off to Voice Lab, and reflect app state through text, mood, and video/avatar assets.

Robot behavior must be:

- Safe by default.
- Deterministic unless an explicit model/backend integration is added.
- Clear about refusals and unsupported actions.
- Backed by real app features, not fake playback or fake catalogs.
- Resilient to missing mood videos or drawable fallbacks.

## Known Requirements

- Preserve existing functionality unless a verified replacement is explicitly approved.
- All audio catalog entries must map to real packaged files.
- No fake playback, placeholder audio, or silent failure paths.
- Reactor interactions must remain discoverable and testable.
- UI must remain premium cyberpunk with neon hierarchy.
- Asset integration must include registry updates and references.
- Release readiness requires build, runtime, UI, audio, asset, and regression validation.
- AI sessions must update trackers and handoff state before ending work.

## Known Constraints

- AI context windows are limited; docs must preserve project state across sessions.
- Multiple AI assistants may work on different parts of the repo.
- Some features may be partially implemented or duplicated across native and WebView paths.
- Assets may exist but not be wired into Android packaging.
- Runtime device/emulator testing may be unavailable in some environments.
- Build tooling must be verified before Gradle is run.
- Conflict-magnet build files should not be touched unless the task explicitly requires it.

## Coding Standards

- Prefer small, focused changes.
- Do not wholesale rewrite files.
- Do not reformat unrelated code.
- Preserve public APIs unless migration is documented and validated.
- Keep Compose state hoisted where practical.
- Add accessibility labels for interactive UI.
- Avoid unused imports, dead code, duplicate resources, and duplicate catalog IDs.
- Keep media players lifecycle-safe and release resources.
- Keep threading/coroutine work lifecycle-aware.
- Never wrap imports in try/catch blocks.

## Build Requirements

Before any Gradle build:

1. Run the Android environment check script: `chmod +x scripts/build-android-debug.sh && ./scripts/build-android-debug.sh`.
2. Confirm `local.properties` exists and contains a valid `sdk.dir`.
3. Confirm `ANDROID_HOME` or `ANDROID_SDK_ROOT` points to a real SDK directory.
4. Confirm SDK contains `platforms/`, `platform-tools/`, and `build-tools/`.
5. Only then run additional Gradle commands if needed.

## Design Requirements

- Use custom neon surfaces instead of plain lists when enhancing premium screens.
- Preserve visual hierarchy: header, reactor/core content, contextual panels, dock.
- Use responsive sizing for narrow phones and large screens.
- Do not crop baked header art/text.
- Ensure all tappable controls are at least 48dp where practical.
- Support reduced-animation preference wherever animation is modified.
- Maintain readable contrast over animated/video backgrounds.
