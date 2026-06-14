# Prankstar Robot Specification

## Purpose

`PRANKSTAR_ROBOT.md` is the authoritative specification for the Prankstar Robot / NEO subsystem. It documents identity, architecture, UI behavior, animation, voice/audio, triggers, interactions, integrations, validation, and maintenance.

## Robot Identity

| Field | Specification |
|---|---|
| Name | Prankstar Robot / NEO unless product naming changes. |
| Role | Local prank-lab assistant, guide, safety layer, recommendation engine, and animated companion. |
| Personality | Cyberpunk lab assistant: playful, concise, mischievous, safe, non-threatening, and clear. |
| Safety posture | Refuse unsafe, illegal, harassing, impersonation, emergency-hoax, hidden-recording, stalking, doxxing, and non-consensual requests. |
| Default intelligence model | Local deterministic behavior unless an explicit backend/model integration is added and documented. |

## Robot Purpose

- Help users discover real catalog sounds.
- Explain reactor and feature controls.
- Recommend prank sounds by category or mood.
- Draft safe jokes/narration and hand off to Voice Lab.
- Reflect app state with mood, copy, and animation.
- Provide safety guardrails and redirect unsafe requests.
- Never pretend to execute actions that are not actually wired.

## Robot Architecture

| Layer | Responsibility | Expected Files/Owners |
|---|---|---|
| UI panel | Renders robot avatar/video, message, input, suggestions, and actions | Bot components under `app/src/main/java/com/pranksterlab/` after inspection |
| Command parser | Converts text/taps into intents | Bot parser/intent classes if present |
| Safety filter | Rejects unsafe requests and sanitizes text | Bot safety classes and feature-specific filters |
| Controller | Orchestrates recommendations, navigation, playback, handoff | Bot controller classes if present |
| Response builder | Creates user-visible bot responses and actions | Bot response classes if present |
| Sound recommender | Searches real sound catalog only | Sound repository/catalog integration |
| Voice Lab bridge | Passes safe drafts to Voice Lab | Shared in-process store/bridge if present |
| Mood resolver | Maps state/events to mood assets | Bot mood/video components |
| Asset resolver | Loads video/image/audio fallback safely | Android raw/drawable/assets resources |

## Robot Personality Rules

| Rule | Required Behavior |
|---|---|
| Playful but safe | Use prank-lab language without threats, harassment, or targeted abuse. |
| Honest | Do not claim cloud intelligence, sending messages, recording, spoofing, or controlling devices unless actually implemented and permitted. |
| Helpful | Offer safe alternatives when refusing unsafe requests. |
| Action-aware | Only show actions that are wired and validated. |
| Concise | Keep UI responses short enough for compact panels. |
| State-aware | Reflect playback, generation, errors, and navigation context. |

## Robot UI Behavior

| UI Element | Requirement | Acceptance Criteria |
|---|---|---|
| Avatar/video | Shows mood animation or safe fallback | Missing video does not crash; aspect ratio preserved. |
| Message text | Communicates current state/action/refusal | Readable, concise, no unsafe copy. |
| Suggestions | Offers safe relevant actions | Buttons are wired or hidden. |
| Input field | Accepts command text where present | Label/hint/error state accessible. |
| Action buttons | Navigate/play/handoff/stop | Real effects only; loading/error states shown. |
| Panel placement | Does not cover reactor or primary controls | Compact layout tested. |

## Robot Animation Behavior

| Mood/State | Visual Behavior | Fallback |
|---|---|---|
| IDLE | Calm loop/static assistant | Static robot image or branded panel. |
| LISTENING | Subtle attentive animation | IDLE fallback. |
| THINKING | Thinking/searching loop | IDLE fallback with text state. |
| EXCITED | High-energy success/deploy animation | IDLE fallback with success copy. |
| WARNING | Caution/refusal visual | Static warning-styled panel. |
| ERROR | Error/failure state | Static error copy and retry/stop action. |
| PLAYING | Deployed/reactor-active visual | EXCITED or IDLE fallback. |

Animation must respect reduced-animation preference where modified and must release video resources safely.

## Robot Voice Behavior

| Capability | Current Rule | Validation |
|---|---|---|
| Spoken bot voice | UNKNOWN until implementation inspection | Do not claim voice output unless verified. |
| Generated narration handoff | Voice Lab/Twak workflows may generate safe narration | Verify text safety and generated audio playback. |
| Voice impersonation | Forbidden unless legal/safe policy and explicit implementation exist | Refuse real-person voice cloning/impersonation. |
| Volume/audio effects | Must use real audio assets or generated files | Playback and stop-all validation required. |

## Robot Sound Effects

| Sound Type | Purpose | Source | Status | Rule |
|---|---|---|---|---|
| UI chirp/beep | Feedback for bot actions | Registered audio asset if present | UNKNOWN | Must be real, cataloged/registered, and stoppable if long. |
| Warning tone | Unsafe/refusal/error feedback | Registered audio asset if present | UNKNOWN | Must not be alarming or offensive. |
| Deploy cue | Reactor/playback excitement | Catalog or registered effect | UNKNOWN | Must not mask active playback controls. |
| Generated voice clip | User-created narration | App-private generated file | PARTIAL | Missing file must show explicit error. |

## Robot Event Registry

| Event ID | Event | Trigger | Expected Robot State | Action | Status |
|---|---|---|---|---|---|
| BOT-E001 | App/Home loaded | Home/Core appears | IDLE | Greet or show ready state | UNKNOWN |
| BOT-E002 | Reactor armed | Reactor idle | IDLE | Prompt deploy/category action | UNKNOWN |
| BOT-E003 | Reactor tapped | User deploys prank | EXCITED/PLAYING | Acknowledge deployment | UNKNOWN |
| BOT-E004 | Playback stopped | Stop All/toggle stop | IDLE | Confirm stopped state | UNKNOWN |
| BOT-E005 | Sound recommendation requested | Text/action | THINKING then IDLE/EXCITED | Search real catalog and offer/play sound | UNKNOWN |
| BOT-E006 | Voice Lab draft requested | Text/action | THINKING | Create safe draft and navigate/handoff | UNKNOWN |
| BOT-E007 | Unsafe request | Text/action | WARNING | Refuse and offer safe alternative | UNKNOWN |
| BOT-E008 | Asset/playback failure | Media error | ERROR | Explain failure and provide retry/stop | UNKNOWN |
| BOT-E009 | Generated clip complete | Generation success | EXCITED | Offer play/save/manage | UNKNOWN |
| BOT-E010 | Reduced animation enabled | Settings change | IDLE/static | Use lower-motion visuals | UNKNOWN |

## Robot State Machine

| State | Entry Conditions | Allowed Actions | Exit Conditions | UI Requirements |
|---|---|---|---|---|
| IDLE | Default, stopped, no active request | Suggest, listen, navigate, recommend | User input, playback, generation, error | Calm avatar/message. |
| LISTENING | Input focused or command mode open | Submit/cancel | Submit, blur, cancel | Clear input state. |
| THINKING | Parsing/search/generation prep | Cancel if long-running | Success, refusal, error | Loading indicator, no duplicate submissions. |
| PLAYING | Sound/video/audio active | Stop All, navigate, recommend next | Playback complete/stop/error | Active status and stop access. |
| WARNING | Unsafe request/refusal | Safe alternative, dismiss | User chooses safe action/dismiss | Non-shaming refusal copy. |
| ERROR | Failed action/media/asset | Retry, dismiss, stop | Retry success/dismiss | Clear error and recovery. |
| DISABLED | Feature unavailable | Navigate/settings/help | Feature enabled/route changed | Explain unavailable state. |

## Robot Interaction Matrix

| User Action | Robot Input | System Action | Required Response | Regression Risk |
|---|---|---|---|---|
| Tap suggested sound | Button/action | Play real catalog sound | PLAYING state or explicit error | Missing asset/silent failure. |
| Type sound request | Text | Parse/recommend | Real matches or helpful no-result | Fake catalog data. |
| Ask for unsafe prank | Text | Safety refusal | WARNING with safe alternative | Policy/release risk. |
| Ask for joke/narration | Text | Draft safe text | Voice Lab handoff or editable draft | Unsafe generated content. |
| Tap stop | Button/action | Stop playback | IDLE stopped confirmation | Playback leak. |
| Navigate via bot | Button/action | Route change | Correct screen and active state | Broken navigation/back stack. |

## Robot Trigger Matrix

| Trigger Source | Trigger | Robot Behavior | Dependencies | Validation |
|---|---|---|---|---|
| Reactor | Deploy/stop/category | Mood/message update | Reactor state, playback | Tap deploy/stop QA. |
| Audio | Playback start/complete/error | PLAYING/IDLE/ERROR | Playback callbacks | Play/finish/error QA. |
| Navigation | Route opened | Contextual message | Route state | Dock/bot route QA. |
| Voice Lab | Draft/generation state | THINKING/EXCITED/ERROR | Generation state | Empty/error/success QA. |
| Settings | Reduced animation | Static/low-motion mode | Preference state | Toggle/relaunch QA. |
| Asset resolver | Missing mood asset | Fallback UI | Drawable/static fallback | Simulate/inspect fallback. |

## Robot Menu Integrations

| Menu/Surface | Robot Role | Status | Acceptance Criteria |
|---|---|---|---|
| Home/Core | Assistant panel and reactor companion | PARTIAL | Does not cover reactor; reflects playback. |
| Voice Lab/Jokes | Draft helper and safe prompt guide | PARTIAL | Handoff is editable and safe. |
| Sound Stash | Recommendation/search helper if present | UNKNOWN | Uses real catalog entries. |
| Forge | Generated clip helper if present | UNKNOWN | Handles generation progress/errors. |
| Settings/System | Safety/help/reduced animation awareness | UNKNOWN | Preference changes affect robot as documented. |

## Robot Navigation Integrations

| Action | Destination | Data Passed | Validation |
|---|---|---|---|
| Open Stash | Sound Stash | Optional category/query | Screen opens and query/category handled safely. |
| Open Jokes/Voice Lab | Voice Lab | Safe draft text/template | Draft appears editable. |
| Open Forge | Sound Forge | Optional generated context | Forge opens without losing state. |
| Stop All | Current route | Stop command | Active audio stops. |
| Help/Settings | Settings/System | Optional help anchor | Settings opens and back works. |

## Robot Reactor Integrations

| Reactor State | Robot State | Required Behavior |
|---|---|---|
| Idle/Armed | IDLE | Prompt safe deploy/category action. |
| Charging | THINKING/EXCITED | Indicate preparing/deploying without blocking controls. |
| Playing | PLAYING | Show active sound/status and stop affordance. |
| Stopped | IDLE | Confirm stopped/ready state. |
| Disabled | DISABLED | Explain unavailable reactor and safe next step. |

## Robot Audio Integrations

| Audio Event | Robot Behavior | Validation |
|---|---|---|
| Sound starts | PLAYING message/mood | Active state appears. |
| Sound completes | Return to IDLE or ready state | UI resets. |
| Stop All | Stop robot-related long audio if any | No sound continues. |
| Error/missing asset | ERROR message | No crash and no silent failure. |
| Generated clip saved | Offer play/manage | Clip path exists or error shown. |

## Robot Onboarding Behavior

| Moment | Behavior | Acceptance Criteria |
|---|---|---|
| First Home/Core view | Explain reactor and safe prank use briefly | Dismissible or non-blocking. |
| First Voice Lab use | Explain safe joke/narration input | Clear prohibited categories. |
| First unsafe request | Refuse with safe alternative | No shame, no policy jargon overload. |
| First generated clip | Explain play/save/manage | User can find generated clip later. |

## Robot Future Expansion Plans

| Expansion | Preconditions | Required Docs/Validation |
|---|---|---|
| Cloud AI integration | Privacy/security policy, user consent, backend design, failure modes | Update privacy docs, networking rules, safety tests. |
| More mood videos | Asset registry, fallback mapping, reduced animation support | Update robot/asset registries and runtime QA. |
| Voice output | Registered audio/generation path and stop controls | Audio validation and safety copy. |
| Personalization | Preference model and privacy review | Settings, persistence, reset behavior. |

## Robot Component Registry

| Component | Purpose | File Location | Dependencies | Status | Notes |
|---|---|---|---|---|---|
| Bot panel component | Render UI and actions | Inspect `app/src/main/java/com/pranksterlab/` | Compose, bot state, assets | UNKNOWN | Add exact file after inspection. |
| Bot controller | Orchestrate commands/actions | Inspect bot package | Parser, repository, navigation | UNKNOWN | Must be safe and deterministic. |
| Bot parser | Convert text to intents | Inspect bot package | Intent model, safety | UNKNOWN | Avoid broad natural-language claims. |
| Bot response builder | Create copy/actions | Inspect bot package | Safety, localization/copy | UNKNOWN | Keep copy concise and safe. |
| Bot video/avatar | Display mood assets | Inspect components/resources | Raw/drawable assets, media player | UNKNOWN | Fallback required. |

## Robot Asset Registry

| Asset | Purpose | Location | Status | Notes |
|---|---|---|---|---|
| `prankstar_bot_*` | Default robot mood videos if packaged | `app/src/main/res/raw/` expected | UNKNOWN | Verify exact names. |
| `twakbot_*` | Twak-Attacks robot moods | `app/src/main/res/raw/` or drawable expected | UNKNOWN | Feature-scoped. |
| `prankstar_robot_power.png` | Static/source fallback | Root or drawable if packaged | PARTIAL | Root presence is not packaging proof. |
| `thinking.mp4` | Mood video source | Repository root observed | PARTIAL | Must be packaged before reference. |
| `surprised.mp4` | Mood video source | Repository root observed | PARTIAL | Must be packaged before reference. |
| `angry.mp4` | Mood video source | Repository root observed | PARTIAL | Must be packaged before reference. |

## Robot Animation Registry

| Animation | State | Asset/Implementation | Reduced Animation Rule | Status |
|---|---|---|---|---|
| Idle loop | IDLE | Video/static/Compose | Static or slower loop | UNKNOWN |
| Thinking loop | THINKING | Video/static/Compose | Static with loading text | UNKNOWN |
| Excited loop | EXCITED/PLAYING | Video/static/Compose | Lower intensity/static | UNKNOWN |
| Warning state | WARNING | Video/static/Compose | Static warning panel | UNKNOWN |
| Error state | ERROR | Static/error UI | Static | UNKNOWN |

## Robot Audio Registry

| Audio | Purpose | Location | Status | Validation |
|---|---|---|---|---|
| Bot UI effect | Optional action feedback | TBD | UNKNOWN | Asset registered and playback safe. |
| Bot warning effect | Optional refusal/error cue | TBD | UNKNOWN | Not alarming; stoppable if long. |
| Bot/generated voice | Narration/voice output if implemented | Generated storage or catalog | UNKNOWN | Safety filter and playback QA. |

## Robot Acceptance Criteria

- Robot is safe, deterministic, and honest about capabilities.
- Every robot action maps to a real app route, playback path, or documented no-op/error.
- Unsafe requests are refused with safe alternatives.
- Mood assets have fallbacks.
- Bot panel does not block reactor, dock, or critical controls.
- Bot text is readable on compact screens.
- Stop All overrides robot-triggered playback.
- Robot-related changes update this file, `SCREEN_REGISTRY.md`, `ASSET_REGISTRY.md`, and `CURRENT_PROJECT_STATE.md`.

## Robot Regression Checklist

- Home/Core bot panel still appears where expected.
- Reactor deploy/stop changes bot state correctly.
- Bot does not recommend missing assets.
- Bot refuses unsafe prompts.
- Voice Lab handoff remains editable and safe.
- Missing bot video falls back without crash.
- Reduced animation setting is not worsened.
- Navigation actions open correct screens.
- Stop All stops robot-triggered sound.

## Robot Validation Procedure

1. Inspect bot files and exact resource names.
2. Run parser/safety checks if tests exist.
3. Verify recommendations use real catalog entries.
4. Verify Home/Core state transitions: idle, deploy, playing, stopped, error.
5. Verify unsafe request refusal copy.
6. Verify Voice Lab handoff.
7. Verify missing mood asset fallback by code review or controlled runtime path.
8. Verify Android build only after environment checks if code/resources changed.
9. Update registries and handoff.

## Robot Expansion Procedure

1. Define expansion purpose, user value, safety impact, and privacy impact.
2. Add component/assets/events to this file before implementation.
3. Add assets to `ASSET_REGISTRY.md` and screens to `SCREEN_REGISTRY.md` as needed.
4. Implement behind clear UI state and fallback behavior.
5. Validate safety, playback/navigation, accessibility, and release implications.
6. Update `CURRENT_PROJECT_STATE.md`, `FEATURE_TRACKER.md`, and release checklist.

## AI Development Rules

- Do not invent robot capabilities.
- Do not add network/cloud behavior without explicit scope and privacy/security updates.
- Do not bypass safety filters.
- Do not use fake sounds, fake recommendations, or fake generation results.
- Do not remove fallback assets or error states.
- Do not let robot UI cover reactor or stop controls.
- Always include rollback and validation steps.

## Maintenance Procedure

- Update this file whenever robot state, copy, action routing, safety behavior, assets, or audio changes.
- Keep robot statuses aligned with `CURRENT_PROJECT_STATE.md`.
- Keep asset rows aligned with `ASSET_REGISTRY.md`.
- Do not mark COMPLETE without runtime or test evidence.
