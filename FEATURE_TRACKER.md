# Prankstar Feature Tracker

## Status Legend

| Status | Meaning |
|---|---|
| Not Started | No confirmed implementation. |
| Partial | Some code/assets exist but requirements are incomplete. |
| Implemented | Feature exists and is wired. |
| Verified | Build/runtime/QA evidence exists. |
| Blocked | Cannot proceed until dependency or issue is resolved. |
| Deprecated | Retained for compatibility but not active. |

## Completion Scale

| Completion | Meaning |
|---|---|
| 0% | Not started. |
| 25% | Skeleton or assets only. |
| 50% | Main UI or logic exists, incomplete wiring. |
| 75% | Functional, needs validation/polish. |
| 100% | Implemented, tested, documented. |

## Core Features

| ID | Feature | Status | Completion | Dependencies | Risk | Owner/Session | Notes |
|---|---|---:|---:|---|---|---|---|
| F-001 | Boot sequence | Partial | TBD | Startup flow, video/image assets | Medium | Unassigned | Verify transition to Home/Core on cold launch. |
| F-002 | Home/Core command surface | Partial | TBD | Reactor, header, dock, bot, playback | High | Unassigned | Must remain premium and interactive. |
| F-003 | Reactor sound trigger | Partial | TBD | Sound repository, playback service/controller | High | Unassigned | Tap should play real catalog sounds and stop safely. |
| F-004 | Reactor category selector | Partial | TBD | Category model, UI chips/orbit, persistence | Medium | Unassigned | Track gestures and selected state. |
| F-005 | Reactor action strip | Partial | TBD | Navigation routes, stop-all | Medium | Unassigned | Stash/Jokes/Forge actions must navigate consistently. |
| F-006 | Sound Stash / Library | Partial | TBD | Sound catalog, playback, search/filter | High | Unassigned | Real files only; no missing or corrupt entries. |
| F-007 | Sound playback controls | Partial | TBD | MediaPlayer/ExoPlayer wrappers | High | Unassigned | Play, stop, active state, release behavior. |
| F-008 | Timer prank | Partial | TBD | Scheduling, playback, lifecycle | Medium | Unassigned | Must cancel on stop/background as designed. |
| F-009 | Randomizer prank | Partial | TBD | Catalog filters, playback | Medium | Unassigned | Avoid repeating invalid/missing assets. |
| F-010 | Sound Forge | Partial | TBD | Generation engine, storage, playback | High | Unassigned | Generated files must be managed and playable. |
| F-011 | Voice Lab | Partial | TBD | Text input, safety, generation/playback | High | Unassigned | No unsafe impersonation or hidden send behavior. |
| F-012 | Twak-Attacks narrator | Partial | TBD | Header/bot assets, safety filters | Medium | Unassigned | Keep scoped to Voice Lab/Joke area unless approved. |
| F-013 | Prankstar Bot / NEO assistant | Partial | TBD | Parser, response builder, UI panel | High | Unassigned | Deterministic, local, safe by default. |
| F-014 | Bot sound recommendation | Partial | TBD | Real sound catalog | Medium | Unassigned | Must never recommend missing assets. |
| F-015 | Bot Voice Lab handoff | Partial | TBD | Shared draft bridge/store | Medium | Unassigned | Verify draft persistence and screen navigation. |
| F-016 | Header system | Partial | TBD | Drawable/raw assets, screen mappings | Medium | Unassigned | Preserve aspect and baked text. |
| F-017 | Bottom dock navigation | Partial | TBD | Route model, active state | High | Unassigned | No duplicate dock; no generic tab replacement. |
| F-018 | Settings/System | Partial | TBD | DataStore/preferences | Medium | Unassigned | Reduced animation preference must be honored where modified. |
| F-019 | Generated sound management | Partial | TBD | Storage, metadata, playback | Medium | Unassigned | Include deletion/error states. |
| F-020 | Safety and responsible-use copy | Partial | TBD | UI text, bot safety | High | Unassigned | Required for release trust and policy alignment. |

## Screen Tracker

| ID | Screen | Status | Key Files | Entry Points | Regression Risks | Notes |
|---|---|---|---|---|---|---|
| S-001 | Boot | Partial | TBD | App launch | Startup crash, stuck transition | Capture cold-start proof. |
| S-002 | Home/Core | Partial | TBD | Launch, dock | Reactor/dock/header regressions | Must be visually premium. |
| S-003 | Sound Stash | Partial | TBD | Dock, reactor action, bot | Catalog/playback crash | Validate category filters. |
| S-004 | Voice Lab/Jokes | Partial | TBD | Dock, bot handoff | Unsafe text, broken generation | Validate empty/error states. |
| S-005 | Sound Forge | Partial | TBD | Dock, action strip | Generated asset failures | Validate storage and playback. |
| S-006 | Settings/System | Partial | TBD | Dock/system action | Preference persistence | Verify reduced animation. |
| S-007 | Timer | Partial | TBD | Menus/cards | Lifecycle leaks | Verify cancel and backgrounding. |
| S-008 | Randomizer | Partial | TBD | Menus/cards | Invalid asset selection | Verify stop-all. |

## Menu and Interaction Tracker

| ID | Menu/Interaction | Status | Source Screen | Target/Effect | Dependencies | Notes |
|---|---|---|---|---|---|---|
| I-001 | Reactor tap | Partial | Home/Core | Play/stop prank sound | Catalog, playback | Must update UI state. |
| I-002 | Reactor swipe/drag | Partial | Home/Core | Change category/mode | Gesture handling | Must not block scroll unexpectedly. |
| I-003 | Dock Home/Core | Partial | Global dock | Home/Core | Navigation | Active state required. |
| I-004 | Dock Stash | Partial | Global dock | Sound Stash | Navigation | Preserve custom dock visuals. |
| I-005 | Dock Jokes | Partial | Global dock | Voice Lab | Navigation | Bot draft handoff may prefill. |
| I-006 | Dock Forge | Partial | Global dock | Sound Forge | Navigation | Verify no duplicate routes. |
| I-007 | Stop All | Partial | Multiple | Stop playback/timers | Playback controller | Must release/stop safely. |
| I-008 | Bot command submit | Partial | Bot panel | Parse command/action | Bot controller | Must reject unsafe requests. |
| I-009 | Sound row play | Partial | Library | Play selected sound | Asset path | Active row state required. |
| I-010 | Generated clip play | Partial | Forge/Stash | Play generated audio | Storage metadata | Missing file error state. |

## Dependency Tracker

| Dependency | Used By | Type | Risk | Validation |
|---|---|---|---|---|
| Android SDK | Build | Tooling | High | Environment script before Gradle. |
| Gradle/AGP/Kotlin | Build | Tooling | High | Avoid edits unless build PR. |
| Jetpack Compose | UI | Library | Medium | Compile and visual QA. |
| MediaPlayer / Media3 | Audio/video | Runtime | High | Release lifecycle and playback proof. |
| DataStore | Preferences | Runtime | Medium | Persistence checks. |
| Packaged audio catalog | Sound features | Assets | High | Catalog validator. |
| Packaged video/drawable assets | UI/reactor/bot | Assets | Medium | Resource existence and runtime rendering. |

## Risk Register

| Risk ID | Risk | Probability | Impact | Mitigation | Status |
|---|---|---:|---:|---|---|
| R-001 | AI removes premium custom UI while simplifying code. | Medium | High | Enforce UI preservation and screenshots for UI changes. | Open |
| R-002 | Asset exists in repo but not packaged into app. | High | High | Asset registry and resource/path validation. | Open |
| R-003 | Audio catalog contains missing/corrupt files. | Medium | High | Run catalog validation before release. | Open |
| R-004 | Gradle changes conflict or break sync. | Medium | High | Separate build-system PR only. | Open |
| R-005 | Device QA unavailable in AI environment. | High | Medium | Record limitation and provide local QA steps. | Open |
| R-006 | Native/WebView Home paths diverge. | Medium | Medium | Track active route and bridge behavior. | Open |

## Notes

- Update this tracker whenever a feature is implemented, verified, blocked, or deprecated.
- Do not mark anything `Verified` without a command, screenshot, log, or manual QA receipt.
- Use `SESSION_HANDOFF.md` to record current priorities and unfinished work.
