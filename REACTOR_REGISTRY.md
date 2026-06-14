# Prankstar Reactor Registry

## Purpose

`REACTOR_REGISTRY.md` tracks every reactor as an individual product asset and feature unit. It complements `REACTOR_SYSTEM.md`, `ASSET_REGISTRY.md`, `SCREEN_REGISTRY.md`, `FEATURE_TRACKER.md`, and `CURRENT_PROJECT_STATE.md`.

## Status Legend

| Status | Meaning |
|---|---|
| NOT_STARTED | Reactor asset/implementation not started. |
| IN_PROGRESS | Active work is underway. |
| PARTIAL | Asset or code exists but full wiring/testing is incomplete. |
| COMPLETE | Integrated, linked, tested, and documented. |
| BLOCKED | External blocker prevents progress. |
| UNKNOWN | Current state requires inspection. |

## Reactor Inventory Table

| Reactor ID | Reactor Name | Asset Filename | Preview Asset | Animation Asset | Audio Asset | Description | Status | Integrated | Tested | UI Linked | Persistence Linked | Settings Linked | Build Verified | Notes |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| RCTR-001 | Reactor 1 | `reactor1.mp4` | TBD | `reactor1.mp4` | Catalog-selected | Core video variant expected for Home/Web assets. | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | Verify exact packaged path before use. |
| RCTR-002 | Reactor 2 | `reactor2.mp4` | TBD | `reactor2.mp4` | Catalog-selected | Core video variant expected for Home/Web assets. | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | Verify exact packaged path before use. |
| RCTR-003 | Reactor 3 | `reactor3.mp4` | TBD | `reactor3.mp4` | Catalog-selected | Core video variant expected for Home/Web assets. | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | Verify exact packaged path before use. |
| RCTR-004 | Reactor 4 | `reactor4.mp4` | TBD | `reactor4.mp4` | Catalog-selected | Known requested reactor asset; existing docs indicate package work may exist. | PARTIAL | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | Reverify exact Android asset path and runtime playback. |
| RCTR-005 | Reactor 5 | `reactor5.mp4` | TBD | `reactor5.mp4` | Catalog-selected | Core video variant expected for Home/Web assets. | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | Verify exact packaged path before use. |
| RCTR-006 | Reactor 6 | `reactor6.mp4` | TBD | `reactor6.mp4` | Catalog-selected | Core video variant expected for Home/Web assets. | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | Verify exact packaged path before use. |
| RCTR-007 | Reactor 7 | `reactor7.mp4` | TBD | `reactor7.mp4` | Catalog-selected | Root asset observed; packaging/reference status must be inspected. | PARTIAL | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | Do not assume root file is packaged. |
| RCTR-008 | Reactor 11 | `reactor11.mp4` | TBD | `reactor11.mp4` | Catalog-selected | Root asset observed; future/alternate reactor candidate. | PARTIAL | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | Requires intake decision before product use. |
| RCTR-009 | Native Compose Reactor | Code-driven | Static core/drawable if wired | Compose animation | Catalog-selected | Native animated reactor implementation using Compose state and drawing. | PARTIAL | UNKNOWN | UNKNOWN | PARTIAL | UNKNOWN | UNKNOWN | UNKNOWN | Verify active code path and selected category/playback behavior. |
| RCTR-010 | Ultimate Reactor | Code/video hybrid | TBD | Compose/video effects | Catalog-selected | Enhanced reactor mode described in existing docs and QA artifacts. | PARTIAL | UNKNOWN | UNKNOWN | PARTIAL | UNKNOWN | UNKNOWN | UNKNOWN | Verify whether active route uses native or WebView variant. |

## Reactor Dependency Table

| Reactor ID | UI Components | Playback Dependencies | Navigation Dependencies | Persistence Dependencies | Asset Dependencies | Risk |
|---|---|---|---|---|---|---|
| RCTR-001 to RCTR-008 | Home/Core reactor display, selector, WebView/native media component | Sound repository, playback stop/deploy bridge | Dock/action strip routes | Selected reactor/mode storage if implemented | MP4 file in packaged path | High |
| RCTR-009 | Native reactor composables/state | Sound repository, MediaPlayer/Media3 bridge | Action strip, Home route | Category/mode preference if implemented | Drawable/code animation assets | Critical |
| RCTR-010 | Ultimate reactor components and Home route | Sound repository, active playback state | Action strip/dock | Mode preference if implemented | Video/header/core assets | Critical |

## Reactor Asset Table

| Reactor ID | Asset | Expected Package Location | Source Location | Referencing Files | Registry Link | Status | Notes |
|---|---|---|---|---|---|---|---|
| RCTR-001 | `reactor1.mp4` | `app/src/main/assets/prankstar/assets/` or `res/raw` | TBD | TBD | `ASSET_REGISTRY.md` | UNKNOWN | Confirm before wiring. |
| RCTR-002 | `reactor2.mp4` | `app/src/main/assets/prankstar/assets/` or `res/raw` | TBD | TBD | `ASSET_REGISTRY.md` | UNKNOWN | Confirm before wiring. |
| RCTR-003 | `reactor3.mp4` | `app/src/main/assets/prankstar/assets/` or `res/raw` | TBD | TBD | `ASSET_REGISTRY.md` | UNKNOWN | Confirm before wiring. |
| RCTR-004 | `reactor4.mp4` | `app/src/main/assets/prankstar/assets/` or `res/raw` | Repository root if present | TBD | `ASSET_REGISTRY.md` | PARTIAL | Existing docs mention packaging; revalidate. |
| RCTR-005 | `reactor5.mp4` | `app/src/main/assets/prankstar/assets/` or `res/raw` | TBD | TBD | `ASSET_REGISTRY.md` | UNKNOWN | Confirm before wiring. |
| RCTR-006 | `reactor6.mp4` | `app/src/main/assets/prankstar/assets/` or `res/raw` | TBD | TBD | `ASSET_REGISTRY.md` | UNKNOWN | Confirm before wiring. |
| RCTR-007 | `reactor7.mp4` | `app/src/main/assets/prankstar/assets/` or `res/raw` | Repository root if present | TBD | `ASSET_REGISTRY.md` | PARTIAL | Root presence is not packaging proof. |
| RCTR-008 | `reactor11.mp4` | Proposed | Repository root if present | None until approved | `ASSET_REGISTRY.md` | PARTIAL | Intake required before product use. |

## Reactor Audio Table

| Reactor ID | Audio Source | Selection Rules | Stop Rules | Validation | Status |
|---|---|---|---|---|---|
| All active reactors | Real sound catalog only | Selected category or documented random-safe selection | Stop All and tap-toggle behavior must stop active playback | Catalog validator plus runtime play/stop | PARTIAL |
| Generated-sound reactor action | Generated clip catalog if explicitly wired | Must exclude missing/stale generated files | Stop All must release generated playback | Create/play/delete/missing-file QA | UNKNOWN |
| Bot-triggered reactor action | Bot recommendation from real catalog | Bot must not recommend missing assets | Stop All must override bot-triggered playback | Bot command and playback QA | UNKNOWN |

## Reactor Animation Table

| Reactor ID | Animation Type | Reduced Animation Behavior | Performance Risk | Validation |
|---|---|---|---|---|
| RCTR-001 to RCTR-008 | MP4 loop or WebView/native video playback | Replace with still/low-motion/fewer effects where implemented | Video decode, memory, frame drops | Runtime observe, background/foreground, no tap blocking. |
| RCTR-009 | Compose pulse/glow/waveform/orbit | Reduce pulse/waveform intensity and non-essential motion | Recomposition/allocations | Inspect animation state and runtime smoothness. |
| RCTR-010 | Compose/video hybrid effects | Reduce layered motion and transitions | High due to layered visuals | Runtime observe on compact device. |

## Reactor Integration Status Table

| Reactor ID | Asset Present | Packaged | Code Referenced | Selector Visible | Deploy Works | Stop Works | Category Works | Persisted | Settings Exposed | Verified Date | Evidence |
|---|---|---|---|---|---|---|---|---|---|---|---|
| RCTR-001 | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | TBD | TBD |
| RCTR-002 | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | TBD | TBD |
| RCTR-003 | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | TBD | TBD |
| RCTR-004 | PARTIAL | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | TBD | Existing docs mention asset work; reverify. |
| RCTR-005 | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | TBD | TBD |
| RCTR-006 | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | TBD | TBD |
| RCTR-007 | PARTIAL | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | TBD | Root asset presence observed. |
| RCTR-008 | PARTIAL | UNKNOWN | NO | NO | NO | NO | NO | NO | NO | TBD | Intake candidate only. |
| RCTR-009 | UNKNOWN | N/A | UNKNOWN | PARTIAL | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | TBD | Inspect active native route. |
| RCTR-010 | UNKNOWN | N/A | UNKNOWN | PARTIAL | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | TBD | Inspect active Home/Core route. |

## Reactor Testing Checklist

| Check | Action | Pass Criteria |
|---|---|---|
| Asset existence | Verify exact source and package paths | Referenced file exists where app loads it. |
| Build/resource check | Run after Android SDK environment verification if code/resources changed | Build succeeds with no missing resources. |
| Home visibility | Launch Home/Core | Reactor visible, readable, and not covered. |
| Deploy | Tap reactor | Real catalog sound plays and state updates. |
| Stop | Tap while playing or press Stop All | Playback stops and state resets. |
| Category/mode | Change category/mode | Visible selection updates and future deploy follows it. |
| Navigation | Use action strip/dock | Correct route opens and back behavior is stable. |
| Reduced animation | Toggle preference | Modified animations reduce appropriately. |
| Lifecycle | Background/foreground during animation/playback | No crash, leak, or stuck playback. |

## Reactor Acceptance Criteria

- Reactor is listed in this registry before product integration.
- Assets are registered in `ASSET_REGISTRY.md` with exact paths.
- UI entry point is documented in `SCREEN_REGISTRY.md`.
- Deploy and stop use real playback paths only.
- Missing optional assets fall back safely.
- Accessibility labels exist for primary reactor controls.
- Build impact is verified when code/resources change.
- Runtime QA evidence is recorded before status becomes COMPLETE.

## New Reactor Intake Procedure

1. Add a row to Reactor Inventory with status IN_PROGRESS.
2. Add all source and target assets to `ASSET_REGISTRY.md`.
3. Verify file names are Android-safe for the intended destination.
4. Decide whether the reactor is native, WebView, video, image, or Compose-driven.
5. Define selector label, preview, fallback, audio rules, and persistence behavior.
6. Implement behind a safe selector or feature flag; do not replace default reactor without approval.
7. Validate build, Home visibility, deploy, stop, category/mode, navigation, and lifecycle.
8. Update `CURRENT_PROJECT_STATE.md`, `FEATURE_TRACKER.md`, and `SESSION_HANDOFF.md`.

## Reactor Replacement Procedure

1. Document the current reactor and proposed replacement IDs.
2. Verify all old references and asset dependencies.
3. Confirm replacement meets every acceptance criterion.
4. Keep rollback path to previous reactor until release verification passes.
5. Do not delete old assets until references and QA evidence prove they are unused.
6. Update screenshots/QA docs and all registries.

## Reactor Removal Procedure

1. Mark reactor DEPRECATED before deletion.
2. Search for references with `rg` and record results.
3. Remove selector entries and code references in one focused PR.
4. Remove assets only after no references remain and release owner approves.
5. Validate build/package/runtime.
6. Update `ASSET_REGISTRY.md`, `SCREEN_REGISTRY.md`, and `CURRENT_PROJECT_STATE.md`.

## AI Reactor Validation Procedure

1. Read `REACTOR_SYSTEM.md`, `REACTOR_REGISTRY.md`, `ASSET_REGISTRY.md`, `SCREEN_REGISTRY.md`, and `AI_RULES.md`.
2. Identify reactor ID and exact files/assets.
3. Verify assets exist before referencing.
4. Inspect deploy/stop/category/persistence wiring.
5. Run targeted checks; verify Android environment before Gradle if needed.
6. Record evidence and update registry statuses.

## Regression Checklist

- Home/Core still renders after boot.
- Default reactor still visible.
- Tap deploy still plays real sound.
- Stop All still stops all active sound.
- Dock/action navigation still works.
- Bot state still updates appropriately.
- Reduced animation is not worsened.
- Missing optional reactor assets do not crash app.
- No duplicate reactors/docks appear unintentionally.

## Maintenance Procedure

- Update this file whenever a reactor asset, selector, UI mode, audio rule, persistence rule, or settings entry changes.
- Keep reactor asset rows aligned with `ASSET_REGISTRY.md`.
- Keep reactor status aligned with `CURRENT_PROJECT_STATE.md`.
- Do not mark `Tested`, `Build Verified`, or `COMPLETE` without evidence.
