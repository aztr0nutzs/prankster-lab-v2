# Prankstar File Map

## Purpose

`FILE_MAP.md` tracks file ownership, responsibility, dependencies, assets, build impact, and risk. It is the project-control companion to `SCREEN_REGISTRY.md`, `FEATURE_TRACKER.md`, `ASSET_REGISTRY.md`, and `AI_RULES.md`.

## Status and Risk Legend

| Field | Values | Meaning |
|---|---|---|
| Status | NOT_STARTED, IN_PROGRESS, PARTIAL, COMPLETE, BLOCKED, UNKNOWN | Current documentation/implementation state. |
| Risk | Low, Medium, High, Critical | Probability and impact of regression when editing. |
| Build Impact | None, Docs Only, Resource, Kotlin/Java Compile, Manifest, Gradle/Dependency, Packaging, Runtime Only | Expected verification scope. |

## File Ownership Table Template

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| `path/to/file` | Describe what it does | Owning feature/subsystem | Linked code/resources/docs | APIs/libraries/state/assets | Direct asset paths | Impact category | Low/Medium/High/Critical | UNKNOWN | Evidence and cautions |

## Activities

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| `app/src/main/java/com/pranksterlab/**/MainActivity*` | App host, launch flow, navigation host, boot sequence if present | App shell/startup | Navigation files, theme/resources, boot components | Android lifecycle, Compose/navigation, startup state | Launcher/boot/header assets as wired | Kotlin/Java Compile, Runtime Only | Critical | UNKNOWN | Inspect exact file before editing; avoid manifest changes unless scoped. |

## Fragments

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| `app/src/main/java/com/pranksterlab/**/*Fragment*` | Legacy/hybrid screen surfaces if present | Screen ownership varies | Activity/adapter/layout files | Fragment lifecycle, navigation | Screen-specific | Kotlin/Java Compile, Runtime Only | High | UNKNOWN | Add each discovered fragment here before editing. |

## Adapters

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| `app/src/main/java/com/pranksterlab/**/*Adapter*` | List/binding logic if RecyclerView or legacy UI exists | Lists/catalog/generated clips | Models, view holders, layouts | Android views or Compose interop | Row icons/audio metadata | Kotlin/Java Compile | Medium | UNKNOWN | Verify no stale item IDs or playback references. |

## Managers

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| `app/src/main/java/com/pranksterlab/**/*Manager*` | Cross-feature coordination such as audio, assets, preferences, generation | Subsystem-specific | Controllers, repositories, services | Coroutine/lifecycle/storage/media APIs | Varies | Kotlin/Java Compile, Runtime Only | High | UNKNOWN | Managers often have broad side effects; require regression checks. |

## Controllers

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| `app/src/main/java/com/pranksterlab/**/*Controller*` | Event orchestration and command handling | Bot/reactor/playback/features | UI state, repositories, models | Domain rules, media, navigation | Varies | Kotlin/Java Compile, Runtime Only | High | UNKNOWN | Preserve existing command semantics. |
| `app/src/main/java/com/pranksterlab/core/bot/**/*Controller*` | Bot command orchestration if present | Prankstar Robot | Bot parser/intent/response files | Sound repository, safety, navigation | Bot assets indirectly | Kotlin/Java Compile, Runtime Only | High | UNKNOWN | Must remain deterministic and safe by default. |

## Services

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| `app/src/main/java/com/pranksterlab/**/*Service*` | Background/audio/generation service if present | Runtime services | Manifest if exported/declared, controllers | Android service lifecycle, permissions | Audio/video/generated files | Manifest if changed, Kotlin/Java Compile | Critical | UNKNOWN | Do not edit manifest without explicit scope. |

## Utilities

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| `app/src/main/java/com/pranksterlab/**/*Util*` | Shared helpers | Cross-cutting | Call sites | Kotlin stdlib/Android APIs | Varies | Kotlin/Java Compile | Medium | UNKNOWN | Verify all call sites for behavior changes. |
| `tools/**` | Validation/build/support scripts | QA/release | Docs, catalog files | Python/Node/shell as applicable | Audio/assets | Tooling Only | Medium | UNKNOWN | Script output should be captured in QA docs. |

## Models

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| `app/src/main/java/com/pranksterlab/**/*Model*` | Data shape for UI/domain/storage | Feature/domain | Repositories, ViewModels, UI | Serialization/persistence if used | Asset IDs/paths indirectly | Kotlin/Java Compile, Runtime Only | High | UNKNOWN | Backward compatibility required for persisted data. |
| `app/src/main/java/com/pranksterlab/**/PrankSound*` | Sound metadata if present | Audio catalog | Sound repository, UI rows, playback | Asset paths, categories | Audio assets | Kotlin/Java Compile, Runtime Only | Critical | UNKNOWN | Changes can break playback/catalog validation. |

## Repositories

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| `app/src/main/java/com/pranksterlab/**/*Repository*` | Data access for sounds, settings, generated clips, bot recommendations | Data/domain | Models, controllers, ViewModels | Storage, assets, DataStore | Audio/generated assets | Kotlin/Java Compile, Runtime Only | High | UNKNOWN | Validate missing/corrupt asset handling. |

## ViewModels

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| `app/src/main/java/com/pranksterlab/**/*ViewModel*` | UI state and event handling | Screen/feature | Screens, repositories, models | Coroutines/Flow/lifecycle | Indirect | Kotlin/Java Compile, Runtime Only | High | UNKNOWN | Verify lifecycle, cancellation, nullability, and saved state. |

## Custom Views and Compose Components

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| `app/src/main/java/com/pranksterlab/components/**` | Reusable UI components such as headers, dock, bot, reactor | UI system | Screens, resources, theme | Compose, AndroidView/media if used | Headers, dock, reactor, bot assets | Kotlin/Java Compile, Runtime Only | High | UNKNOWN | Preserve premium custom UI. |
| `app/src/main/java/com/pranksterlab/**/reactor/**` | Reactor UI/state/components | Reactor system | Home/Core, playback, navigation | Compose animation, media, state | Reactor assets | Kotlin/Java Compile, Runtime Only | Critical | UNKNOWN | Regression test deploy/stop/category. |

## Animations

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| Code-driven Compose animations | Pulses, glows, waveforms, transitions | UI/reactor/bot | Components/screens | Compose animation APIs | Optional visuals/video | Kotlin/Java Compile, Runtime Only | Medium | UNKNOWN | Respect reduced-animation preference when modified. |
| `app/src/main/res/anim*`, `app/src/main/res/drawable*` animation assets | XML/drawable animation if present | UI resources | Components/themes | Android resources | Drawable assets | Resource | Medium | UNKNOWN | Resource name conflicts can break build. |

## Assets

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| `app/src/main/assets/**` | Packaged runtime assets | Asset system/audio/web | Repositories, WebView, validators | Android asset manager, WebView | Audio/video/web | Packaging, Runtime Only | High | UNKNOWN | Never assume root assets are packaged. |
| `app/src/main/res/raw/**` | Android raw media resources | Video/audio resources | Resource references | Android resources, media players | MP4/audio | Resource, Packaging | High | UNKNOWN | File names must be Android-resource safe. |
| `app/src/main/res/drawable/**` | Images/vector/drawable resources | UI assets | Components/screens | Android resources | PNG/vector/drawable | Resource | Medium | UNKNOWN | Avoid cropping baked text. |
| `public/**` | Source/public assets or staging | Asset intake | Copy scripts/docs | File system only unless packaged | Audio/images | None unless copied | Medium | UNKNOWN | Not packaged by default unless build config says so. |

## Gradle Files

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| `settings.gradle.kts` | Project module/plugin management | Build system | Root/module Gradle files | Gradle, plugins | None | Gradle/Dependency | Critical | UNKNOWN | Conflict magnet; build-only PR required. |
| `build.gradle.kts` | Root build configuration | Build system | Settings/module Gradle | Gradle, plugins | None | Gradle/Dependency | Critical | UNKNOWN | Do not edit in feature/UI PRs. |
| `app/build.gradle.kts` or module equivalent | Android app build config | Build system | Manifest/resources/source | AGP, Kotlin, dependencies | Packaging config | Gradle/Dependency, Packaging | Critical | UNKNOWN | Build-only PR unless explicitly scoped. |
| `gradle.properties` | Build properties | Build system | Gradle wrapper/build files | JVM/Android properties | None | Gradle/Dependency | High | UNKNOWN | Validate compatibility; avoid version roulette. |
| `gradle/wrapper/gradle-wrapper.properties` | Gradle distribution | Build system | CI/local Gradle | Gradle version | None | Gradle/Dependency | Critical | UNKNOWN | Must use known-compatible AGP/Kotlin pairs. |

## Manifest Files

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| `app/src/main/AndroidManifest.xml` | App components, permissions, launcher, services | Android platform integration | Activities/services/build config | Android framework, permissions | Launcher icon/theme refs | Manifest, Packaging | Critical | UNKNOWN | Conflict magnet; smallest diff only when explicitly scoped. |

## Navigation Files

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| `app/src/main/java/com/pranksterlab/**/*Nav*` | Route declarations and navigation wiring | Navigation | Screens, dock, bot actions | Compose/navigation if used | None directly | Kotlin/Java Compile, Runtime Only | High | UNKNOWN | Validate route entry/back behavior. |

## Resources and Layouts

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| `app/src/main/res/values/**` | Themes, colors, strings, dimensions | UI resources | Screens/components/manifest | Android resources | Resource references | Resource | High | UNKNOWN | String/theme changes can affect whole app. |
| `app/src/main/res/layout/**` | XML layouts if present | Legacy/hybrid UI | Activities/fragments/adapters | Android views | Drawable/string refs | Resource, Runtime Only | High | UNKNOWN | Inspect all binding/call sites. |
| `app/src/main/res/mipmap-*/**` | Launcher icons | App identity | Manifest/launcher | Android resources | Icon images | Resource, Packaging | Medium | UNKNOWN | Avoid unless launcher update is scoped. |

## Audio Assets

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| `app/src/main/assets/sounds/**` | Packaged prank sound catalog | Audio catalog | Sound repository/catalog validator | Android assets, media playback | MP3/OGG/WAV | Packaging, Runtime Only | Critical | UNKNOWN | Validate headers, paths, duplicate IDs. |
| `public/sounds/**` | Source/staging sound assets | Asset intake | Copy scripts/docs | File system | MP3/OGG/WAV | None unless packaged | Medium | UNKNOWN | Not automatically playable unless packaged/cataloged. |

## Video Assets

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| `app/src/main/assets/prankstar/assets/*.mp4` | Web/native bundled reactor/header videos | Reactor/Home/WebView | WebView/native components | Asset manager/WebView/media | MP4 | Packaging, Runtime Only | High | UNKNOWN | Confirm exact names before references. |
| `app/src/main/res/raw/*.mp4` | Raw Android video resources | Bot/header/background | Video components | Android resources, ExoPlayer/MediaPlayer | MP4 | Resource, Packaging | High | UNKNOWN | Large-file and resource-name risk. |

## Image Assets

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| Root `*.png` files | Source/user-provided images | Asset intake | Registry/docs/copy targets | File system | PNG | None unless packaged | Medium | UNKNOWN | Root files are not packaged by default. |
| `app/src/main/res/drawable/*.png` | Packaged UI images | UI assets | Components/screens | Android resources | PNG | Resource, Packaging | Medium | UNKNOWN | Validate density/aspect/readability. |

## Documentation

| File/Path | Purpose | Responsibility | Related Files | Dependencies | Assets Used | Build Impact | Risk | Status | Notes |
|---|---|---|---|---|---|---|---|---|---|
| `PROJECT_CONTEXT.md` | Primary AI context | Project control | All workflow docs | None | None | Docs Only | Low | COMPLETE | Read at session start. |
| `FEATURE_TRACKER.md` | Feature/screen/interaction tracker | Project control | Screen/current state docs | None | None | Docs Only | Low | COMPLETE | Update after feature status changes. |
| `SCREEN_REGISTRY.md` | Screen inventory | Project control | Feature/UI/current state docs | None | None | Docs Only | Low | COMPLETE | This file. |
| `FILE_MAP.md` | File ownership/dependency tracking | Project control | All docs/code | None | None | Docs Only | Low | COMPLETE | This file. |
| `CURRENT_PROJECT_STATE.md` | Living truth source | Project control | All trackers/handoff | None | None | Docs Only | Low | COMPLETE | Update each session. |

## Dependency Tracking Matrix

| Dependency | Primary Files/Paths | Dependent Systems | Risk | Verification |
|---|---|---|---|---|
| Android SDK | Build scripts, local environment | All Android builds | Critical | Run environment script before Gradle. |
| Gradle/AGP/Kotlin | Gradle files, wrapper | Build/sync/package | Critical | Build-only PR, known-compatible versions. |
| Jetpack Compose | Components/screens/ViewModels | UI | High | Compile, route smoke, visual QA. |
| MediaPlayer/Media3/ExoPlayer | Playback/video components | Audio, reactor, bot, headers | High | Play/stop/release tests. |
| DataStore/preferences | Settings, persistence, reactor/bot preferences | Settings, reduced animation, selected reactor | Medium | Toggle/persist/relaunch checks. |
| Sound catalog assets | `assets/sounds`, repositories | Library, reactor, bot recommendations | Critical | Catalog validator and runtime playback. |
| WebView bridge/assets | WebView host/assets | Stable Home/Web hybrid route | High | Bridge route/action QA. |
| Generated file storage | Forge/Stash/generated clip code | Generated audio | High | Create/play/delete/missing-file tests. |

## Critical File Registry

| File/Path Pattern | Why Critical | Required Safeguard |
|---|---|---|
| Activity/launcher files | Startup and app host failures block all users | Cold launch and back-stack QA. |
| Navigation files | Route regressions break screens and dock | Test all dock/action routes touched. |
| Playback controller/repository files | Audio is core app value | Play/stop/corrupt/missing validation. |
| Reactor components/state | Home/Core primary interaction | Deploy/stop/category/regression QA. |
| Bot parser/controller/safety files | Safety and command correctness | Safe/refusal/action tests. |
| Sound catalog files | Missing/corrupt audio breaks release | Validator and duplicate checks. |

## High-Risk File Registry

| File/Path Pattern | Risk | Rule |
|---|---|---|
| `app/src/main/assets/web-ui/**` | Conflict-magnet WebView bundle | Do not edit unless explicitly scoped. |
| `app/src/main/assets/prankstar/assets/**` | Large media and bridge references | Verify packaging and runtime. |
| `app/src/main/res/raw/**` | Resource packaging and large media | Verify resource names/build. |
| `app/src/main/res/values/**` | Global UI/build effects | Inspect references before edits. |
| `tools/**` validators | Release confidence | Keep output stable and documented. |

## Build-Critical File Registry

| File | Build Impact | Change Policy |
|---|---|---|
| `settings.gradle.kts` | Module/plugin resolution | Build-only PR. |
| `build.gradle.kts` | Root build configuration | Build-only PR. |
| `app/build.gradle.kts` or module equivalent | App compile/package/dependencies | Build-only PR unless explicitly approved. |
| `gradle/wrapper/gradle-wrapper.properties` | Gradle runtime version | Build-only PR with compatibility notes. |
| `gradle.properties` | JVM/Android build behavior | Build-only PR or explicit build task. |
| `AndroidManifest.xml` | Component/package/permission/resource integration | Explicit scope only; smallest diff. |

## Rollback Guidance

| Change Type | Rollback Method | Post-Rollback Verification |
|---|---|---|
| Documentation-only | `git revert <commit>` or restore files | File presence/readability check. |
| Kotlin/Java | Revert commit or restore touched files | Build and targeted runtime QA. |
| Resources/assets | Revert files and references together | Resource build and asset/runtime validation. |
| Gradle/build | Revert entire build PR | Clean sync/build with known environment. |
| Manifest | Revert manifest and dependent code | Install/launch/component QA. |

## AI Analysis Procedure

1. Read `PROJECT_CONTEXT.md`, `AI_RULES.md`, `WORKFLOW.md`, `CURRENT_PROJECT_STATE.md`, and this file.
2. Identify exact file/path pattern in this map.
3. Determine owner, related files, dependencies, assets, build impact, and risk.
4. Inspect all related files before editing.
5. List exact files to modify.
6. If file is critical/high-risk/build-critical, document why the change is necessary and how it will be verified.
7. Update this file if ownership, dependency, or risk information changes.

## File Change Verification Procedure

| Step | Action | Success Criteria |
|---|---|---|
| 1 | Review `git diff --name-only` | Only approved files changed. |
| 2 | Classify build impact | Correct validation scope selected. |
| 3 | Check related files | No broken references or stale docs. |
| 4 | Run relevant static/tool checks | Checks pass or limitation documented. |
| 5 | For code/resources, verify Android environment before Gradle | SDK/build prerequisites confirmed. |
| 6 | Update trackers | `CURRENT_PROJECT_STATE.md`, `SESSION_HANDOFF.md`, and relevant registry updated. |

## Maintenance Procedure

- Add new code/resource/doc files to the correct section when introduced.
- Update risk level after major architecture changes or repeated regressions.
- Keep build-critical file policies aligned with `AI_RULES.md`.
- Add exact file paths after code inspection replaces broad glob rows.
- Do not mark a high-risk file COMPLETE without validation evidence.
