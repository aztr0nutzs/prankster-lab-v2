# Prankstar Current Project State

## Purpose

`CURRENT_PROJECT_STATE.md` is the living truth source for Prankstar. It records the current build, feature, UI, navigation, reactor, robot, asset, bug, task, risk, blocker, and release-readiness state. Update it at the beginning and end of every meaningful AI or human work session.

## Status Legend

| Status | Meaning |
|---|---|
| NOT_STARTED | No confirmed work has begun. |
| IN_PROGRESS | Active work is underway. |
| PARTIAL | Some implementation or documentation exists but validation/completion is incomplete. |
| COMPLETE | Implemented, verified, and documented. |
| BLOCKED | Progress is blocked by an explicit dependency or issue. |
| UNKNOWN | Current state has not been verified in the current context. |

## Current Build Status

| Area | Status | Evidence | Last Verified | Next Action |
|---|---|---|---|---|
| Android SDK environment | UNKNOWN | Not verified in this documentation-only update | 2026-06-14 | Run environment script before any Gradle build. |
| Gradle sync/build | UNKNOWN | Not run for documentation-only change | 2026-06-14 | Verify only when code/resources/build files change. |
| Debug APK | UNKNOWN | Existing historical docs may contain prior build proof; current session did not build | 2026-06-14 | Run approved build process in Android-ready environment. |
| Release artifact | NOT_STARTED | No current release artifact recorded here | 2026-06-14 | Complete release checklist first. |
| Dependency health | UNKNOWN | Current audit not run in this session | 2026-06-14 | Review dependency/audit reports before release. |

## Current Feature Status

| Feature ID | Feature | Status | Evidence | Risk | Next Action |
|---|---|---|---|---|---|
| F-001 | Boot sequence | PARTIAL | Existing docs reference boot behavior; current code not reinspected here | Medium | Runtime cold-launch QA. |
| F-002 | Home/Core command surface | PARTIAL | Existing docs and QA artifacts reference Home/Core | High | Inspect active route and verify on device/emulator. |
| F-003 | Reactor sound trigger | PARTIAL | Existing workflow docs require real catalog playback | High | Verify tap deploy and stop. |
| F-004 | Reactor category selector | PARTIAL | Registry rows created; code state unknown | Medium | Inspect selector state and persistence. |
| F-005 | Reactor action strip | PARTIAL | Existing docs reference action strip | Medium | Verify route buttons on compact devices. |
| F-006 | Sound Stash / Library | PARTIAL | Existing docs reference catalog playback | High | Run catalog validator and runtime playback. |
| F-007 | Sound playback controls | PARTIAL | Existing docs require play/stop validation | High | Verify MediaPlayer/Media3 lifecycle. |
| F-008 | Timer prank | UNKNOWN | Not inspected in this session | Medium | Locate implementation and validate lifecycle. |
| F-009 | Randomizer prank | UNKNOWN | Not inspected in this session | Medium | Locate implementation and validate asset selection. |
| F-010 | Sound Forge | PARTIAL | Existing docs reference generated sound management | High | Verify generate/play/delete/missing-file states. |
| F-011 | Voice Lab | PARTIAL | Existing docs reference Voice Lab/Jokes | High | Verify safety and handoff flows. |
| F-012 | Twak-Attacks narrator | PARTIAL | Existing docs reference Twak assets/features | Medium | Verify scoped integration and packaged assets. |
| F-013 | Prankstar Robot / NEO assistant | PARTIAL | Existing docs and new spec define subsystem | High | Inspect bot components and validate state machine. |
| F-014 | Bot sound recommendation | UNKNOWN | Not verified in this session | Medium | Confirm real catalog usage only. |
| F-015 | Bot Voice Lab handoff | UNKNOWN | Not verified in this session | Medium | Verify draft bridge and navigation. |
| F-016 | Header system | PARTIAL | Existing docs reference custom headers | Medium | Verify aspect/readability on compact width. |
| F-017 | Bottom dock navigation | PARTIAL | Existing docs reference premium dock | High | Verify single dock and active state. |
| F-018 | Settings/System | PARTIAL | Existing docs reference settings/reduced animation | Medium | Verify persistence and animation behavior. |
| F-019 | Generated sound management | UNKNOWN | Not verified in this session | Medium | Inspect storage metadata and cleanup. |
| F-020 | Safety and responsible-use copy | PARTIAL | Policies/templates exist; runtime coverage unknown | High | Verify app-visible safety surfaces. |

## Current UI Status

| UI Area | Status | Evidence | Known Risk | Next Action |
|---|---|---|---|---|
| Cyberpunk visual system | PARTIAL | Existing docs require neon/HUD preservation | AI could flatten UI | Enforce `UI_SPECIFICATION.md` and screenshots for UI changes. |
| Header system | PARTIAL | Documented, exact code state unknown | Cropped baked text | Inspect header component and compact screenshots. |
| Bottom dock | PARTIAL | Documented, exact code state unknown | Duplicate dock or generic replacement | Verify routes and active state. |
| Reactor display | PARTIAL | Documented as central | Overlap/jank/missing assets | Device QA Home/Core. |
| Bot panel | PARTIAL | Documented, exact code state unknown | Covers controls or fake actions | Validate panel placement/actions. |
| Accessibility | UNKNOWN | Not audited in this session | Missing labels/touch targets | Include in every UI task. |
| Reduced animation | PARTIAL | Existing docs note incomplete global enforcement | Motion preference inconsistency | Inspect settings and modified animations. |

## Current Navigation Status

| Flow | Status | Evidence | Risk | Next Action |
|---|---|---|---|---|
| Boot to Home/Core | PARTIAL | Existing docs reference flow | Startup stuck/crash | Cold launch QA. |
| Dock to Stash | PARTIAL | Existing docs reference dock | Broken route/active state | Runtime navigation QA. |
| Dock to Jokes/Voice Lab | PARTIAL | Existing docs reference dock/handoff | Draft route mismatch | Runtime navigation QA. |
| Dock to Forge | PARTIAL | Existing docs reference dock | Empty/generated states | Runtime navigation QA. |
| Home to Settings/System | UNKNOWN | Not verified in this session | Missing route | Inspect routes. |
| Bot navigation actions | UNKNOWN | Not verified in this session | Fake/broken actions | Validate each bot action. |
| WebView bridge navigation | UNKNOWN | Existing docs mention bridge | Native/WebView divergence | Inspect active Home path and bridge. |

## Current Reactor Status

| Reactor Area | Status | Evidence | Risk | Next Action |
|---|---|---|---|---|
| Reactor inventory | PARTIAL | `REACTOR_REGISTRY.md` created | Exact integration state unknown | Inspect assets/code and update rows. |
| Default reactor | PARTIAL | Existing docs reference active reactor | Core UX regression | Verify Home/Core runtime. |
| Reactor videos | PARTIAL | Root assets observed historically; packaged state unknown | Missing packaged assets | Verify exact paths. |
| Reactor audio deploy | PARTIAL | Required by docs | Silent/missing playback | Runtime play/stop QA. |
| Reactor persistence | UNKNOWN | Not inspected | Settings mismatch | Inspect DataStore/settings integration. |
| Reactor settings | UNKNOWN | Not inspected | User cannot select/restore mode | Inspect settings UI. |

## Current Robot Status

| Robot Area | Status | Evidence | Risk | Next Action |
|---|---|---|---|---|
| Robot specification | COMPLETE | `PRANKSTAR_ROBOT.md` created | Must keep aligned with code | Update after bot changes. |
| Bot UI panel | PARTIAL | Existing docs reference panel | Covers reactor or stale state | Inspect and runtime QA. |
| Bot command parser | UNKNOWN | Not inspected in this session | Unsafe/fake actions | Inspect parser/safety tests. |
| Bot recommendations | UNKNOWN | Not verified | Missing asset recommendations | Validate real catalog usage. |
| Bot Voice Lab handoff | UNKNOWN | Not verified | Draft loss or unsafe content | Runtime QA. |
| Bot mood assets | PARTIAL | Root/source assets observed historically | Missing packaged fallback | Verify resource mapping. |
| Bot safety | PARTIAL | Existing docs describe refusals | Policy risk | Test unsafe prompts. |

## Current Asset Status

| Asset Area | Status | Evidence | Risk | Next Action |
|---|---|---|---|---|
| Asset registry | PARTIAL | `ASSET_REGISTRY.md` exists | Needs exact references/status | Audit code/resources. |
| Audio catalog | PARTIAL | Existing docs reference catalog validation | Missing/corrupt assets | Run validator. |
| Reactor assets | PARTIAL | Registry rows created | Root vs packaged confusion | Verify package paths. |
| Robot assets | PARTIAL | Registry/spec rows created | Missing mood fallbacks | Verify raw/drawable mappings. |
| Header assets | PARTIAL | Existing docs reference headers | Cropping/readability | Compact UI screenshots. |
| Dock assets | UNKNOWN | Not verified | Dock visual mismatch | Inspect drawable/resource usage. |
| Generated assets | UNKNOWN | Not verified | Stale generated files | Generate/play/delete QA. |

## Known Bugs

| Bug ID | Severity | Status | Summary | Evidence | Next Action |
|---|---|---|---|---|---|
| BUG-ENV-001 | S2 | BLOCKED | No configured `origin` remote in this checkout prevents upstream fetch/rebase. | `git fetch origin --prune` fails with remote error. | Configure remote or run sync in a fully cloned environment. |
| BUG-QA-001 | S2 | UNKNOWN | Device/emulator availability not verified in this session. | Documentation-only session; no ADB QA. | Run device/emulator smoke tests before release. |

## Known Missing Features

| Feature | Status | Impact | Next Action |
|---|---|---|---|
| Exact screen/file inventory with code paths | PARTIAL | AI may need extra inspection before changes | Populate registries with exact paths after code audit. |
| Verified reactor selector status | UNKNOWN | Reactor variants may not be user-selectable | Inspect active UI/settings. |
| Verified robot mood asset mapping | UNKNOWN | Missing videos could crash or fallback unexpectedly | Inspect resources and fallback code. |
| Verified global reduced-animation enforcement | PARTIAL | Accessibility regression risk | Audit animation code paths. |

## Known Broken Features

| Feature | Status | Evidence | Next Action |
|---|---|---|---|
| No confirmed broken runtime feature from this documentation session | UNKNOWN | No runtime QA performed | Use `BUG_TRACKER.md` when runtime bugs are found. |

## High Priority Tasks

| Task ID | Task | Owner | Status | Acceptance Criteria |
|---|---|---|---|---|
| HP-001 | Run Android SDK/build environment verification before next Gradle build | Unassigned | UNKNOWN | Environment script passes or actionable blocker recorded. |
| HP-002 | Audit exact source files for screens/components and update `SCREEN_REGISTRY.md`/`FILE_MAP.md` | Unassigned | IN_PROGRESS | Exact file paths replace broad inspection rows. |
| HP-003 | Validate audio catalog and update asset statuses | Unassigned | UNKNOWN | 0 missing/corrupt/duplicate assets or blockers recorded. |
| HP-004 | Device/emulator smoke test Home/Core, dock, reactor, Library playback | Unassigned | UNKNOWN | Screenshots/logs recorded and trackers updated. |
| HP-005 | Verify robot safety/recommendation/handoff behavior | Unassigned | UNKNOWN | Unsafe prompts refused; real catalog actions only. |

## Medium Priority Tasks

| Task ID | Task | Owner | Status | Acceptance Criteria |
|---|---|---|---|---|
| MP-001 | Verify generated clip lifecycle | Unassigned | UNKNOWN | Create/play/delete/missing-file states pass. |
| MP-002 | Audit reduced-animation coverage | Unassigned | PARTIAL | Modified animation paths respect preference. |
| MP-003 | Verify header/dock compact layout | Unassigned | UNKNOWN | No cropping/overlap at compact width. |
| MP-004 | Populate reactor individual statuses | Unassigned | PARTIAL | Each reactor row has evidence-backed status. |

## Low Priority Tasks

| Task ID | Task | Owner | Status | Acceptance Criteria |
|---|---|---|---|---|
| LP-001 | Add screenshot references to screen rows | Unassigned | NOT_STARTED | QA screenshot paths linked where available. |
| LP-002 | Normalize terminology between Prankster Lab and Prankstar | Unassigned | PARTIAL | Product naming documented consistently without breaking package names. |
| LP-003 | Add exact hidden menu inventory | Unassigned | UNKNOWN | Every discovered hidden menu has trigger/risk/owner. |

## Current Risks

| Risk ID | Risk | Probability | Impact | Mitigation | Status |
|---|---|---|---|---|---|
| RISK-001 | AI changes code without exact file ownership context | Medium | High | Use `FILE_MAP.md` and file scope contract. | OPEN |
| RISK-002 | Root assets mistaken for packaged Android assets | High | High | Use `ASSET_REGISTRY.md` and path verification. | OPEN |
| RISK-003 | Reactor/Home regressions go unnoticed without device QA | Medium | High | Runtime smoke and screenshots. | OPEN |
| RISK-004 | Bot unsafe/fake behavior introduced | Medium | High | Use `PRANKSTAR_ROBOT.md` safety rules and tests. | OPEN |
| RISK-005 | Build sync cannot be verified in current checkout | Medium | Medium | Configure remote/SDK environment; document blockers. | OPEN |

## Recent Changes

| Date | Change | Files | Commit/Evidence | Notes |
|---|---|---|---|---|
| 2026-06-14 | Added base AI workflow documentation system | Existing workflow docs | Prior commit in current branch | Documentation-only. |
| 2026-06-14 | Added missing project-control registries and living state doc | `SCREEN_REGISTRY.md`, `FILE_MAP.md`, `REACTOR_REGISTRY.md`, `PRANKSTAR_ROBOT.md`, `CURRENT_PROJECT_STATE.md` | Current working change | Documentation-only. |

## Pending Work

| Work Item | Dependency | Status | Next Owner Action |
|---|---|---|---|
| Replace UNKNOWN statuses with inspected facts | Code/resource audit | IN_PROGRESS | Inspect exact files with `rg --files` and update rows. |
| Runtime QA | Device/emulator | UNKNOWN | Install/run app and capture screenshots/logs. |
| Build verification | Valid Android SDK | UNKNOWN | Run environment script before Gradle. |
| Asset verification | Validator/resources | UNKNOWN | Run catalog and resource checks. |

## Next Session Goals

1. Read `AGENTS.md`, `PROJECT_CONTEXT.md`, `AI_RULES.md`, and this file.
2. Run `git status --short --branch` and attempt upstream sync if remote exists.
3. Pick one focused purpose: code audit, asset audit, build verification, runtime QA, or one feature/bug.
4. Replace broad UNKNOWN registry rows with exact inspected file paths and evidence.
5. Update `SESSION_HANDOFF.md` before ending.

## Blockers

| Blocker ID | Blocker | Status | Impact | Resolution Path |
|---|---|---|---|---|
| BLK-001 | No `origin` remote in current checkout | BLOCKED | Required upstream sync cannot complete here | Configure `origin` or perform rebase in proper clone. |
| BLK-002 | Android SDK/device status not verified in this session | UNKNOWN | Build/runtime claims cannot be made | Run environment script and device checks in capable environment. |

## Build Verification Status

| Verification | Status | Last Attempt | Result | Required Before |
|---|---|---|---|---|
| Upstream fetch/rebase | BLOCKED | 2026-06-14 | Failed because `origin` remote is missing | Merge confidence. |
| Android environment script | UNKNOWN | Not run | Not required for docs-only change | Gradle build. |
| Gradle build | UNKNOWN | Not run | Not required for docs-only change | Code/resource release claims. |
| Device install/launch | UNKNOWN | Not run | Not required for docs-only change | Runtime release claims. |

## Release Readiness Status

| Area | Status | Release Gate |
|---|---|---|
| Build | UNKNOWN | Clean build in verified SDK environment. |
| Runtime | UNKNOWN | Device/emulator smoke passes. |
| UI | PARTIAL | Core screens verified with screenshots. |
| Navigation | PARTIAL | Dock/routes/back behavior verified. |
| Audio | PARTIAL | Catalog validation and playback verified. |
| Reactor | PARTIAL | Deploy/stop/category/mode verified. |
| Robot | PARTIAL | Safety/actions/assets verified. |
| Assets | PARTIAL | Registry and package paths verified. |
| Bugs | UNKNOWN | S0/S1 resolved or explicitly deferred. |
| Store/policy | PARTIAL | Privacy/safety copy reviewed. |

## Session Update Procedure

1. At session start, update current date, branch, and immediate focus if changed.
2. Record environment limitations discovered during inspection.
3. Update only statuses supported by evidence.
4. Link evidence to commands, screenshots, logs, commits, or code paths.
5. At session end, update Recent Changes, Pending Work, Next Session Goals, and `SESSION_HANDOFF.md`.

## Status Update Procedure

| Status Change | Required Evidence |
|---|---|
| UNKNOWN to PARTIAL | Code/resource/docs inspection found some implementation. |
| PARTIAL to IN_PROGRESS | Active task/owner and file scope identified. |
| IN_PROGRESS to COMPLETE | Acceptance criteria met and validation evidence recorded. |
| Any status to BLOCKED | Blocker ID, impact, and resolution path recorded. |
| COMPLETE to PARTIAL/BLOCKED | Regression or missing validation identified and bug/task created. |

## AI Startup Procedure

1. Read `AGENTS.md` and nested instructions for touched paths.
2. Attempt upstream sync if remote exists.
3. Read `PROJECT_CONTEXT.md`, `AI_RULES.md`, `WORKFLOW.md`, `SESSION_HANDOFF.md`, and this file.
4. Identify task scope and exact files to modify.
5. Check `SCREEN_REGISTRY.md`, `FILE_MAP.md`, `REACTOR_REGISTRY.md`, `PRANKSTAR_ROBOT.md`, and `ASSET_REGISTRY.md` as relevant.
6. Proceed only with a single-purpose change.

## AI Shutdown Procedure

1. Run appropriate validation or document why it was not applicable.
2. Update this file with evidence-backed status changes.
3. Update `SESSION_HANDOFF.md` with completed/remaining work.
4. Ensure `git status --short --branch` only shows intended changes before commit.
5. Commit focused changes and create PR record when required.

## Session Handoff Procedure

- Summarize current focus and exact files changed in `SESSION_HANDOFF.md`.
- Record active blockers, bugs, tasks, and validation gaps.
- Identify the next safest action for the next AI session.
- Do not leave undocumented UNKNOWN-to-COMPLETE transitions.

## Recovery Procedure

1. If context is lost, read this file first after `AGENTS.md`.
2. Check `git status --short --branch` and recent commits.
3. Compare changed files against the file scope contract.
4. Use `FILE_MAP.md` to classify risk and dependencies.
5. Use `SCREEN_REGISTRY.md` and `REACTOR_REGISTRY.md` for UI/reactor recovery.
6. Use `BUG_TRACKER.md` for failures and `RELEASE_CHECKLIST.md` for release blockers.
7. Revert only the smallest unsafe change or create a focused fix.
