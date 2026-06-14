# Prankstar Screen Registry

## Purpose

`SCREEN_REGISTRY.md` is the master inventory for every Prankstar user-facing surface and navigation destination. It complements `FEATURE_TRACKER.md`, `UI_SPECIFICATION.md`, `WORKFLOW.md`, and `CURRENT_PROJECT_STATE.md` by tracking screen ownership, dependencies, assets, entry/exit paths, risk, and verification status.

## Status Legend

| Status | Meaning |
|---|---|
| NOT_STARTED | No confirmed screen implementation exists. |
| IN_PROGRESS | Active implementation work is underway. |
| PARTIAL | Screen or surface exists but requirements, assets, QA, or wiring are incomplete. |
| COMPLETE | Screen is implemented and wired with expected behavior. |
| BLOCKED | Cannot proceed until an external issue is resolved. |
| UNKNOWN | Existing state has not been inspected in the current session. |

## Screen Type Legend

| Type | Definition |
|---|---|
| Activity | Android activity entry point or host. |
| Screen | Route-level Compose/native screen or WebView route. |
| Fragment | Legacy or hybrid Android fragment, if present. |
| Dialog | Modal confirmation, picker, error, or warning surface. |
| Bottom Sheet | Modal or persistent sheet anchored from bottom. |
| Popup | Lightweight transient menu, tooltip, dropdown, or contextual surface. |
| Overlay | Non-route layer over a screen, including boot, scrims, effects, video layers, or status HUD. |
| Hidden Menu | Advanced, debug, long-press, or undiscoverable user surface. |
| Panel | Reusable or screen-local section such as bot panel, reactor panel, dock, header, drawer, card, or expandable area. |

## Master Screen Inventory

| ID | Name | Type | File Location | Entry Points | Exit Paths | Dependencies | Assets Used | Navigation Relationships | Feature Ownership | Status | Known Issues | Notes |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| SCR-001 | App Host / Main Activity | Activity | Inspect `app/src/main/java/com/pranksterlab/` activity files before editing | Launcher | Boot/Home route, system back, app close | Android lifecycle, navigation host, boot flow | Launcher icon, boot/header/video assets as wired | Owns app startup and route host | UNKNOWN | Requires current code inspection before changes | Treat as high risk; avoid manifest/build changes unless explicitly scoped. |
| SCR-002 | Boot Sequence | Overlay | Inspect MainActivity/startup components | App launch | Home/Core | Startup state, media/video/image assets | Boot video/image assets if wired | Launch-only transition to Home/Core | Boot system | PARTIAL | Device QA may be unavailable | Must never trap users on a black/stuck frame. |
| SCR-003 | Home/Core Reactor | Screen | Inspect Home/Core route and reactor components | Boot, dock Home/Core, back stack | Dock routes, reactor action strip, system back | Reactor, playback, bot, header, dock, navigation | Reactor videos/images, header video, bot assets, dock art | Central hub for Stash, Jokes, Forge, Settings/System | Reactor/Home | PARTIAL | Native/WebView divergence risk | Preserve premium UI and reactor as focal point. |
| SCR-004 | Sound Stash / Library | Screen | Inspect library/sound stash screen files | Dock Stash, bot command, reactor action | Home/Core, playback controls, details if present | Sound repository, catalog, MediaPlayer/Media3, search/filter | Sound assets, category icons | Playback destination from dock and bot | Audio library | PARTIAL | Missing/corrupt assets break core value | Must use real catalog data only. |
| SCR-005 | Voice Lab / Jokes | Screen | Inspect Voice Lab/Joke Generator files | Dock Jokes, bot handoff, reactor action | Home/Core, generated clip flow, share/save if present | Bot bridge, text generation, safety filters, audio generation | Header assets, bot assets, generated audio | Receives drafts from bot and links to generated audio | Voice/Joke system | PARTIAL | Unsafe prompts and empty/error states need QA | No real-person impersonation or hidden sending. |
| SCR-006 | Twak-Attacks / Tweakographic Narrator | Panel/Screen | Inspect Voice Lab feature-specific files | Voice Lab section/menu | Voice Lab, generated clip controls | Safety filters, narrator templates, bot mood mapping | `twak_attack_header`, `twakbot_*` if packaged | Feature-local surface under Voice Lab | Twak narrator | PARTIAL | Asset packaging must be verified | Keep scoped unless product explicitly promotes it to a route. |
| SCR-007 | Sound Forge | Screen | Inspect Forge route/files | Dock Forge, reactor action | Home/Core, generated clip list | Generation engine, storage, playback | Generated audio, forge visuals | Creates/manages generated sounds | Forge | PARTIAL | Generated-file missing state risk | Must handle storage errors explicitly. |
| SCR-008 | Settings / System | Screen | Inspect settings/system files | Dock/System action, menu | Home/Core, back | DataStore/preferences, reduced animation | Settings icons/header assets | Controls app preferences and diagnostics | Settings | PARTIAL | Reduced animation may not be global | Must persist and reflect preference state. |
| SCR-009 | Timer | Screen/Panel | Inspect timer feature files | Menus/cards/bot if wired | Home/Core, stop/cancel | Scheduler, playback, lifecycle | Timer icons/sounds | Delayed prank flow | Timer | UNKNOWN | Background lifecycle risk | Verify cancel on stop/background. |
| SCR-010 | Randomizer | Screen/Panel | Inspect randomizer feature files | Menus/cards/bot if wired | Home/Core, stop/cancel | Catalog filters, playback | Category assets/sounds | Random sound deployment | Randomizer | UNKNOWN | Invalid asset selection risk | Must avoid missing/corrupt catalog entries. |
| SCR-011 | Prankstar Bot Panel | Panel | Inspect bot UI components | Home/Core, Voice Lab, feature panels | Bot action buttons/routes | Bot controller/parser, sound repository, safety | Bot videos/images/audio | Can navigate/play/recommend/handoff | Robot/Bot | PARTIAL | Mood asset fallback must be verified | Local deterministic by default. |
| SCR-012 | Bottom Dock | Panel | Inspect dock component | Global app shell except intentionally hidden routes | Destination routes | Navigation model, active route state | Dock image/icons | Primary navigation | Navigation/UI shell | PARTIAL | Duplicate dock risk on hybrid Home paths | Must not be replaced with generic tabs. |
| SCR-013 | Header System | Panel | Inspect header components | Primary screens | Screen content below | Drawable/raw/video resources | Screen-specific headers | Screen identity and branding | UI shell | PARTIAL | Cropped baked text risk | Use fit/aspect-safe rendering. |
| SCR-014 | Reactor Action Strip | Panel | Inspect reactor components | Home/Core | Stash, Jokes, Forge, Stop All | Navigation, playback stop | Button/icon assets | Secondary action navigation | Reactor/Home | PARTIAL | May be unreachable on compact devices | Must be scroll-safe and dock-safe. |
| SCR-015 | Audio Error / Missing Asset Dialog | Dialog | Inspect playback error handling | Playback failure | Dismiss/retry/stop | Playback controller, asset validator | Optional warning icon | Error recovery surface | Audio QA | UNKNOWN | Silent failures are forbidden | Add if absent when touching playback failure paths. |
| SCR-016 | Safety / Responsible Use Dialog | Dialog | Inspect onboarding/settings/safety files | First run, menu, unsafe bot prompt | Accept/dismiss/settings | Safety copy, persistence | App branding | Policy and user education | Safety | UNKNOWN | Release policy risk if absent | Required before public release decisions. |
| SCR-017 | Expandable Feature Panels | Panel | Inspect Home/Voice/Settings panels | Screen sections | Collapse/expand, nested actions | Local UI state | Header/card assets | Progressive disclosure | UI shell | UNKNOWN | State loss or inaccessible hidden controls | Use clear labels and preserve state if needed. |
| SCR-018 | Hidden/Advanced Menus | Hidden Menu | Inspect long-press/debug/admin code paths | Gestures, debug flags, secret taps | Dismiss/back | Feature flags, settings, diagnostics | Optional debug icons | Non-primary tools | Advanced/system | UNKNOWN | Hidden behavior can regress unnoticed | Document each discovered menu before modification. |
| SCR-019 | WebView Home / Stable V9 Surface | Screen | Inspect `app/src/main/assets/` and WebView host files before editing | Home route if active | Bridge navigation | WebView bridge, Android interface, packaged web assets | HTML assets, reactor videos, header videos | Hybrid route to native destinations | Home/WebView | UNKNOWN | Bridge divergence and asset packaging risk | Conflict-magnet assets require explicit scope. |
| SCR-020 | Generated Clip Manager | Panel/Screen | Inspect Forge/Stash generated clip files | Forge/Stash | Play/delete/back | Storage metadata, playback | Generated audio | Manages user-generated sounds | Generated audio | UNKNOWN | Missing-file and stale metadata risk | Verify delete/play/list states together. |

## Activity Registry

| Activity ID | Activity Name | File Location | Responsibilities | Launch Mode/Entry | Dependencies | Risk | Status | Verification |
|---|---|---|---|---|---|---|---|---|
| ACT-001 | Main launcher activity | Inspect current app source before editing | App host, startup, route composition, boot transition | Launcher intent | Navigation host, app theme, startup assets | High | UNKNOWN | Cold launch, warm launch, back behavior, boot transition. |

## Fragment Registry

| Fragment ID | Fragment Name | File Location | Host | Entry Points | Dependencies | Status | Notes |
|---|---|---|---|---|---|---|---|
| FRG-001 | Fragment inventory pending inspection | Current project is expected to be Compose-first, but Java/Kotlin fragments must be inventoried if present | TBD | TBD | TBD | UNKNOWN | Add each discovered fragment before editing fragment-related behavior. |

## Dialog Registry

| Dialog ID | Dialog Name | Purpose | Entry Points | Exit Paths | Dependencies | Status | Acceptance Criteria |
|---|---|---|---|---|---|---|---|
| DLG-001 | Playback error dialog | Explain failed/corrupt/missing audio or video | Media failure | Dismiss/Retry/Stop | Playback controller, asset path | UNKNOWN | User receives actionable feedback; app does not crash. |
| DLG-002 | Safety warning dialog | Explain prohibited prank/voice behavior | Unsafe bot/generation request, onboarding, settings | Accept/Dismiss/Settings | Safety policy, persistence | UNKNOWN | Refusal is clear and redirects to safe actions. |
| DLG-003 | Delete generated clip confirmation | Prevent accidental deletion | Generated clip delete | Cancel/Delete | Generated clip metadata/storage | UNKNOWN | Confirm destructive action and update list state. |

## Bottom Sheet Registry

| Sheet ID | Name | Purpose | Entry Points | Dependencies | Status | Notes |
|---|---|---|---|---|---|---|
| SHT-001 | Sound filters/category sheet | Filter or select sounds/categories | Library, reactor category menu | Sound catalog/category model | UNKNOWN | Use if filter UI exists or is added. |
| SHT-002 | Reactor mode selector sheet | Select reactor variants/modes | Home/Core reactor controls | Reactor registry, persistence | UNKNOWN | Must not hide deploy/stop controls permanently. |
| SHT-003 | Generated clip actions sheet | Play, rename, delete, export if supported | Generated clip row/menu | Storage metadata, playback | UNKNOWN | Must handle missing files. |

## Popup Registry

| Popup ID | Name | Purpose | Entry Points | Dismiss Behavior | Status | Notes |
|---|---|---|---|---|---|---|
| POP-001 | Overflow action popup | Secondary actions for screen/panel | More button/long press | Outside tap/back/action | UNKNOWN | Keep actions labeled and accessible. |
| POP-002 | Tooltip/help popup | Explain reactor/bot controls | Help icon/first use | Timeout/outside tap | UNKNOWN | Do not block core controls. |
| POP-003 | Category quick popup | Fast category switch | Reactor/category control | Select/outside tap/back | UNKNOWN | Must update selected state visibly. |

## Overlay Registry

| Overlay ID | Name | Purpose | Entry Points | Dependencies | Status | Regression Risk |
|---|---|---|---|---|---|---|
| OVR-001 | Boot overlay | Startup animation/transition | App launch | Startup assets, lifecycle | PARTIAL | Stuck/black screen. |
| OVR-002 | Video background overlay | Ambient Home/Core motion | Home/Core | ExoPlayer/Media3, raw/assets video | PARTIAL | Tap blocking or jank. |
| OVR-003 | Scanline/glow overlay | Cyberpunk HUD effect | Premium screens | Compose drawing/animation | PARTIAL | Reduced readability or performance. |
| OVR-004 | Loading overlay | Generation/playback wait state | Voice/Forge/Library | Async state | UNKNOWN | Blocking controls without escape. |

## Hidden Menu Registry

| Menu ID | Name | Trigger | Purpose | Dependencies | Status | Safety Rule |
|---|---|---|---|---|---|---|
| HME-001 | Advanced/system diagnostics | TBD after inspection | Debugging and QA | Settings/logging | UNKNOWN | Must not expose secrets or destructive actions without confirmation. |
| HME-002 | Reactor advanced selector | TBD after inspection | Reactor variants or tuning | Reactor registry/persistence | UNKNOWN | Must not break default reactor path. |
| HME-003 | Audio validation tools | TBD after inspection | Catalog diagnostics | Sound repository/validator | UNKNOWN | Must not ship unsafe debug affordances unintentionally. |

## Panel Registry

| Panel ID | Name | Parent Screen | Purpose | Dependencies | Assets | Status | Notes |
|---|---|---|---|---|---|---|---|
| PNL-001 | Reactor core panel | Home/Core | Deploy/stop/category reactor interaction | Playback, category state | Reactor assets | PARTIAL | Highest visual priority on Home/Core. |
| PNL-002 | Prankstar Bot panel | Home/Core, Voice Lab | Assistant state, commands, suggestions | Bot controller, safety, navigation | Robot assets | PARTIAL | Must not fake actions. |
| PNL-003 | Bottom dock panel | App shell/Home hybrid | Primary navigation | Route state | Dock assets | PARTIAL | One instance only. |
| PNL-004 | Header panel | Primary screens | Branding and screen identity | Header assets | Header assets | PARTIAL | Avoid baked-text cropping. |
| PNL-005 | Sound list/category panel | Sound Stash | Browse and play sounds | Sound catalog, playback | Audio/category assets | PARTIAL | Validate active row state. |
| PNL-006 | Generated clip panel | Forge/Stash | Manage generated audio | Storage metadata | Generated audio | UNKNOWN | Must show missing-file state. |
| PNL-007 | Settings preference panel | Settings | Toggle preferences | DataStore/settings | Icons | UNKNOWN | Must persist state. |

## Navigation Flow Mapping

| Flow ID | Source | Trigger | Destination | Back Behavior | Data Passed | Validation |
|---|---|---|---|---|---|---|
| NAV-001 | Boot overlay | Boot complete | Home/Core | App exits/back stack starts at Home | None | Cold launch reaches Home. |
| NAV-002 | Home/Core | Dock Stash | Sound Stash | Back/dock Home returns Home | Optional category/filter | Library renders and plays sound. |
| NAV-003 | Home/Core | Dock Jokes | Voice Lab/Jokes | Back/dock Home returns Home | Optional bot draft | Draft state handled safely. |
| NAV-004 | Home/Core | Dock Forge | Sound Forge | Back/dock Home returns Home | Optional generated clip context | Forge renders and handles empty state. |
| NAV-005 | Home/Core | Settings/System action | Settings/System | Back returns Home or previous route | None/preference context | Preferences persist. |
| NAV-006 | Bot panel | Play recommendation | Current route or Library | Remain stable | Sound ID/path | Real sound plays or safe error. |
| NAV-007 | Bot panel | Voice Lab handoff | Voice Lab/Jokes | Back returns previous route/Home | Draft text/template | Draft visible and editable. |
| NAV-008 | Reactor action strip | Stop All | Current screen | Stay on current route | Stop command | All active audio stops. |
| NAV-009 | Generated clip panel | Delete confirmation | Dialog/sheet | Return to clip list | Clip ID/path | List updates after deletion/cancel. |

## Screen Acceptance Checklist

| Check | Acceptance Criteria |
|---|---|
| Inventory | Screen is listed in Master Screen Inventory with ID, type, file location, entry points, exits, dependencies, assets, status, and owner. |
| Navigation | Entry and exit paths are documented and tested when changed. |
| State | Loading, empty, error, active, and disabled states are handled where applicable. |
| Assets | Every visual/audio/video dependency is listed or linked to `ASSET_REGISTRY.md`. |
| Accessibility | Primary controls have labels and usable touch targets. |
| Visual style | Premium cyberpunk UI is preserved on premium screens. |
| Regression | Related flows are checked before status moves to COMPLETE. |

## Screen Verification Checklist

| Verification | Command/Action | Success Criteria | Required When |
|---|---|---|---|
| File inspection | Inspect referenced files before editing | Exact files and dependencies known | Every screen task |
| Navigation smoke | Launch screen from each entry point | Correct screen appears; no duplicate dock; back works | Navigation/UI changes |
| Runtime interaction | Exercise primary controls | State updates and errors are visible | Interactive changes |
| Asset check | Verify referenced paths/resources | No missing packaged assets | Asset-visible changes |
| Accessibility review | Inspect labels/touch targets | Primary controls are accessible | UI changes |
| Build check | Run only after Android SDK verification | Build succeeds | Code/resource changes |

## Screen Regression Checklist

- Verify Home/Core still launches after boot.
- Verify dock active state and no duplicate dock.
- Verify reactor deploy/stop still works.
- Verify Sound Stash playback still works.
- Verify Voice Lab/Bot safe handoff still works if touched.
- Verify generated clip list handles empty/missing files if touched.
- Verify settings persistence if settings are touched.
- Verify no premium UI downgrade or generic replacement.

## AI Usage Instructions

1. Start with `PROJECT_CONTEXT.md`, `AI_RULES.md`, `WORKFLOW.md`, and this file.
2. Locate the screen ID for the task before editing.
3. If the surface is missing from this registry, add it before changing code.
4. List exact files to modify and do not exceed the list without scope expansion.
5. Check corresponding rows in `FEATURE_TRACKER.md`, `FILE_MAP.md`, `ASSET_REGISTRY.md`, and `CURRENT_PROJECT_STATE.md`.
6. Update status only with evidence.
7. Record remaining work in `SESSION_HANDOFF.md`.

## Maintenance Procedure

| Event | Required Update |
|---|---|
| New screen/panel/dialog added | Add row to the appropriate registry and Master Screen Inventory. |
| Route changed | Update Navigation Flow Mapping and affected entry/exit paths. |
| Asset added/changed | Update `ASSET_REGISTRY.md` and screen asset columns. |
| Feature ownership changed | Update Feature Ownership column and `FEATURE_TRACKER.md`. |
| Bug found | Add/update `BUG_TRACKER.md` and Known Issues column. |
| Screen verified | Record validation evidence and update status. |

## Future Screen Addition Procedure

1. Define screen purpose, owner, and acceptance criteria.
2. Add a proposed row to Master Screen Inventory with status NOT_STARTED or IN_PROGRESS.
3. Add rows to Activity/Fragment/Dialog/Sheet/Popup/Overlay/Panel registries as applicable.
4. Map entry points, exit paths, and back behavior.
5. List assets and dependencies; add new assets to `ASSET_REGISTRY.md` first.
6. Implement in a focused PR.
7. Validate navigation, UI, accessibility, assets, and regression paths.
8. Update `CURRENT_PROJECT_STATE.md`, `FEATURE_TRACKER.md`, and `SESSION_HANDOFF.md`.
