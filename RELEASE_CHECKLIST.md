# Prankstar Release Checklist

## Release Metadata

| Field | Value |
|---|---|
| Release version | TBD |
| Commit/branch | TBD |
| Build variant | TBD |
| Release owner | TBD |
| Date | TBD |

## Build Verification

| Check | Command/Action | Pass Criteria | Result |
|---|---|---|---|
| Android environment check | `chmod +x scripts/build-android-debug.sh && ./scripts/build-android-debug.sh` | Valid SDK and debug build process completes or reports actionable environment issue. | TBD |
| Clean debug build | After environment verification, Gradle clean assembleDebug command approved for repo | Build succeeds. | TBD |
| Dependency audit | Review Gradle/npm/dependency reports where applicable | Known vulnerabilities triaged. | TBD |
| No unintended build-file changes | `git diff -- build files` | No unscoped Gradle/manifest edits. | TBD |

## Runtime Verification

| Check | Action | Pass Criteria | Result |
|---|---|---|---|
| Cold launch | Install and open app | No crash; boot transitions to Home/Core. | TBD |
| Warm launch | Reopen from recents | State resumes safely. | TBD |
| Background/foreground | Background during audio/video then return | No leaked playback unless intended; UI stable. | TBD |
| Rotation/config change | Rotate if supported or trigger config changes | No crash or corrupted state. | TBD |

## UI Verification

| Check | Action | Pass Criteria | Result |
|---|---|---|---|
| Home/Core visual | Inspect compact phone screenshot | Header/reactor/bot/dock readable and not overlapping. | TBD |
| Header system | Visit primary screens | Headers preserve aspect and text. | TBD |
| Dock | Navigate all dock items | One dock, active state correct, routes work. | TBD |
| Compact width | Test 360dp-class width | Core flows usable. | TBD |
| Accessibility | Check labels/touch targets | Primary controls named and usable. | TBD |
| Reduced animation | Enable preference and revisit animated screens | New/modified animations reduce appropriately. | TBD |

## Navigation Verification

| Route | Entry Points | Pass Criteria | Result |
|---|---|---|---|
| Home/Core | Launch, dock, back | Stable and interactive. | TBD |
| Sound Stash | Dock/action/bot | Opens and plays sounds. | TBD |
| Voice Lab/Jokes | Dock/action/bot | Opens, handles text states safely. | TBD |
| Sound Forge | Dock/action | Opens and manages generated sounds. | TBD |
| Settings/System | Dock/system action | Opens and persists settings. | TBD |
| Timer/Randomizer | Menus/cards | Start/cancel behavior safe. | TBD |

## Asset Verification

| Check | Command/Action | Pass Criteria | Result |
|---|---|---|---|
| Asset registry current | Review `ASSET_REGISTRY.md` | New/wired assets documented. | TBD |
| Audio catalog | Run catalog validator if available | 0 missing, 0 corrupt, 0 duplicate IDs/paths. | TBD |
| Image resources | Inspect resource references | No missing drawables/mipmaps. | TBD |
| Video resources | Runtime inspect video screens | Videos loop or fallback safely. | TBD |
| Generated assets | Create/play/delete generated clip | Storage metadata remains valid. | TBD |

## Animation Verification

| Check | Action | Pass Criteria | Result |
|---|---|---|---|
| Reactor idle/play/stop | Interact with reactor | State transitions animate without jank/stuck state. | TBD |
| Header video | Observe header loops | No crash, no visible stretching/cropping. | TBD |
| Bot mood video | Trigger mood changes | Correct/fallback video displays safely. | TBD |
| Reduced animation | Toggle preference | Non-essential motion reduced. | TBD |

## Audio Verification

| Check | Action | Pass Criteria | Result |
|---|---|---|---|
| Play catalog sound | Play from Library | Real audible playback. | TBD |
| Reactor deploy | Tap reactor | Real catalog sound plays. | TBD |
| Stop All | Press stop while sound active | Playback stops immediately. | TBD |
| Corrupt/missing handling | Use validator or controlled missing case | User-safe error/fallback, no crash. | TBD |
| Generated audio | Play generated clip | Playback works or error is explicit. | TBD |

## Regression Verification

| Area | Pass Criteria | Result |
|---|---|---|
| Existing core screens | No route crashes. | TBD |
| Existing sound catalog | No missing/corrupt packaged audio. | TBD |
| Existing bot commands | Safe commands still work; unsafe commands refused. | TBD |
| Existing custom UI | Neon/header/dock/reactor systems preserved. | TBD |
| Existing docs/trackers | Updated for release state. | TBD |

## Final Release Verification

| Check | Pass Criteria | Result |
|---|---|---|
| Version metadata | Correct version code/name and release notes. | TBD |
| Store policy copy | Safety/privacy text reviewed. | TBD |
| Signing | Release signing configured outside public docs. | TBD |
| Final artifact | APK/AAB generated and archived. | TBD |
| Rollback plan | Previous stable artifact/commit identified. | TBD |
| Handoff | `SESSION_HANDOFF.md` updated with release status. | TBD |
