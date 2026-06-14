# Prankstar Reactor System

## Purpose

The reactor is the central interaction model for Prankstar. It combines visual identity, category selection, audio deployment, quick navigation, assistant state, and animation feedback.

## Reactor Architecture

| Layer | Responsibility |
|---|---|
| Reactor model/state | Current category, mode, playback state, charge/progress, enabled state. |
| Reactor UI | Core visual, orbit/category controls, waveforms, side strips, action buttons. |
| Interaction handler | Taps, long press, swipe/drag, stop, category selection. |
| Playback bridge | Converts deploy actions into real catalog playback or stop-all calls. |
| Navigation bridge | Routes action-strip buttons to Stash, Jokes, Forge, System. |
| Persistence bridge | Saves selected reactor mode/category where implemented. |
| Asset resolver | Maps configured reactor videos/images to packaged resources or fallbacks. |

## Asset Locations

| Asset Type | Expected Locations | Notes |
|---|---|---|
| Reactor videos | `app/src/main/assets/prankstar/assets/reactor*.mp4`, `app/src/main/res/raw/` | Confirm exact names before referencing. |
| Reactor images | `app/src/main/res/drawable/` | Use for static/fallback core art. |
| Header/background videos | `app/src/main/res/raw/`, `app/src/main/assets/prankstar/assets/` | Must loop/release safely. |
| WebView reactor assets | `app/src/main/assets/web-ui/`, `app/src/main/assets/prankstar/assets/` | Keep bridge mappings documented. |
| QA screenshots | `qa/screenshots/` | Store proof for visual regression checks. |

## Selection Logic

Reactor selection logic should be deterministic and testable.

| Input | Expected Result | Validation |
|---|---|---|
| Tap center while idle | Deploy selected/random sound. | Audio starts, state becomes playing/deployed. |
| Tap center while playing | Stop active playback or follow documented toggle behavior. | Audio stops, state returns to armed/stopped. |
| Swipe left/right | Change selected category or reactor mode. | UI highlight and state update. |
| Category chip/orbit tap | Select explicit category. | Future deploy uses selected category. |
| Stop All | Stop active audio/timers/generated playback. | No continued sound, UI resets. |
| Dock/action navigation | Navigate without losing app stability. | Correct route and active state. |

## Animation Behavior

| State | Required Visual Behavior |
|---|---|
| Idle / Armed | Slow pulse, readable status, low-intensity glow. |
| Charging | Increased pulse/halo/waveform movement, no layout jump. |
| Playing / Deployed | Active glow, waveform/VU feedback, assistant state update. |
| Stopped | Smooth return to idle, no stuck animation. |
| Disabled | Muted/desaturated visual with clear disabled affordance. |

Animation requirements:

- Respect reduced-animation preference for new or modified animation paths.
- Do not create unbounded allocations in frame loops.
- Keep animation code lifecycle-safe in Compose.
- Use stable keys/state to avoid restarting video or heavy animations unnecessarily.

## Persistence Behavior

| Preference | Expected Behavior | Storage |
|---|---|---|
| Selected reactor mode | Restored on app relaunch where implemented. | DataStore or existing settings store. |
| Selected category | Restored if product decision requires it. | DataStore or route state. |
| Reduced animation | Reduces or disables non-essential motion. | Settings preference. |
| Last active sound | Optional display only; must not auto-play without user action. | In-memory or explicit persistence if approved. |

## UI Behavior

- Reactor must remain the focal point on Home/Core.
- Assistant panel must not cover the reactor or critical controls.
- Action strip must remain visible or reachable on narrow screens.
- Dock must not overlap core interaction targets.
- Text over video/image backgrounds must maintain contrast.
- Loading/missing asset states must degrade gracefully.

## Touch Behavior

| Target | Minimum Expected Behavior |
|---|---|
| Reactor center | 48dp+ effective target, accessible label, deploy/stop semantics. |
| Category nodes/chips | 48dp+ where practical, selected state announced. |
| Action buttons | 48dp+ target, clear label and route/action. |
| Stop All | Always obvious when audio is active. |
| Gestures | Must not prevent vertical scroll unless gesture is clearly reactor-owned. |

## Future Reactor Integration Procedure

1. Add the new reactor asset or UI variant without removing existing variants.
2. Register asset details in `ASSET_REGISTRY.md`.
3. Add or update a reactor mode entry in this document.
4. Wire selection using a small, isolated change.
5. Preserve fallback behavior for missing assets.
6. Add accessibility labels for new interactive targets.
7. Validate build impact after Android SDK environment checks.
8. Verify runtime behavior: idle, deploy, stop, navigate, rotate category/mode.
9. Capture screenshot/video proof for visual changes when possible.
10. Update `FEATURE_TRACKER.md` and `SESSION_HANDOFF.md`.

## Reactor Acceptance Checklist

| Check | Pass Criteria | Result |
|---|---|---|
| Visual presence | Reactor visible on Home/Core after launch. | TBD |
| Tap deploy | Real sound plays from catalog. | TBD |
| Tap/stop | Sound stops and UI resets. | TBD |
| Category state | Category changes are visible and affect deploy scope. | TBD |
| Navigation | Action strip/dock routes open correct screens. | TBD |
| Accessibility | Key controls have labels and usable touch targets. | TBD |
| Reduced animation | Modified animations respect preference. | TBD |
| Asset fallback | Missing optional reactor asset does not crash app. | TBD |
